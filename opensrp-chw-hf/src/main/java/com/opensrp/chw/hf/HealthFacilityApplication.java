package com.opensrp.chw.hf;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.evernote.android.job.JobManager;
import com.opensrp.chw.core.application.CoreChwApplication;
import com.opensrp.chw.core.contract.CoreApplication;
import com.opensrp.chw.core.custom_views.NavigationMenu;
import com.opensrp.chw.core.loggers.CrashlyticsTree;
import com.opensrp.chw.core.service.CoreAuthorizationService;
import com.opensrp.chw.core.utils.CoreConstants;
import com.opensrp.chw.hf.activity.ChildRegisterActivity;
import com.opensrp.chw.hf.activity.FamilyProfileActivity;
import com.opensrp.chw.hf.activity.FamilyRegisterActivity;
import com.opensrp.chw.hf.activity.LoginActivity;
import com.opensrp.chw.hf.activity.ReferralRegister;
import com.opensrp.chw.hf.custom_view.HfNavigationMenu;
import com.opensrp.chw.hf.job.HfJobCreator;
import com.opensrp.chw.hf.model.NavigationModel;
import com.opensrp.chw.hf.repository.HfChwRepository;
import com.opensrp.chw.hf.sync.HfSyncConfiguration;
import com.opensrp.hf.BuildConfig;

import org.jetbrains.annotations.NotNull;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.P2POptions;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.malaria.MalariaLibrary;
import org.smartregister.chw.pnc.PncLibrary;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

import static com.opensrp.chw.core.utils.FormUtils.getFamilyMetadata;

public class HealthFacilityApplication extends CoreChwApplication implements CoreApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        //init Job Manager
        SyncStatusBroadcastReceiver.init(this);
        JobManager.create(this).addJobCreator(new HfJobCreator());

        //Necessary to determine the right form to pick from assets
        CoreConstants.JSON_FORM.setLocaleAndAssetManager(HealthFacilityApplication.getCurrentLocale(),
                HealthFacilityApplication.getInstance().getApplicationContext().getAssets());

        //Setup Navigation menu. Done only once when app is created
        NavigationMenu.setupNavigationMenu(this, new HfNavigationMenu(), new NavigationModel(), getRegisteredActivities());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashlyticsTree(HealthFacilityApplication.getInstance().getContext()
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
        LocationHelper.init(new ArrayList<>(Arrays.asList(BuildConfig.ALLOWED_LOCATION_LEVELS)), BuildConfig.DEFAULT_LOCATION);
        ReportingLibrary.init(context, getRepository(), null, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        AncLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        PncLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        MalariaLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);

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
        Timber.i("Logged out user %s", getContext().allSharedPreferences().fetchRegisteredANM());
    }

    public void setOpenSRPUrl() {
        AllSharedPreferences preferences = Utils.getAllSharedPreferences();
        if (BuildConfig.DEBUG) {
            preferences.savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url_debug);
        } else {
            preferences.savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url);
        }
    }

    public @NotNull Map<String, Class> getRegisteredActivities() {
        Map<String, Class> registeredActivities = new HashMap<>();
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.ANC_REGISTER_ACTIVITY, FamilyRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.FAMILY_REGISTER_ACTIVITY, FamilyRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.CHILD_REGISTER_ACTIVITY, ChildRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.PNC_REGISTER_ACTIVITY, FamilyRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.REFERRALS_REGISTER_ACTIVITY, ReferralRegister.class);
        return registeredActivities;
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new HfChwRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            Timber.e(e);
        }
        return repository;
    }
}
