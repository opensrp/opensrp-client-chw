package org.smartregister.chw.repository;

import android.content.Context;

import com.opensrp.chw.core.application.CoreChwApplication;
import com.opensrp.chw.core.repository.CoreChwRepository;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.AllConstants;
import org.smartregister.chw.BuildConfig;

import timber.log.Timber;

public class ChwRepository extends CoreChwRepository {
    private Context context;

    public ChwRepository(Context context, org.smartregister.Context openSRPContext) {
        super(context, AllConstants.DATABASE_NAME, BuildConfig.DATABASE_VERSION, openSRPContext.session(), CoreChwApplication.createCommonFtsObject(), openSRPContext.sharedRepositoriesArray());
        this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.w(CoreChwRepository.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion + ", which will destroy all old data");
        ChwRepositoryFlv.onUpgrade(context, db, oldVersion, newVersion);
    }
}
