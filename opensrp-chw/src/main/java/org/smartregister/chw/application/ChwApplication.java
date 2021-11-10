package org.smartregister.chw.application;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.evernote.android.job.JobManager;
import com.vijay.jsonwizard.NativeFormLibrary;
import com.vijay.jsonwizard.domain.Form;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.koin.core.context.GlobalContextKt;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.P2POptions;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.activity.AllClientsRegisterActivity;
import org.smartregister.chw.activity.AncRegisterActivity;
import org.smartregister.chw.activity.ChildRegisterActivity;
import org.smartregister.chw.activity.FamilyProfileActivity;
import org.smartregister.chw.activity.FamilyRegisterActivity;
import org.smartregister.chw.activity.FpRegisterActivity;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.activity.MalariaRegisterActivity;
import org.smartregister.chw.activity.PncRegisterActivity;
import org.smartregister.chw.activity.ReferralRegisterActivity;
import org.smartregister.chw.activity.UpdatesRegisterActivity;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.configs.AllClientsRegisterRowOptions;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.loggers.CrashlyticsTree;
import org.smartregister.chw.core.provider.CoreAllClientsRegisterQueryProvider;
import org.smartregister.chw.core.service.CoreAuthorizationService;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.chw.custom_view.NavigationMenuFlv;
import org.smartregister.chw.fp.FpLibrary;
import org.smartregister.chw.job.ChwJobCreator;
import org.smartregister.chw.malaria.MalariaLibrary;
import org.smartregister.chw.model.NavigationModelFlv;
import org.smartregister.chw.pnc.PncLibrary;
import org.smartregister.chw.referral.ReferralLibrary;
import org.smartregister.chw.repository.ChwRepository;
import org.smartregister.chw.schedulers.ChwScheduleTaskExecutor;
import org.smartregister.chw.service.ChildAlertService;
import org.smartregister.chw.sync.ChwClientProcessor;
import org.smartregister.chw.util.ChwLocationBasedClassifier;
import org.smartregister.chw.util.FailSafeRecalledID;
import org.smartregister.chw.util.FileUtils;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.chw.util.Utils;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.Constants;
import org.smartregister.growthmonitoring.GrowthMonitoringConfig;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.sync.P2PClassifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class ChwApplication extends CoreChwApplication {

    private static Flavor flavor = new ChwApplicationFlv();
    private AppExecutors appExecutors;

    public static Flavor getApplicationFlavor() {
        return flavor;
    }

    public static void prepareGuideBooksFolder() {
        String rootFolder = getGuideBooksDirectory();
        createFolders(rootFolder, false);
        boolean onSdCard = FileUtils.canWriteToExternalDisk();
        if (onSdCard)
            createFolders(rootFolder, true);
    }

    private static void createFolders(String rootFolder, boolean onSdCard) {
        try {
            FileUtils.createDirectory(rootFolder, onSdCard);
        } catch (Exception e) {
            Timber.v(e);
        }
    }

    public static String getGuideBooksDirectory() {
        String[] packageName = ChwApplication.getInstance().getContext().applicationContext().getPackageName().split("\\.");
        String suffix = packageName[packageName.length - 1];
        return "opensrp_guidebooks_" + (suffix.equalsIgnoreCase("chw") ? "liberia" : suffix);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        //Necessary to determine the right form to pick from assets
        CoreConstants.JSON_FORM.setLocaleAndAssetManager(ChwApplication.getCurrentLocale(),
                ChwApplication.getInstance().getApplicationContext().getAssets());

        //Setup Navigation menu. Done only once when app is created
        NavigationMenu.setupNavigationMenu(this, new NavigationMenuFlv(), new NavigationModelFlv(),
                getRegisteredActivities(), flavor.hasP2P());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashlyticsTree(ChwApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM()));
        }

        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());

        initializeLibraries();

        // init json helper
        this.jsonSpecHelper = new JsonSpecHelper(this);

        //init Job Manager
        JobManager.create(this).addJobCreator(new ChwJobCreator());

        initOfflineSchedules();

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

        // create a folder for guidebooks
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                prepareGuideBooksFolder();
            }
        } else {
            prepareGuideBooksFolder();
        }

        EventBus.getDefault().register(this);
    }

    private void initializeLibraries() {
        //Initialize Modules
        P2POptions p2POptions = new P2POptions(true);
        p2POptions.setAuthorizationService(flavor.hasForeignData() ? new LmhAuthorizationService() : new CoreAuthorizationService());
        p2POptions.setRecalledIdentifier(new FailSafeRecalledID());

        CoreLibrary.init(context, new ChwSyncConfiguration(), BuildConfig.BUILD_TIMESTAMP, p2POptions);
        CoreLibrary.getInstance().setEcClientFieldsFile(CoreConstants.EC_CLIENT_FIELDS);

        // init libraries
        ImmunizationLibrary.init(context, getRepository(), null, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        ImmunizationLibrary.getInstance().setAllowSyncImmediately(flavor.saveOnSubmission());

        ConfigurableViewsLibrary.init(context);
        FamilyLibrary.init(context, getMetadata(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        AncLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        AncLibrary.getInstance().setSubmitOnSave(flavor.saveOnSubmission());

        PncLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        MalariaLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        FpLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        // Init Reporting library
        ReportingLibrary.init(context, getRepository(), null, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        GrowthMonitoringConfig growthMonitoringConfig = new GrowthMonitoringConfig();
        growthMonitoringConfig.setWeightForHeightZScoreFile("weight_for_height.csv");
        GrowthMonitoringLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION, growthMonitoringConfig);

        if (hasReferrals()) {
            //Setup referral library
            ReferralLibrary.init(this);
            ReferralLibrary.getInstance().setAppVersion(BuildConfig.VERSION_CODE);
            ReferralLibrary.getInstance().setDatabaseVersion(BuildConfig.DATABASE_VERSION);
        }

        OpdLibrary.init(context, getRepository(),
                new OpdConfiguration.Builder(CoreAllClientsRegisterQueryProvider.class)
                        .setBottomNavigationEnabled(true)
                        .setOpdRegisterRowOptions(AllClientsRegisterRowOptions.class)
                        .build(),
                BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION
        );

        SyncStatusBroadcastReceiver.init(this);

        LocationHelper.init(new ArrayList<>(Arrays.asList(BuildConfig.DEBUG ? BuildConfig.ALLOWED_LOCATION_LEVELS_DEBUG : BuildConfig.ALLOWED_LOCATION_LEVELS)), BuildConfig.DEBUG ? BuildConfig.DEFAULT_LOCATION_DEBUG : BuildConfig.DEFAULT_LOCATION);

        // set up processor
        FamilyLibrary.getInstance().setClientProcessorForJava(ChwClientProcessor.getInstance(getApplicationContext()));

        // Set display date format for date pickers in native forms
        Form form = new Form();
        form.setDatePickerDisplayFormat("dd MMM yyyy");

        NativeFormLibrary.getInstance().setClientFormDao(CoreLibrary.getInstance().context().getClientFormRepository());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        GlobalContextKt.stopKoin();
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
    public FamilyMetadata getMetadata() {
        FamilyMetadata metadata = FormUtils.getFamilyMetadata(new FamilyProfileActivity(), getDefaultLocationLevel(), getFacilityHierarchy(), getFamilyLocationFields());

        HashMap<String, String> setting = new HashMap<>();
        setting.put(Constants.CustomConfig.FAMILY_FORM_IMAGE_STEP, JsonFormUtils.STEP1);
        setting.put(Constants.CustomConfig.FAMILY_MEMBER_FORM_IMAGE_STEP, JsonFormUtils.STEP2);
        metadata.setCustomConfigs(setting);
        return metadata;
    }

    @Override
    public ArrayList<String> getAllowedLocationLevels() {
        return new ArrayList<>(Arrays.asList(BuildConfig.DEBUG ? BuildConfig.ALLOWED_LOCATION_LEVELS_DEBUG : BuildConfig.ALLOWED_LOCATION_LEVELS));
    }

    @Override
    public ArrayList<String> getFacilityHierarchy() {
        return new ArrayList<>(Arrays.asList(BuildConfig.LOCATION_HIERACHY));
    }

    @Override
    public String getDefaultLocationLevel() {
        return BuildConfig.DEBUG ? BuildConfig.DEFAULT_LOCATION_DEBUG : BuildConfig.DEFAULT_LOCATION;
    }

    @NotNull
    public Map<String, Class> getRegisteredActivities() {
        Map<String, Class> registeredActivities = new HashMap<>();
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.ANC_REGISTER_ACTIVITY, AncRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.FAMILY_REGISTER_ACTIVITY, FamilyRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.CHILD_REGISTER_ACTIVITY, ChildRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.PNC_REGISTER_ACTIVITY, PncRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.MALARIA_REGISTER_ACTIVITY, MalariaRegisterActivity.class);
        if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH) {
            registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.REFERRALS_REGISTER_ACTIVITY, ReferralRegisterActivity.class);
        }
        if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH && BuildConfig.BUILD_FOR_BORESHA_AFYA_SOUTH) {
            registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.ALL_CLIENTS_REGISTERED_ACTIVITY, AllClientsRegisterActivity.class);
        }
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.FP_REGISTER_ACTIVITY, FpRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.UPDATES_REGISTER_ACTIVITY, UpdatesRegisterActivity.class);
        return registeredActivities;
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

    public void setOpenSRPUrl() {
        AllSharedPreferences preferences = Utils.getAllSharedPreferences();
        preferences.savePreference(AllConstants.DRISHTI_BASE_URL,
                BuildConfig.DEBUG ? BuildConfig.opensrp_url_debug : BuildConfig.opensrp_url
        );
    }

    public boolean hasReferrals() {
        return flavor.hasReferrals();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVisitEvent(Visit visit) {
        if (visit != null) {
            Timber.v("Visit Submitted re processing Schedule for event ' %s '  : %s", visit.getVisitType(), visit.getBaseEntityId());
            ChwScheduleTaskExecutor.getInstance().execute(visit.getBaseEntityId(), visit.getVisitType(), visit.getDate());

            ChildAlertService.updateAlerts(visit.getBaseEntityId());
        }
    }

    public AppExecutors getAppExecutors() {
        if (appExecutors == null) {
            appExecutors = new AppExecutors();
        }
        return appExecutors;
    }

    @Override
    public P2PClassifier<JSONObject> getP2PClassifier() {
        return flavor.hasForeignData() ? new ChwLocationBasedClassifier() : null;
    }

    @Override
    public boolean getChildFlavorUtil() {
        return flavor.getChildFlavorUtil();
    }

    public interface Flavor {
        boolean hasP2P();

        boolean syncUsingPost();

        boolean hasReferrals();

        boolean hasANC();

        boolean hasPNC();

        boolean hasChildSickForm();

        boolean hasFamilyPlanning();

        boolean hasMalaria();

        boolean hasWashCheck();

        boolean hasFamilyKitCheck();

        boolean hasRoutineVisit();

        boolean hasServiceReport();

        boolean hasStockUsageReport();

        boolean hasJobAids();

        boolean hasQR();

        boolean hasPinLogin();

        boolean hasReports();

        boolean hasTasks();

        boolean hasDefaultDueFilterForChildClient();

        boolean launchChildClientsAtLogin();

        boolean hasJobAidsVitaminAGraph();

        boolean hasJobAidsDewormingGraph();

        boolean hasChildrenMNPSupplementationGraph();

        boolean hasJobAidsBreastfeedingGraph();

        boolean hasJobAidsBirthCertificationGraph();

        boolean hasSurname();

        boolean showMyCommunityActivityReport();

        boolean useThinkMd();

        boolean hasFamilyLocationRow();

        boolean usesPregnancyRiskProfileLayout();

        boolean splitUpcomingServicesView();

        boolean getChildFlavorUtil();

        boolean showChildrenUnder5();

        boolean hasForeignData();

        boolean showNoDueVaccineView();

        boolean prioritizeChildNameOnChildRegister();

        boolean showChildrenUnderFiveAndGirlsAgeNineToEleven();

        boolean dueVaccinesFilterInChildRegister();

        boolean includeCurrentChild();

        boolean saveOnSubmission();

        boolean relaxVisitDateRestrictions();

        boolean showLastNameOnChildProfile();

        boolean showChildrenAboveTwoDueStatus();

        boolean showFamilyServicesScheduleWithChildrenAboveTwo();

        boolean showIconsForChildrenUnderTwoAndGirlsAgeNineToEleven();

        boolean useAllChildrenTitle();

        boolean showDueFilterToggle();

        boolean hideCaregiverAndFamilyHeadWhenOnlyOneAdult();

    }

}
