package org.smartregister.chw.application;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.evernote.android.job.JobManager;
import com.opensrp.chw.core.contract.CoreApplication;
import com.opensrp.chw.core.custom_views.NavigationMenu;
import com.opensrp.chw.core.loggers.CrashlyticsTree;
import com.opensrp.chw.core.utils.Constants;

import org.jetbrains.annotations.NotNull;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.P2POptions;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.activity.AncRegisterActivity;
import org.smartregister.chw.activity.ChildRegisterActivity;
import org.smartregister.chw.activity.FamilyProfileActivity;
import org.smartregister.chw.activity.FamilyRegisterActivity;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.PncRegisterActivity;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.custom_view.NavigationMenuFlv;
import org.smartregister.chw.helper.RulesEngineHelper;
import org.smartregister.chw.job.ChwJobCreator;
import org.smartregister.chw.malaria.MalariaLibrary;
import org.smartregister.chw.model.NavigationModelFlv;
import org.smartregister.chw.pnc.PncLibrary;
import org.smartregister.chw.repository.AncRegisterRepository;
import org.smartregister.chw.repository.ChwRepository;
import org.smartregister.chw.repository.HomeVisitIndicatorInfoRepository;
import org.smartregister.chw.repository.HomeVisitRepository;
import org.smartregister.chw.repository.HomeVisitServiceRepository;
import com.opensrp.chw.core.service.CoreAuthorizationService;
import org.smartregister.chw.sync.ChwClientProcessor;
import org.smartregister.chw.util.Utils;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.jsonmapping.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

import static com.opensrp.chw.core.utils.ApplicationUtils.getCommonFtsObject;
import static com.opensrp.chw.core.utils.FormUtils.getFamilyMetadata;

public class ChwApplication extends DrishtiApplication implements CoreApplication {

    private static final int MINIMUM_JOB_FLEX_VALUE = 1;
    private static ClientProcessorForJava clientProcessor;

    private static CommonFtsObject commonFtsObject = null;
    private static HomeVisitRepository homeVisitRepository;
    private static HomeVisitServiceRepository homeVisitServiceRepository;
    private static AncRegisterRepository ancRegisterRepository;
    private static HomeVisitIndicatorInfoRepository homeVisitIndicatorInfoRepository;

    private JsonSpecHelper jsonSpecHelper;
    private ECSyncHelper ecSyncHelper;
    private String password;

    private RulesEngineHelper rulesEngineHelper;

    public static synchronized ChwApplication getInstance() {
        return (ChwApplication) mInstance;
    }

    public static JsonSpecHelper getJsonSpecHelper() {
        return getInstance().jsonSpecHelper;
    }

    public static CommonFtsObject createCommonFtsObject() {
        return getCommonFtsObject(commonFtsObject);
    }

    public static ClientProcessorForJava getClientProcessor(android.content.Context context) {
        if (clientProcessor == null) {
            clientProcessor = ChwClientProcessor.getInstance(context);
//            clientProcessor = FamilyLibrary.getInstance().getClientProcessorForJava();
        }
        return clientProcessor;
    }

    public static HomeVisitRepository homeVisitRepository() {
        if (homeVisitRepository == null) {
            homeVisitRepository = new HomeVisitRepository(getInstance().getRepository(), getInstance().getContext().commonFtsObject(), getInstance().getContext().alertService());
        }
        return homeVisitRepository;
    }

    public static HomeVisitServiceRepository getHomeVisitServiceRepository() {
        if (homeVisitServiceRepository == null) {
            homeVisitServiceRepository = new HomeVisitServiceRepository(getInstance().getRepository());
        }
        return homeVisitServiceRepository;
    }

    public static AncRegisterRepository ancRegisterRepository() {
        if (ancRegisterRepository == null) {
            ancRegisterRepository = new AncRegisterRepository(getInstance().getRepository());
        }
        return ancRegisterRepository;
    }

    public static HomeVisitIndicatorInfoRepository homeVisitIndicatorInfoRepository() {
        if (homeVisitIndicatorInfoRepository == null) {
            homeVisitIndicatorInfoRepository = new HomeVisitIndicatorInfoRepository(getInstance().getRepository());
        }
        return homeVisitIndicatorInfoRepository;
    }

