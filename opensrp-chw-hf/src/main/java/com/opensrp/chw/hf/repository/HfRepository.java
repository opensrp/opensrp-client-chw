package com.opensrp.chw.hf.repository;

import android.content.Context;

import com.opensrp.chw.core.repository.CoreRepository;
import com.opensrp.chw.hf.HealthFacilityApplication;
import com.opensrp.hf.BuildConfig;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.AllConstants;

import timber.log.Timber;

public class HfRepository extends CoreRepository {
    public HfRepository(Context context, org.smartregister.Context openSRPContext) {
        super(context, AllConstants.DATABASE_NAME, BuildConfig.DATABASE_VERSION, openSRPContext.session(), HealthFacilityApplication.createCommonFtsObject(), openSRPContext.sharedRepositoriesArray());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.w(HfRepository.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion + ", which will destroy all old data");
        super.onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        String pass = HealthFacilityApplication.getInstance().getPassword();
        if (StringUtils.isNotBlank(pass)) {
            return getReadableDatabase(pass);
        } else {
            throw new IllegalStateException("Password is blank");
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        String pass = HealthFacilityApplication.getInstance().getPassword();
        if (StringUtils.isNotBlank(pass)) {
            return getWritableDatabase(pass);
        } else {
            throw new IllegalStateException("Password is blank");
        }
    }

}
