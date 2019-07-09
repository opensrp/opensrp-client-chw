package org.smartregister.chw.application;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.evernote.android.job.JobManager;

import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.P2POptions;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.activity.FamilyProfileActivity;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.helper.RulesEngineHelper;
import org.smartregister.chw.job.ChwJobCreator;
import org.smartregister.chw.malaria.MalariaLibrary;
import org.smartregister.chw.repository.AncRegisterRepository;
import org.smartregister.chw.repository.ChwRepository;
import org.smartregister.chw.repository.HomeVisitIndicatorInfoRepository;
import org.smartregister.chw.repository.HomeVisitRepository;
import org.smartregister.chw.repository.HomeVisitServiceRepository;
import org.smartregister.chw.service.ChwAuthorizationService;
import org.smartregister.chw.sync.ChwClientProcessor;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.Utils;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.activity.FamilyWizardFormActivity;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.family.util.DBConstants;
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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class ChwApplication extends DrishtiApplication {

    private static final String TAG = ChwApplication.class.getCanonicalName();
    private static final int MINIMUM_JOB_FLEX_VALUE = 1;
    private static ClientProcessorForJava clientProcessor;

    private static CommonFtsObject commonFtsObject;
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
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            }
        }
        return commonFtsObject;
    }

    private static String[] getFtsTables() {
        return new String[]{Constants.TABLE_NAME.FAMILY, Constants.TABLE_NAME.FAMILY_MEMBER, Constants.TABLE_NAME.CHILD};
    }

    private static String[] getFtsSearchFields(String tableName) {
        if (tableName.equals(Constants.TABLE_NAME.FAMILY)) {
            return new String[]{DBConstants.KEY.BASE_ENTITY_ID, DBConstants.KEY.VILLAGE_TOWN, DBConstants.KEY.FIRST_NAME,
                    DBConstants.KEY.LAST_NAME, DBConstants.KEY.UNIQUE_ID};
        } else if (tableName.equals(Constants.TABLE_NAME.FAMILY_MEMBER) || tableName.equals(Constants.TABLE_NAME.CHILD)) {
            return new String[]{DBConstants.KEY.BASE_ENTITY_ID, DBConstants.KEY.FIRST_NAME, DBConstants.KEY.MIDDLE_NAME,
                    DBConstants.KEY.LAST_NAME, DBConstants.KEY.UNIQUE_ID};
        }
        return null;
    }

    private static String[] getFtsSortFields(String tableName) {
        if (tableName.equals(Constants.TABLE_NAME.FAMILY)) {
            return new String[]{DBConstants.KEY.LAST_INTERACTED_WITH, DBConstants.KEY.DATE_REMOVED, DBConstants.KEY.FAMILY_HEAD, DBConstants.KEY.PRIMARY_CAREGIVER};
        } else if (tableName.equals(Constants.TABLE_NAME.FAMILY_MEMBER)) {
            return new String[]{DBConstants.KEY.DOB, DBConstants.KEY.DOD, DBConstants.KEY
                    .LAST_INTERACTED_WITH, DBConstants.KEY.DATE_REMOVED};
        } else if (tableName.equals(Constants.TABLE_NAME.CHILD)) {
            return new String[]{ChildDBConstants.KEY.LAST_HOME_VISIT, ChildDBConstants.KEY.VISIT_NOT_DONE, DBConstants.KEY
                    .LAST_INTERACTED_WITH, ChildDBConstants.KEY.DATE_CREATED, DBConstants.KEY.DATE_REMOVED, DBConstants.KEY.DOB};
        }
        return null;
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

        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        Timber.plant(new Timber.DebugTree());

        //Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());
        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().build()).build());

        //Initialize Modules
        P2POptions p2POptions = new P2POptions(true);
        p2POptions.setAuthorizationService(new ChwAuthorizationService());

        CoreLibrary.init(context, new ChwSyncConfiguration(), BuildConfig.BUILD_TIMESTAMP, p2POptions);
        CoreLibrary.getInstance().setEcClientFieldsFile(Constants.EC_CLIENT_FIELDS);

        // init libraries
        ImmunizationLibrary.init(context, getRepository(), null, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        ConfigurableViewsLibrary.init(context, getRepository());
        FamilyLibrary.init(context, getRepository(), getMetadata(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        AncLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
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

    public void setOpenSRPUrl() {
        AllSharedPreferences preferences = Utils.getAllSharedPreferences();
        if (BuildConfig.DEBUG) {
            preferences.savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url_debug);
        } else {
            preferences.savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url);
        }
    }

    private void saveLanguage(String language) {
        AllSharedPreferences allSharedPreferences = ChwApplication.getInstance().getContext().allSharedPreferences();
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

    private FamilyMetadata getMetadata() {
        FamilyMetadata metadata = new FamilyMetadata(FamilyWizardFormActivity.class, FamilyWizardFormActivity.class, FamilyProfileActivity.class, Constants.IDENTIFIER.UNIQUE_IDENTIFIER_KEY, false);
        metadata.updateFamilyRegister(Constants.JSON_FORM.getFamilyRegister(), Constants.TABLE_NAME.FAMILY, Constants.EventType.FAMILY_REGISTRATION, Constants.EventType.UPDATE_FAMILY_REGISTRATION, Constants.CONFIGURATION.FAMILY_REGISTER, Constants.RELATIONSHIP.FAMILY_HEAD, Constants.RELATIONSHIP.PRIMARY_CAREGIVER);
        metadata.updateFamilyMemberRegister(Constants.JSON_FORM.getFamilyMemberRegister(), Constants.TABLE_NAME.FAMILY_MEMBER, Constants.EventType.FAMILY_MEMBER_REGISTRATION, Constants.EventType.UPDATE_FAMILY_MEMBER_REGISTRATION, Constants.CONFIGURATION.FAMILY_MEMBER_REGISTER, Constants.RELATIONSHIP.FAMILY);
        metadata.updateFamilyDueRegister(Constants.TABLE_NAME.CHILD, Integer.MAX_VALUE, false);
        metadata.updateFamilyActivityRegister(Constants.TABLE_NAME.CHILD_ACTIVITY, Integer.MAX_VALUE, false);
        metadata.updateFamilyOtherMemberRegister(Constants.TABLE_NAME.FAMILY_MEMBER, Integer.MAX_VALUE, false);
        return metadata;
    }

    public void notifyAppContextChange() {
        Locale current = getApplicationContext().getResources().getConfiguration().locale;
        saveLanguage(current.getLanguage());
        FamilyLibrary.getInstance().setMetadata(getMetadata());
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
        Log.d(TAG, "scheduleJobs pending implementation");
    }

    private long getFlexValue(int value) {
        int minutes = MINIMUM_JOB_FLEX_VALUE;

        if (value > MINIMUM_JOB_FLEX_VALUE) {

            minutes = (int) Math.ceil(value / 3);
        }

        return TimeUnit.MINUTES.toMillis(minutes);
    }

    public ECSyncHelper getEcSyncHelper() {
        if (ecSyncHelper == null) {
            ecSyncHelper = ECSyncHelper.getInstance(getApplicationContext());
        }
        return ecSyncHelper;
    }

    public AllCommonsRepository getAllCommonsRepository(String table) {
        return ChwApplication.getInstance().getContext().allCommonsRepositoryobjects(table);
    }

    @Override
    public ClientProcessorForJava getClientProcessor() {
        return ChwApplication.getClientProcessor(ChwApplication.getInstance().getApplicationContext());
    }


}
