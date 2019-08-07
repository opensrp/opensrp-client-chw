package com.opensrp.chw.hf.repository;

import android.content.Context;

import com.opensrp.chw.core.application.CoreChwApplication;
import com.opensrp.chw.core.repository.CoreChwRepository;
import com.opensrp.chw.core.repository.HomeVisitRepository;
import com.opensrp.hf.BuildConfig;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.AllConstants;
import org.smartregister.chw.anc.repository.VisitDetailsRepository;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.domain.db.Column;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.IMDatabaseUtils;
import org.smartregister.repository.AlertRepository;
import org.smartregister.repository.EventClientRepository;

import timber.log.Timber;

public class HfChwRepository extends CoreChwRepository {
    private Context context;

    private static String[] UPGRADE_V6 = {
            "ALTER TABLE ec_family ADD COLUMN nearest_facility VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN voter_id VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN driver_license VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN passport VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN insurance_provider VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN insurance_provider_other VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN insurance_provider_number VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN disabilities VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN service_provider VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN leader VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN leader_other VARCHAR;"
    };

    private static String[] UPGRADE_V8 = {
            "ALTER TABLE ec_family_member ADD COLUMN type_of_disability VARCHAR;"
    };

    private static String[] UPGRADE_V9 = {
            "ALTER TABLE ec_child ADD COLUMN insurance_provider VARCHAR;",
            "ALTER TABLE ec_child ADD COLUMN insurance_provider_number VARCHAR;",
            "ALTER TABLE ec_child ADD COLUMN insurance_provider_other VARCHAR;",
            "ALTER TABLE ec_child ADD COLUMN type_of_disability VARCHAR;"
    };

    public HfChwRepository(Context context, org.smartregister.Context openSRPContext) {
        super(context, AllConstants.DATABASE_NAME, BuildConfig.DATABASE_VERSION, openSRPContext.session(), CoreChwApplication.createCommonFtsObject(), openSRPContext.sharedRepositoriesArray());
        this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.w(HfChwRepository.class.getName(),
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
                case 6:
                    upgradeToVersion6(db);
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


    private static void upgradeToVersion6(SQLiteDatabase db) {
        try {
            for (String query : UPGRADE_V6) {
                db.execSQL(query);
            }
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion6 ");
        }
    }


    private static void upgradeToVersion7(SQLiteDatabase db) {
        try {
            db.execSQL(HomeVisitRepository.UPDATE_TABLE_ADD_VACCINE_NOT_GIVEN);
            db.execSQL(HomeVisitRepository.UPDATE_TABLE_ADD_SERVICE_NOT_GIVEN);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion7 ");
        }
    }

    private static void upgradeToVersion8(SQLiteDatabase db) {
        try {
            for (String query : UPGRADE_V8) {
                db.execSQL(query);
            }
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion8 ");
        }
    }

    private static void upgradeToVersion9(SQLiteDatabase db) {
        try {
            for (String query : UPGRADE_V9) {
                db.execSQL(query);
            }
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion9 ");
        }
    }

    private static void upgradeToVersion10(SQLiteDatabase db) {
        try {
            VisitRepository.createTable(db);
            VisitDetailsRepository.createTable(db);
            db.execSQL(AlertRepository.ALTER_ADD_OFFLINE_COLUMN);
            db.execSQL(AlertRepository.OFFLINE_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_TEAM_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_TEAM_ID_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_TEAM_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_TEAM_ID_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);
            db.execSQL(HomeVisitRepository.UPDATE_TABLE_ADD_VACCINE_NOT_GIVEN);
            db.execSQL(HomeVisitRepository.UPDATE_TABLE_ADD_SERVICE_NOT_GIVEN);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion10 ");
        }
    }
}
