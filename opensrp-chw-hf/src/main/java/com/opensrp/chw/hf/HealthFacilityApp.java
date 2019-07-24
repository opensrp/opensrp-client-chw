package com.opensrp.chw.hf;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.opensrp.chw.core.contract.CoreApplication;
import com.opensrp.chw.core.loggers.CrashlyticsTree;
import com.opensrp.chw.core.utils.Constants;
import com.opensrp.chw.hf.activity.LoginActivity;
import com.opensrp.chw.hf.sync.HfSyncConfiguration;
import com.opensrp.hf.BuildConfig;

import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class HealthFacilityApp extends DrishtiApplication implements CoreApplication {

    private static final String TAG = HealthFacilityApp.class.getCanonicalName();
    private String password;
    private JsonSpecHelper jsonSpecHelper;
    private ECSyncHelper ecSyncHelper;

    public static synchronized HealthFacilityApp getInstance() {
        return (HealthFacilityApp) mInstance;
    }

    public static JsonSpecHelper getJsonSpecHelper() {
        return getInstance().jsonSpecHelper;
    }

    public static Locale getCurrentLocale() {
        return mInstance == null ? Locale.getDefault() : mInstance.getResources().getConfiguration().locale;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Necessary to determine the right form to pick from assets
        Constants.JSON_FORM.setLocaleAndAssetManager(HealthFacilityApp.getCurrentLocale(),
                HealthFacilityApp.getInstance().getApplicationContext().getAssets());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashlyticsTree(HealthFacilityApp.getInstance().getContext()
                    .allSharedPreferences().fetchRegisteredANM()));
        }

        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());

        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG).build()).build());

        // init json helper
        this.jsonSpecHelper = new JsonSpecHelper(this);

        // init libraries
        CoreLibrary.init(context, new HfSyncConfiguration(), BuildConfig.BUILD_TIMESTAMP, null);
        ConfigurableViewsLibrary.init(context, getRepository());

        //     FamilyLibrary.init(context, getRepository(), getFamilyMetadata(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);

        SyncStatusBroadcastReceiver.init(this);
        LocationHelper.init(new ArrayList<>(Arrays.asList(BuildConfig.ALLOWED_LOCATION_LEVELS)), BuildConfig.DEFAULT_LOCATION);

        setOpenSRPUrl();

        Configuration configuration = getApplicationContext().getResources().getConfiguration();
        String language;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            language = configuration.getLocales().get(0).getLanguage();
        } else {
            language = configuration.locale.getLanguage();
        }

        if (language.equals(Locale.FRENCH.getLanguage())) {
            saveLanguage(Locale.FRENCH.getLanguage());
        }

    }

    @Override
    public void logoutCurrentUser() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
        Timber.tag(TAG).i("Logged out user %s", getContext().allSharedPreferences().fetchRegisteredANM());
    }

    public String getPassword() {
        if (password == null) {
            String username = getContext().allSharedPreferences().fetchRegisteredANM();
            password = getContext().userService().getGroupId(username);
        }
        return password;
    }

    public void setOpenSRPUrl() {
        AllSharedPreferences preferences = Utils.getAllSharedPreferences();
        if (BuildConfig.DEBUG) {
            preferences.savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url_debug);
        } else {
            preferences.savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url);
        }
    }

    public void saveLanguage(String language) {
        HealthFacilityApp.getInstance().getContext().allSharedPreferences().saveLanguagePreference(language);
    }

    public Context getContext() {
        return context;
    }

    @Override
    public ECSyncHelper getEcSyncHelper() {
        if (ecSyncHelper == null) {
            ecSyncHelper = ECSyncHelper.getInstance(getApplicationContext());
        }
        return ecSyncHelper;
    }

    @Override
    public void notifyAppContextChange() {

    }
}
