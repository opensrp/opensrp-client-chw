package com.opensrp.chw.hf;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.opensrp.chw.core.loggers.CrashlyticsTree;
import com.opensrp.chw.hf.activity.LoginActivity;
import com.opensrp.hf.BuildConfig;

import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class HealthFacilityApp extends DrishtiApplication {

    private static final String TAG = HealthFacilityApp.class.getCanonicalName();
    private String password;

    public static synchronized HealthFacilityApp getInstance() {
        return (HealthFacilityApp) mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashlyticsTree(HealthFacilityApp.getInstance().getContext().allSharedPreferences().fetchRegisteredANM()));
        }


        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());


        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());
        //Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().build()).build());


        // init libraries
        ImmunizationLibrary.init(context, getRepository(), null, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        ConfigurableViewsLibrary.init(context, getRepository());

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

    public void setOpenSRPUrl() {
        AllSharedPreferences preferences = Utils.getAllSharedPreferences();
        if (BuildConfig.DEBUG) {
            preferences.savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url_debug);
        } else {
            preferences.savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url);
        }
    }

    private void saveLanguage(String language) {
        AllSharedPreferences allSharedPreferences = HealthFacilityApp.getInstance().getContext().allSharedPreferences();
        allSharedPreferences.saveLanguagePreference(language);
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
    }

    public String getPassword() {
        if (password == null) {
            String username = getContext().allSharedPreferences().fetchRegisteredANM();
            password = getContext().userService().getGroupId(username);
        }
        return password;
    }

    public Context getContext() {
        return context;
    }


}
