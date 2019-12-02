package org.smartregister.brac.hnpp;

import android.content.Intent;
import android.text.TextUtils;
import com.evernote.android.job.JobManager;
import org.jetbrains.annotations.NotNull;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.brac.hnpp.activity.HNPPJsonFormActivity;
import org.smartregister.brac.hnpp.activity.HNPPMemberJsonFormActivity;
import org.smartregister.brac.hnpp.activity.HnppAllMemberRegisterActivity;
import org.smartregister.brac.hnpp.activity.HnppAncRegisterActivity;
import org.smartregister.brac.hnpp.custom_view.HnppNavigationTopView;
import org.smartregister.brac.hnpp.listener.HnppNavigationListener;
import org.smartregister.brac.hnpp.presenter.HnppNavigationPresenter;
import org.smartregister.brac.hnpp.repository.HnppChwRepository;
import org.smartregister.brac.hnpp.repository.HnppVisitLogRepository;
import org.smartregister.brac.hnpp.repository.SSLocationRepository;
import org.smartregister.brac.hnpp.repository.HouseholdIdRepository;
import org.smartregister.brac.hnpp.sync.HnppClientProcessor;
import org.smartregister.brac.hnpp.sync.HnppSyncConfiguration;
import org.smartregister.brac.hnpp.utils.HNPPApplicationUtils;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.CoreApplication;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.brac.hnpp.activity.ChildRegisterActivity;
import org.smartregister.brac.hnpp.activity.FamilyProfileActivity;
import org.smartregister.brac.hnpp.activity.FamilyRegisterActivity;
import org.smartregister.brac.hnpp.activity.ReferralRegisterActivity;
import org.smartregister.brac.hnpp.custom_view.HnppNavigationMenu;
import org.smartregister.brac.hnpp.job.HnppJobCreator;
import org.smartregister.brac.hnpp.model.HnppNavigationModel;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.util.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public class HnppApplication extends CoreChwApplication implements CoreApplication {

    private HouseholdIdRepository householdIdRepository;
    private HnppVisitLogRepository hnppVisitLogRepository;
    private static SSLocationRepository locationRepository;
    private static CommonFtsObject commonFtsObject = null;
    private EventClientRepository eventClientRepository;
    @Override
    public void onCreate() {
        super.onCreate();
        //init Job Manager
        SyncStatusBroadcastReceiver.init(this);
        JobManager.create(this).addJobCreator(new HnppJobCreator());

        //Necessary to determine the right form to pick from assets
        CoreConstants.JSON_FORM.setLocaleAndAssetManager(HnppApplication.getCurrentLocale(),
                HnppApplication.getInstance().getApplicationContext().getAssets());

        //Setup Navigation menu. Done only once when app is created
//        NavigationMenu.setupNavigationMenu(new HnppNavigationListener(),null,this, new HnppNavigationTopView(), new HnppNavigationMenu(), new HnppNavigationModel(),
//                getRegisteredActivities(), false);

//        if (BuildConfig.DEBUG) {
//            Timber.plant(new Timber.DebugTree());
//        } else {
//            Timber.plant(new CrashlyticsTree(HnppApplication.getInstance().getContext()
//                    .allSharedPreferences().fetchRegisteredANM()));
//        }

        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

//        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder()
//                .disabled(BuildConfig.DEBUG).build()).build());

        // init json helper
        this.jsonSpecHelper = new JsonSpecHelper(this);
        CoreLibrary.init(context,new HnppSyncConfiguration(),BuildConfig.BUILD_TIMESTAMP);
        ConfigurableViewsLibrary.init(context, getRepository());
        FamilyLibrary.init(context, getRepository(), FormUtils.getFamilyMetadata(new FamilyProfileActivity()), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        ImmunizationLibrary.init(context, getRepository(), null, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        LocationHelper.init(new ArrayList<>(Arrays.asList(BuildConfig.ALLOWED_LOCATION_LEVELS)), BuildConfig.DEFAULT_LOCATION);
        //ReportingLibrary.init(context, getRepository(), null, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        AncLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        //PncLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
       // MalariaLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        setOpenSRPUrl();
        // set up processor
        FamilyLibrary.getInstance().setClientProcessorForJava(HnppClientProcessor.getInstance(getApplicationContext()));




//        Configuration configuration = getApplicationContext().getResources().getConfiguration();
//        String language;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            language = configuration.getLocales().get(0).getLanguage();
//        } else {
//            language = configuration.locale.getLanguage();
//        }
//
//        if (language.equals("bn")) {
//            saveLanguage("bn");
//        }
    }

    public void setupNavigation(HnppNavigationPresenter mPresenter){
        NavigationMenu.setupNavigationMenu(new HnppNavigationListener(),mPresenter,this, new HnppNavigationTopView(), new HnppNavigationMenu(), new HnppNavigationModel(),
                getRegisteredActivities(), false);
    }
    public static CommonFtsObject createCommonFtsObject() {
        return HNPPApplicationUtils.getCommonFtsObject(commonFtsObject);
    }
    public static synchronized HnppApplication getHNPPInstance() {
        return (HnppApplication) mInstance;
    }

    @Override
    public void logoutCurrentUser() {
//        Intent intent = new Intent(this,org.smartregister.brac.hnpp.activity.LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        context.userService().logoutSession();
    }
    @Override
    public void forceLogout() {
        Intent intent = new Intent(this,org.smartregister.brac.hnpp.activity.LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        context.userService().logoutSession();
    }

    public @NotNull Map<String, Class> getRegisteredActivities() {
        Map<String, Class> registeredActivities = new HashMap<>();
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.ANC_REGISTER_ACTIVITY, HnppAncRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.FAMILY_REGISTER_ACTIVITY, FamilyRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.ALL_MEMBER_REGISTER_ACTIVITY, HnppAllMemberRegisterActivity.class);

        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.CHILD_REGISTER_ACTIVITY, ChildRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.PNC_REGISTER_ACTIVITY, FamilyRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.REFERRALS_REGISTER_ACTIVITY, ReferralRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.MALARIA_REGISTER_ACTIVITY, FamilyRegisterActivity.class);
        return registeredActivities;
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new HnppChwRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            Timber.e(e);
        }
        return repository;
    }

    public HnppVisitLogRepository getHnppVisitLogRepository() {
        if (hnppVisitLogRepository == null) {
            hnppVisitLogRepository = new HnppVisitLogRepository(getInstance().getRepository());
        }
        return hnppVisitLogRepository;
    }

    public HouseholdIdRepository getHouseholdIdRepository() {
        if (householdIdRepository == null) {
            householdIdRepository = new HouseholdIdRepository(getInstance().getRepository());
        }
        return householdIdRepository;
    }

    public static SSLocationRepository getSSLocationRepository() {
        if ( locationRepository == null) {
            locationRepository = new SSLocationRepository(getInstance().getRepository());
        }
        return locationRepository;
    }

    public void setOpenSRPUrl() {
        AllSharedPreferences preferences = Utils.getAllSharedPreferences();
        if(TextUtils.isEmpty(preferences.getPreference(AllConstants.DRISHTI_BASE_URL))){
          preferences.savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url);

        }

    }
    public EventClientRepository getEventClientRepository() {
        if (eventClientRepository == null) {
            eventClientRepository = new EventClientRepository(getRepository());
        }
        return eventClientRepository;
    }
    @Override
    public FamilyMetadata getMetadata() {
        FamilyMetadata metadata = new FamilyMetadata(HNPPJsonFormActivity.class, HNPPMemberJsonFormActivity.class, FamilyProfileActivity.class, CoreConstants.IDENTIFIER.UNIQUE_IDENTIFIER_KEY, false);
        metadata.updateFamilyRegister(CoreConstants.JSON_FORM.getFamilyRegister(), CoreConstants.TABLE_NAME.FAMILY, CoreConstants.EventType.FAMILY_REGISTRATION, CoreConstants.EventType.UPDATE_FAMILY_REGISTRATION, CoreConstants.CONFIGURATION.FAMILY_REGISTER, CoreConstants.RELATIONSHIP.FAMILY_HEAD, CoreConstants.RELATIONSHIP.PRIMARY_CAREGIVER);
        metadata.updateFamilyMemberRegister(CoreConstants.JSON_FORM.getFamilyMemberRegister(), CoreConstants.TABLE_NAME.FAMILY_MEMBER, CoreConstants.EventType.FAMILY_MEMBER_REGISTRATION, CoreConstants.EventType.UPDATE_FAMILY_MEMBER_REGISTRATION, CoreConstants.CONFIGURATION.FAMILY_MEMBER_REGISTER, CoreConstants.RELATIONSHIP.FAMILY);
        metadata.updateFamilyDueRegister(CoreConstants.TABLE_NAME.CHILD, Integer.MAX_VALUE, false);
        metadata.updateFamilyActivityRegister(CoreConstants.TABLE_NAME.CHILD_ACTIVITY, Integer.MAX_VALUE, false);
        metadata.updateFamilyOtherMemberRegister(CoreConstants.TABLE_NAME.FAMILY_MEMBER, Integer.MAX_VALUE, false);
        return metadata;
    }
}
