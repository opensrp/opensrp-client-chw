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
import org.smartregister.chw.util.ChildDBConstants;
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

    private static final String TAG = ChwRepositoryFlv.class.getCanonicalName();
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
                    upgradeToVersion5(db);
                    break;
                case 7:
                    upgradeToVersion7(db);
                    break;
                case 8:
                    upgradeToVersion8(db);
                    break;
                case 9:
                    upgradeToVersion9(db);
                    break;
                case 10:
                    upgradeToVersion10(db, oldVersion);
                    break;
                case 11:
                    upgradeToVersion11(context, db);
                    break;
                case 12:
                    upgradeToVersion12(db);
                    break;
                case 13:
                    upgradeToVersion13(db);
                    break;
                case 14:
                    upgradeToVersion14(db);
                    break;
                case 15:
                    upgradeToVersion15(db);
                    break;
                default:
                    break;
            }
            upgradeTo++;
        }
    }

    private static void upgradeToVersion14(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE ec_family_member ADD COLUMN marital_status VARCHAR;");
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion14");
        }
    }


    private static void upgradeToVersion2(Context context, SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_EVENT_ID_COL);
            db.execSQL(VaccineRepository.EVENT_ID_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_FORMSUBMISSION_ID_COL);
            db.execSQL(VaccineRepository.FORMSUBMISSION_INDEX);

            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL_INDEX);

//            EventClientRepository.createTable(db, EventClientRepository.Table.path_reports, EventClientRepository.report_column.values());
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_HIA2_STATUS_COL);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion2 ");
        }

    }

    private static void upgradeToVersion3(SQLiteDatabase db) {
        try {
            Column[] columns = {EventClientRepository.event_column.formSubmissionId};
            EventClientRepository.createIndex(db, EventClientRepository.Table.event, columns);

            db.execSQL(VaccineRepository.ALTER_ADD_CREATED_AT_COLUMN);
            VaccineRepository.migrateCreatedAt(db);

            db.execSQL(RecurringServiceRecordRepository.ALTER_ADD_CREATED_AT_COLUMN);
            RecurringServiceRecordRepository.migrateCreatedAt(db);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion3 ");
        }
        try {
            Column[] columns = {EventClientRepository.event_column.formSubmissionId};
            EventClientRepository.createIndex(db, EventClientRepository.Table.event, columns);


        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion3 " + e.getMessage());
        }
    }

    private static void upgradeToVersion4(SQLiteDatabase db) {
        try {
            db.execSQL(AlertRepository.ALTER_ADD_OFFLINE_COLUMN);
            db.execSQL(AlertRepository.OFFLINE_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_TEAM_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_TEAM_ID_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_TEAM_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_TEAM_ID_COL);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion4 ");
        }

    }

    private static void upgradeToVersion5(SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion5 ");
        }
    }

    private static void upgradeToVersion7(SQLiteDatabase db) {
        try {
            //db.execSQL(HomeVisitRepository.UPDATE_TABLE_ADD_VACCINE_NOT_GIVEN);
            //db.execSQL(HomeVisitRepository.UPDATE_TABLE_ADD_SERVICE_NOT_GIVEN)
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion7 ");
        }
    }

    private static void upgradeToVersion8(SQLiteDatabase db) {
        try {
            RepositoryUtils.addDetailsColumnToFamilySearchTable(db);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion8 ");
        }
    }

    private static void upgradeToVersion9(SQLiteDatabase db) {
    }

    private static void upgradeToVersion12(SQLiteDatabase db) {
        try {
            db.execSQL(VisitRepository.ADD_VISIT_GROUP_COLUMN);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion9");
        }
    }

    private static void upgradeToVersion13(SQLiteDatabase db) {
        try {
            db.execSQL(ChildDBConstants.ADD_COLUMN_THINK_MD_ID);
            db.execSQL(ChildDBConstants.ADD_COLUMN_HTML_ASSESSMENT);
            db.execSQL(ChildDBConstants.ADD_COLUMN_CARE_PLAN_DATE);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion13");
        }
    }

    private static void upgradeToVersion10(SQLiteDatabase db, int oldDbVersion) {
        try {
            ReportingLibrary reportingLibraryInstance = ReportingLibrary.getInstance();
            if (oldDbVersion == 9) {
                reportingLibraryInstance.truncateIndicatorDefinitionTables(db);
            }
            initializeIndicatorDefinitions(reportingLibraryInstance, db);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void upgradeToVersion11(Context context, SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_IS_VOIDED_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_IS_VOIDED_COL_INDEX);

            IMDatabaseUtils.accessAssetsAndFillDataBaseForVaccineTypes(context, db);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void upgradeToVersion15(SQLiteDatabase db) {
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

    private static void initializeIndicatorDefinitions(ReportingLibrary reportingLibrary, SQLiteDatabase sqLiteDatabase) {
        String childIndicatorsConfigFile = "config/child-reporting-indicator-definitions.yml";
        String ancIndicatorConfigFile = "config/anc-reporting-indicator-definitions.yml";
        String pncIndicatorConfigFile = "config/pnc-reporting-indicator-definitions.yml";
        for (String configFile : Collections.unmodifiableList(
                Arrays.asList(childIndicatorsConfigFile, ancIndicatorConfigFile, pncIndicatorConfigFile))) {
            reportingLibrary.readConfigFile(configFile, sqLiteDatabase);
        }
    }
}