    /**
     * Update application contants to fit current context
     */
    public static Locale getCurrentLocale() {
        return mInstance == null ? Locale.getDefault() : mInstance.getResources().getConfiguration().locale;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Necessary to determine the right form to pick from assets
        Constants.JSON_FORM.setLocaleAndAssetManager(ChwApplication.getCurrentLocale(),
                ChwApplication.getInstance().getApplicationContext().getAssets());


        //Setup Navigation menu. Done only once when app is created
        NavigationMenu.setupNavigationMenu(this, new NavigationMenuFlv(), new NavigationModelFlv(), getRegisteredActivities());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashlyticsTree(ChwApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM()));
        }


        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());


        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());
        //Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().build()).build());

        //Initialize Modules
        P2POptions p2POptions = new P2POptions(true);
        p2POptions.setAuthorizationService(new CoreAuthorizationService());

        CoreLibrary.init(context, new ChwSyncConfiguration(), BuildConfig.BUILD_TIMESTAMP, p2POptions);
        CoreLibrary.getInstance().setEcClientFieldsFile(Constants.EC_CLIENT_FIELDS);

        // init libraries
        ImmunizationLibrary.init(context, getRepository(), null, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        ConfigurableViewsLibrary.init(context, getRepository());
        FamilyLibrary.init(context, getRepository(), getFamilyMetadata(new FamilyProfileActivity()), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        AncLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        PncLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        MalariaLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);

        SyncStatusBroadcastReceiver.init(this);

        LocationHelper.init(new ArrayList<>(Arrays.asList(BuildConfig.ALLOWED_LOCATION_LEVELS)), BuildConfig.DEFAULT_LOCATION);

        // set up processor
        FamilyLibrary.getInstance().setClientProcessorForJava(ChwClientProcessor.getInstance(getApplicationContext()));

        // init json helper
        this.jsonSpecHelper = new JsonSpecHelper(this);

        // Init Reporting library
        ReportingLibrary.init(context, getRepository(), null, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);

        //init Job Manager
        JobManager.create(this).addJobCreator(new ChwJobCreator());

        initOfflineSchedules();
        scheduleJobs();

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
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new ChwRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            Timber.e(e);
        }
        return repository;
    }

    public String getPassword() {
        if (password == null) {
            String username = getContext().allSharedPreferences().fetchRegisteredANM();
            password = getContext().userService().getGroupId(username);
        }
        return password;
    }

    @Override
    public ClientProcessorForJava getClientProcessor() {
        return ChwApplication.getClientProcessor(ChwApplication.getInstance().getApplicationContext());
    }

    @Override
    @NotNull
    public Map<String, Class> getRegisteredActivities() {
        Map<String, Class> registeredActivities = new HashMap<>();
        registeredActivities.put(Constants.REGISTERED_ACTIVITIES.ANC_REGISTER_ACTIVITY, AncRegisterActivity.class);
        registeredActivities.put(Constants.REGISTERED_ACTIVITIES.FAMILY_REGISTER_ACTIVITY, FamilyRegisterActivity.class);
        registeredActivities.put(Constants.REGISTERED_ACTIVITIES.CHILD_REGISTER_ACTIVITY, ChildRegisterActivity.class);
        registeredActivities.put(Constants.REGISTERED_ACTIVITIES.PNC_REGISTER_ACTIVITY, PncRegisterActivity.class);
        return registeredActivities;
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
    public void saveLanguage(String language) {
        ChwApplication.getInstance().getContext().allSharedPreferences().saveLanguagePreference(language);
    }

    @Override
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
        Locale current = getApplicationContext().getResources().getConfiguration().locale;
        saveLanguage(current.getLanguage());
        FamilyLibrary.getInstance().setMetadata(getFamilyMetadata(new FamilyProfileActivity()));
    }

    public VaccineRepository vaccineRepository() {
        return ImmunizationLibrary.getInstance().vaccineRepository();
    }

    public RulesEngineHelper getRulesEngineHelper() {
        if (rulesEngineHelper == null) {
            rulesEngineHelper = new RulesEngineHelper(getApplicationContext());
        }
        return rulesEngineHelper;
    }

    public void initOfflineSchedules() {
        try {
            // child schedules
            List<VaccineGroup> childVaccines = VaccinatorUtils.getSupportedVaccines(this);
            List<Vaccine> specialVaccines = VaccinatorUtils.getSpecialVaccines(this);
            VaccineSchedule.init(childVaccines, specialVaccines, "child");
        } catch (Exception e) {
            Timber.e(e);
        }

        try {
            // mother vaccines
            List<VaccineGroup> womanVaccines = VaccinatorUtils.getSupportedWomanVaccines(this);
            VaccineSchedule.init(womanVaccines, null, "woman");
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void scheduleJobs() {
        // TODO implement job scheduling
        Timber.d("scheduleJobs pending implementation");
    }

    private long getFlexValue(int value) {
        int minutes = MINIMUM_JOB_FLEX_VALUE;

        if (value > MINIMUM_JOB_FLEX_VALUE) {

            minutes = (int) Math.ceil(value / 3);
        }

        return TimeUnit.MINUTES.toMillis(minutes);
    }

    public AllCommonsRepository getAllCommonsRepository(String table) {
        return ChwApplication.getInstance().getContext().allCommonsRepositoryobjects(table);
    }

}
