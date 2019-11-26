package org.smartregister.brac.hnpp.repository;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.AllConstants;
import org.smartregister.chw.anc.repository.VisitDetailsRepository;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.repository.CoreChwRepository;
import org.smartregister.brac.hnpp.BuildConfig;
import timber.log.Timber;

public class HnppChwRepository extends CoreChwRepository {
    private Context context;

    public HnppChwRepository(Context context, org.smartregister.Context openSRPContext) {
        super(context, AllConstants.DATABASE_NAME, BuildConfig.DATABASE_VERSION, openSRPContext.session(), CoreChwApplication.createCommonFtsObject(), openSRPContext.sharedRepositoriesArray());
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
    }

    @Override
    protected void onCreation(SQLiteDatabase database) {
        SSLocationRepository.createTable(database);
        HouseholdIdRepository.createTable(database);
        VisitRepository.createTable(database);
        VisitDetailsRepository.createTable(database);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.w(HnppChwRepository.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 7:
                    upgradeToVersion7(context, db);
                    break;

                default:
                    break;
            }
            upgradeTo++;
        }
    }

    private void upgradeToVersion7(Context context, SQLiteDatabase db) {
        try{
            db.execSQL("ALTER TABLE ec_family ADD COLUMN serial_no VARCHAR;");
            db.execSQL("ALTER TABLE ec_family_member ADD COLUMN mother_name VARCHAR;");
            db.execSQL("ALTER TABLE ec_family_member ADD COLUMN mother_entity_id VARCHAR;");
        }catch (Exception e){

        }
        try{
            db.execSQL("ALTER TABLE ec_family ADD COLUMN occupation VARCHAR;");
            db.execSQL("ALTER TABLE ec_family ADD COLUMN occupation_other VARCHAR;");
            db.execSQL("ALTER TABLE ec_family ADD COLUMN financial_status VARCHAR;");
            db.execSQL("ALTER TABLE ec_family ADD COLUMN list_of_assets VARCHAR;");
            db.execSQL("ALTER TABLE ec_family ADD COLUMN floor_material VARCHAR;");
            db.execSQL("ALTER TABLE ec_family ADD COLUMN wall_material VARCHAR;");
            db.execSQL("ALTER TABLE ec_family ADD COLUMN roof_material VARCHAR;");
        }catch (Exception e){

        }


    }

}
