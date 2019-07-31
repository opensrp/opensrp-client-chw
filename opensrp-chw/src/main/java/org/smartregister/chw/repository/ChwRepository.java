package org.smartregister.chw.repository;

import android.content.Context;

import com.opensrp.chw.core.repository.CoreRepository;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.AllConstants;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.reporting.ReportingLibrary;

import java.util.Arrays;
import java.util.Collections;

import timber.log.Timber;

/**
 * Created by keyman on 27/11/2018.
 */
public class ChwRepository extends CoreRepository {

    private Context context;

    public ChwRepository(Context context, org.smartregister.Context openSRPContext) {
        super(context, AllConstants.DATABASE_NAME, BuildConfig.DATABASE_VERSION, openSRPContext.session(), ChwApplication.createCommonFtsObject(), openSRPContext.sharedRepositoriesArray());
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
        HomeVisitIndicatorInfoRepository.createTable(database);
        HomeVisitRepository.createTable(database);
        HomeVisitServiceRepository.createTable(database);

        ReportingLibrary reportingLibraryInstance = ReportingLibrary.getInstance();
        String childIndicatorsConfigFile = "config/child-reporting-indicator-definitions.yml";
        String ancIndicatorConfigFile = "config/anc-reporting-indicator-definitions.yml";
        reportingLibraryInstance.initMultipleIndicatorsData(Collections.unmodifiableList(
                Arrays.asList(childIndicatorsConfigFile, ancIndicatorConfigFile)), database);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.w(ChwRepository.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion + ", which will destroy all old data");
        ChwRepositoryFlv.onUpgrade(context, db, oldVersion, newVersion);
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        String pass = ChwApplication.getInstance().getPassword();
        if (StringUtils.isNotBlank(pass)) {
            return getReadableDatabase(pass);
        } else {
            throw new IllegalStateException("Password is blank");
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        String pass = ChwApplication.getInstance().getPassword();
        if (StringUtils.isNotBlank(pass)) {
            return getWritableDatabase(pass);
        } else {
            throw new IllegalStateException("Password is blank");
        }
    }
}