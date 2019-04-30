package org.smartregister.chw.repository;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.AllConstants;
import org.smartregister.chw.util.Country;
import org.smartregister.chw.util.RepositoryUtils;
import org.smartregister.configurableviews.repository.ConfigurableViewsRepository;
import org.smartregister.domain.db.Column;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineNameRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.repository.VaccineTypeRepository;
import org.smartregister.immunization.util.IMDatabaseUtils;
import org.smartregister.repository.AlertRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.SettingsRepository;
import org.smartregister.repository.UniqueIdRepository;

/**
 * Created by keyman on 27/11/2018.
 */
public class ChwRepository extends Repository {

    private static final String TAG = ChwRepository.class.getCanonicalName();
    protected SQLiteDatabase readableDatabase;
    protected SQLiteDatabase writableDatabase;
    private Context context;

    public ChwRepository(Context context, org.smartregister.Context openSRPContext) {
        super(context, AllConstants.DATABASE_NAME, BuildConfig.DATABASE_VERSION, openSRPContext.session(), ChwApplication.createCommonFtsObject(), openSRPContext.sharedRepositoriesArray());
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
        EventClientRepository.createTable(database, EventClientRepository.Table.client, EventClientRepository.client_column.values());
        EventClientRepository.createTable(database, EventClientRepository.Table.event, EventClientRepository.event_column.values());
        VaccineRepository.createTable(database);
        VaccineNameRepository.createTable(database);
        VaccineTypeRepository.createTable(database);
        HomeVisitRepository.createTable(database);
        ConfigurableViewsRepository.createTable(database);

        UniqueIdRepository.createTable(database);
        SettingsRepository.onUpgrade(database);

        RecurringServiceTypeRepository.createTable(database);
        RecurringServiceRecordRepository.createTable(database);
        //onUpgrade(database, 1, 2);
        RecurringServiceTypeRepository recurringServiceTypeRepository = ImmunizationLibrary.getInstance().recurringServiceTypeRepository();
        IMDatabaseUtils.populateRecurringServices(context, database, recurringServiceTypeRepository);


        onUpgrade(database, 1, BuildConfig.DATABASE_VERSION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ChwRepository.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    upgradeToVersion2(db);
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
                default:
                    break;
            }
            upgradeTo++;
        }
    }


    @Override
    public SQLiteDatabase getReadableDatabase() {
        String pass = ChwApplication.getInstance().getPassword();
        if(StringUtils.isNotBlank(pass)){
            return getReadableDatabase(pass);
        }else{
            throw new IllegalStateException("Password is blank");
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        String pass = ChwApplication.getInstance().getPassword();
        if(StringUtils.isNotBlank(pass)){
            return getWritableDatabase(pass);
        }else{
            throw new IllegalStateException("Password is blank");
        }
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase(String password) {
        try {
            if (readableDatabase == null || !readableDatabase.isOpen()) {
                if (readableDatabase != null) {
                    readableDatabase.close();
                }
                readableDatabase = super.getReadableDatabase(password);
            }
            return readableDatabase;
        } catch (Exception e) {
            Log.e(TAG, "Database Error. " + e.getMessage());
            return null;
        }

    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase(String password) {
        if (writableDatabase == null || !writableDatabase.isOpen()) {
            if (writableDatabase != null) {
                writableDatabase.close();
            }
            writableDatabase = super.getWritableDatabase(password);
        }
        return writableDatabase;
    }

    @Override
    public synchronized void close() {
        if (readableDatabase != null) {
            readableDatabase.close();
        }

        if (writableDatabase != null) {
            writableDatabase.close();
        }
        super.close();
    }

    private void upgradeToVersion2(SQLiteDatabase db) {
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
            Log.e(TAG, "upgradeToVersion2 " + Log.getStackTraceString(e));
        }

    }

    private void upgradeToVersion3(SQLiteDatabase db) {
        try {
            Column[] columns = {EventClientRepository.event_column.formSubmissionId};
            EventClientRepository.createIndex(db, EventClientRepository.Table.event, columns);

            db.execSQL(VaccineRepository.ALTER_ADD_CREATED_AT_COLUMN);
            VaccineRepository.migrateCreatedAt(db);

            db.execSQL(RecurringServiceRecordRepository.ALTER_ADD_CREATED_AT_COLUMN);
            RecurringServiceRecordRepository.migrateCreatedAt(db);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion3 " + Log.getStackTraceString(e));
        }
        try {
            Column[] columns = {EventClientRepository.event_column.formSubmissionId};
            EventClientRepository.createIndex(db, EventClientRepository.Table.event, columns);


        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion3 " + e.getMessage());
        }
    }

    private void upgradeToVersion4(SQLiteDatabase db) {
        try {
            db.execSQL(AlertRepository.ALTER_ADD_OFFLINE_COLUMN);
            db.execSQL(AlertRepository.OFFLINE_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_TEAM_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_TEAM_ID_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_TEAM_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_TEAM_ID_COL);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion4 " + Log.getStackTraceString(e));
        }

    }

    private void upgradeToVersion5(SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion5 " + Log.getStackTraceString(e));
        }
    }

    private void upgradeToVersion6(SQLiteDatabase db) {
        try {
            if (BuildConfig.BUILD_COUNTRY == Country.TANZANIA) {
                for (String query : RepositoryUtils.UPGRADE_V6) {
                    db.execSQL(query);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion6 " + Log.getStackTraceString(e));
        }
    }

    private void upgradeToVersion7(SQLiteDatabase db) {
        try {
            db.execSQL(HomeVisitRepository.UPDATE_TABLE_ADD_VACCINE_NOT_GIVEN);
            db.execSQL(HomeVisitRepository.UPDATE_TABLE_ADD_SERVICE_NOT_GIVEN);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion7 " + Log.getStackTraceString(e));
        }
    }

    private void upgradeToVersion8(SQLiteDatabase db) {
        try {
            if (BuildConfig.BUILD_COUNTRY == Country.TANZANIA) {
                for (String query : RepositoryUtils.UPGRADE_V8) {
                    db.execSQL(query);
                }
            }
        }catch (Exception e) {
            Log.e(TAG, "upgradeToVersion8 " + Log.getStackTraceString(e));
        }
    }

    private void upgradeToVersion9(SQLiteDatabase db) {
        try {
            if (BuildConfig.BUILD_COUNTRY == Country.TANZANIA) {
                for (String query : RepositoryUtils.UPGRADE_V9) {
                    db.execSQL(query);
                }
            }
        }catch (Exception e) {
            Log.e(TAG, "upgradeToVersion9 " + Log.getStackTraceString(e));
        }
    }

}
