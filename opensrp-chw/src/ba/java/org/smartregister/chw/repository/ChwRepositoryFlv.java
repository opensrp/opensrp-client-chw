package org.smartregister.chw.repository;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.chw.anc.repository.VisitDetailsRepository;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.util.RepositoryUtils;
import org.smartregister.chw.util.RepositoryUtilsFlv;
import org.smartregister.domain.db.Column;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.IMDatabaseUtils;
import org.smartregister.repository.AlertRepository;
import org.smartregister.repository.EventClientRepository;

import timber.log.Timber;

public class ChwRepositoryFlv {

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
                    upgradeToVersion10(db);
                    break;
                default:
                    break;
            }
            upgradeTo++;
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

            IMDatabaseUtils.accessAssetsAndFillDataBaseForVaccineTypes(context, db);

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
            for (String query : RepositoryUtilsFlv.UPGRADE_V8) {
                db.execSQL(query);
            }
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion7 ");
        }
    }

    private static void upgradeToVersion8(SQLiteDatabase db) {
        try {
            for (String query : RepositoryUtilsFlv.UPGRADE_V9) {
                db.execSQL(query);
            }
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion8 ");
        }
    }

    private static void upgradeToVersion9(SQLiteDatabase db) {
        try {
            VisitRepository.createTable(db);
            VisitDetailsRepository.createTable(db);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion9 ");
        }
    }

    private static void upgradeToVersion10(SQLiteDatabase db) {
        try {
            for (String query : RepositoryUtils.UPGRADE_V10) {
                db.execSQL(query);
            }
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion10 ");
        }
    }
}
