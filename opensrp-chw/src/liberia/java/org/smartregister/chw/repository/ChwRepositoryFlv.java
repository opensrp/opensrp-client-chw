package org.smartregister.chw.repository;

import static org.smartregister.repository.BaseRepository.TYPE_Synced;
import static org.smartregister.repository.BaseRepository.TYPE_Valid;

import android.content.Context;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.util.RepositoryUtils;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.Column;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.IMDatabaseUtils;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.repository.AlertRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class ChwRepositoryFlv {
    private static final String EVENT_ID = "id";
    private static final String _ID = "_id";

    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.w(ChwRepository.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    upgradeToVersion2(context, db);
                    break;
                case 3:
                    upgradeToVersion3(db);
                    break;
                case 4:
                    upgradeToVersion4(db);
                    break;
                case 5:
                    upgradeToVersion5(context, db);
                    break;
                case 6:
                    upgradeToVersion6(db);
                    break;
                case 7:
                    upgradeToVersion7(db);
                    break;
                case 8:
                    upgradeToVersion8(db);
                    break;
                default:
                    break;
            }
            upgradeTo++;
        }
    }


    private static void upgradeToVersion2(Context context, SQLiteDatabase db) {
        try {
            // nuke the database and recreate everything

            // add missing vaccine columns
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_EVENT_ID_COL);
            db.execSQL(VaccineRepository.EVENT_ID_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_FORMSUBMISSION_ID_COL);
            db.execSQL(VaccineRepository.FORMSUBMISSION_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_HIA2_STATUS_COL);

            // add missing event repository table
            Column[] columns = {EventClientRepository.event_column.formSubmissionId};
            EventClientRepository.createIndex(db, EventClientRepository.Table.event, columns);

            db.execSQL(VaccineRepository.ALTER_ADD_CREATED_AT_COLUMN);
            VaccineRepository.migrateCreatedAt(db);

            db.execSQL(RecurringServiceRecordRepository.ALTER_ADD_CREATED_AT_COLUMN);
            RecurringServiceRecordRepository.migrateCreatedAt(db);

            // add missing alert table info
            db.execSQL(AlertRepository.ALTER_ADD_OFFLINE_COLUMN);
            db.execSQL(AlertRepository.OFFLINE_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_TEAM_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_TEAM_ID_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_TEAM_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_TEAM_ID_COL);

            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);

            // setup reporting
            ReportingLibrary reportingLibrary = ReportingLibrary.getInstance();
            String childIndicatorsConfigFile = "config/child-reporting-indicator-definitions.yml";
            String ancIndicatorConfigFile = "config/anc-reporting-indicator-definitions.yml";
            String pncIndicatorConfigFile = "config/pnc-reporting-indicator-definitions.yml";
            for (String configFile : Collections.unmodifiableList(
                    Arrays.asList(childIndicatorsConfigFile, ancIndicatorConfigFile, pncIndicatorConfigFile))) {
                reportingLibrary.readConfigFile(configFile, db);
            }

        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion2 ");
        }
    }

    private static void upgradeToVersion3(SQLiteDatabase db) {
        try {
            // delete possible duplication
            db.execSQL(RepositoryUtils.ADD_MISSING_REPORTING_COLUMN);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void upgradeToVersion4(SQLiteDatabase db) {
        try {
            RepositoryUtils.addDetailsColumnToFamilySearchTable(db);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void upgradeToVersion5(Context context, SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_IS_VOIDED_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_IS_VOIDED_COL_INDEX);

            IMDatabaseUtils.accessAssetsAndFillDataBaseForVaccineTypes(context, db);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void upgradeToVersion6(SQLiteDatabase db) {
        try {
            db.execSQL(VisitRepository.ADD_VISIT_GROUP_COLUMN);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion6");
        }
    }


    private static void upgradeToVersion7(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE ec_family_member ADD COLUMN marital_status VARCHAR;");
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion7");
        }
    }

    private static void upgradeToVersion8(SQLiteDatabase db) {
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
            Timber.e(ex);
        }
        String updateSQL;
        for (Event event : events) {
            updateSQL = String.format("UPDATE %s SET %s = '%s', %s = '%s' WHERE %s = '%s';", eventTableName,
                    eventIdCol, event.getEventId(), eventSyncStatusCol, TYPE_Synced, formSubmissionCol, event.getFormSubmissionId());
            try {
                db.execSQL(updateSQL);
            } catch (Exception e) {
                Timber.e(e, "upgradeToVersion21 ");
            }
        }
    }

    private static List<Event> readEvents(Cursor cursor) {
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

    private static String getEventId(String jsonString) {
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