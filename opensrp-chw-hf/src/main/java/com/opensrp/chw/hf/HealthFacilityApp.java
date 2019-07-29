package com.opensrp.chw.hf;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.evernote.android.job.JobManager;
import com.opensrp.chw.core.contract.CoreApplication;
import com.opensrp.chw.core.custom_views.NavigationMenu;
import com.opensrp.chw.core.loggers.CrashlyticsTree;
import com.opensrp.chw.core.service.CoreAuthorizationService;
import com.opensrp.chw.core.utils.Constants;
import com.opensrp.chw.hf.activity.FamilyProfileActivity;
import com.opensrp.chw.hf.activity.FamilyRegisterActivity;
import com.opensrp.chw.hf.activity.LoginActivity;
import com.opensrp.chw.hf.custom_view.HfNavigationMenu;
import com.opensrp.chw.hf.job.HfJobCreator;
import com.opensrp.chw.hf.model.HfNavigationModel;
import com.opensrp.chw.hf.repository.HfRepository;
import com.opensrp.chw.hf.sync.HfSyncConfiguration;
import com.opensrp.hf.BuildConfig;

import org.jetbrains.annotations.NotNull;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.P2POptions;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

import static com.opensrp.chw.core.utils.ApplicationUtils.getCommonFtsObject;
import static com.opensrp.chw.core.utils.FormUtils.getFamilyMetadata;

public class HealthFacilityApp extends DrishtiApplication implements CoreApplication {

    private static final String TAG = HealthFacilityApp.class.getCanonicalName();
    private static CommonFtsObject commonFtsObject = null;
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

        //init Job Manager
        JobManager.create(this).addJobCreator(new HfJobCreator());

        //Necessary to determine the right form to pick from assets
        Constants.JSON_FORM.setLocaleAndAssetManager(HealthFacilityApp.getCurrentLocale(),
                HealthFacilityApp.getInstance().getApplicationContext().getAssets());

        //Setup Navigation menu. Done only once when app is created
        NavigationMenu.setupNavigationMenu(this, new HfNavigationMenu(), new HfNavigationModel(), getRegisteredActivities());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashlyticsTree(HealthFacilityApp.getInstance().getContext()
                    .allSharedPreferences().fetchRegisteredANM()));
        }

        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG).build()).build());

        // init json helper
        this.jsonSpecHelper = new JsonSpecHelper(this);

        //Initialize Peer to peer modules
        P2POptions p2POptions = new P2POptions(true);
        p2POptions.setAuthorizationService(new CoreAuthorizationService());

        // init libraries
        CoreLibrary.init(context, new HfSyncConfiguration(), BuildConfig.BUILD_TIMESTAMP, p2POptions);
        ConfigurableViewsLibrary.init(context, getRepository());
        FamilyLibrary.init(context, getRepository(), getFamilyMetadata(new FamilyProfileActivity()), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        ImmunizationLibrary.init(context, getRepository(), null, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
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

    @Override
    public @NotNull Map<String, Class> getRegisteredActivities() {
        Map<String, Class> registeredActivities = new HashMap<>();
        registeredActivities.put(Constants.REGISTERED_ACTIVITIES.ANC_REGISTER_ACTIVITY, FamilyRegisterActivity.class);
        registeredActivities.put(Constants.REGISTERED_ACTIVITIES.FAMILY_REGISTER_ACTIVITY, FamilyRegisterActivity.class);
        registeredActivities.put(Constants.REGISTERED_ACTIVITIES.CHILD_REGISTER_ACTIVITY, FamilyRegisterActivity.class);
        return registeredActivities;
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

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new HfRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            Timber.e(e);
        }
        return repository;
    }
    public static CommonFtsObject createCommonFtsObject() {
        return getCommonFtsObject(commonFtsObject);
    }
}
