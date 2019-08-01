package com.opensrp.chw.hf.repository;

import android.content.Context;

import com.opensrp.chw.core.application.CoreChwApplication;
import com.opensrp.chw.core.repository.CoreChwRepository;
import com.opensrp.hf.BuildConfig;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.AllConstants;

import timber.log.Timber;

public class HfChwRepository extends CoreChwRepository {
    public HfChwRepository(Context context, org.smartregister.Context openSRPContext) {
        super(context, AllConstants.DATABASE_NAME, BuildConfig.DATABASE_VERSION, openSRPContext.session(), CoreChwApplication.createCommonFtsObject(), openSRPContext.sharedRepositoriesArray());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.w(HfChwRepository.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion + ", which will destroy all old data");
        super.onUpgrade(db, oldVersion, newVersion);
    }
}
