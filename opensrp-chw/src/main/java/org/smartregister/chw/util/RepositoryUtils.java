package org.smartregister.chw.util;

import static org.smartregister.repository.BaseRepository.TYPE_Synced;
import static org.smartregister.repository.BaseRepository.TYPE_Valid;

import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.domain.Event;
import org.smartregister.family.util.DBConstants;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.DatabaseMigrationUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public interface RepositoryUtils {

    String EVENT_ID = "id";
    String _ID = "_id";

    String ADD_MISSING_REPORTING_COLUMN = "ALTER TABLE 'indicator_queries' ADD COLUMN expected_indicators TEXT NULL;";
    String FAMILY_MEMBER_ADD_REASON_FOR_REGISTRATION = "ALTER TABLE 'ec_family_member' ADD COLUMN reasons_for_registration TEXT NULL;";
    String EC_REFERRAL_ADD_FP_METHOD_COLUMN = "ALTER TABLE 'ec_referral' ADD COLUMN fp_method_accepted_referral TEXT NULL;";

    String[] UPDATE_REPOSITORY_TYPES = {
            "UPDATE recurring_service_types SET service_group = 'woman' WHERE type = 'IPTp-SP';",
            "UPDATE recurring_service_types SET service_group = 'child' WHERE type != 'IPTp-SP';",
    };

    String[] UPGRADE_V10 = {
            "ALTER TABLE ec_child ADD COLUMN mother_entity_id VARCHAR;",
            "ALTER TABLE ec_child ADD COLUMN entry_point VARCHAR;"
    };

    String DELETE_DUPLICATE_SCHEDULES = "delete from schedule_service where id not in ( " +
            "select max(id) from schedule_service " +
            "group by base_entity_id , schedule_group_name , schedule_name " +
            "having count(*) > 1 " +
            ")";

    static void addDetailsColumnToFamilySearchTable(SQLiteDatabase db) {
        try {

            db.execSQL("ALTER TABLE ec_family ADD COLUMN entity_type VARCHAR; " +
                    "UPDATE ec_family SET entity_type = 'ec_family' WHERE id is not null;");

            List<String> columns = new ArrayList<>();
            columns.add(CoreConstants.DB_CONSTANTS.DETAILS);
            columns.add(DBConstants.KEY.ENTITY_TYPE);
            DatabaseMigrationUtils.addFieldsToFTSTable(db, CoreChwApplication.createCommonFtsObject(), CoreConstants.TABLE_NAME.FAMILY, columns);

        } catch (Exception e) {
            Timber.e(e, "commonUpgrade -> Failed to add column 'entity_type' and 'details' to ec_family_search ");
        }
    }

    static void updateNullEventIds(SQLiteDatabase db) {
        List<Event> events = new ArrayList<>();
        String eventTableName = EventClientRepository.Table.event.name();
        String eventIdCol = EventClientRepository.event_column.eventId.name();
        String eventSyncStatusCol = EventClientRepository.event_column.syncStatus.name();
        String eventValidCol = EventClientRepository.event_column.validationStatus.name();
        String jsonCol = EventClientRepository.event_column.json.name();
        String formSubmissionCol = EventClientRepository.event_column.formSubmissionId.name();

        Cursor cursor;
        String selection = eventIdCol + " IS NULL AND " + eventValidCol + " = ?";
        try {
            cursor = db.query(eventTableName, new String[]{jsonCol},
                    selection, new String[]{TYPE_Valid}, null, null, null);
            events = readEvents(cursor);
        } catch (Exception ex) {
            Timber.e(ex, "Problem getting events ");
        }
        String updateSQL;
        for (Event event : events) {
            updateSQL = String.format("UPDATE %s SET %s = '%s', %s = '%s' WHERE %s = '%s';", eventTableName,
                    eventIdCol, event.getEventId(), eventSyncStatusCol, TYPE_Synced, formSubmissionCol, event.getFormSubmissionId());
            try {
                db.execSQL(updateSQL);
            } catch (Exception e) {
                Timber.e(e, "Problem executing update ");
            }
        }
    }

    static List<Event> readEvents(Cursor cursor) {
        List<Event> events = new ArrayList<>();
        ECSyncHelper syncHelper = ChwApplication.getInstance().getEcSyncHelper();
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String json = cursor.getString(cursor.getColumnIndex("json"));
                    Event event = syncHelper.convert(new JSONObject(json), Event.class);
                    event.setEventId(getEventId(json));
                    events.add(event);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            cursor.close();
        }
        return events;
    }

     static String getEventId(String jsonString) {
        JSONObject jsonObject;
        String eventId = null;
        if (StringUtils.isNotEmpty(jsonString)) {
            try {
                jsonObject = new JSONObject(jsonString);
                if (jsonObject.has(EVENT_ID)) {
                    eventId = jsonObject.getString(EVENT_ID);
                } else if (jsonObject.has(_ID)) {
                    eventId = jsonObject.getString(_ID);
                }
            } catch (Exception ex) {
                Timber.e(ex);
            }
        }
        return eventId;
    }

}
