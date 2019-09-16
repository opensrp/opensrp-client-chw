package org.smartregister.chw.core.application;

import android.content.Intent;

import org.smartregister.Context;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.contract.CoreApplication;
import org.smartregister.chw.core.helper.RulesEngineHelper;
import org.smartregister.chw.core.repository.AncRegisterRepository;
import org.smartregister.chw.core.repository.WashCheckRepository;
import org.smartregister.chw.core.sync.ChwClientProcessor;
import org.smartregister.chw.core.utils.ApplicationUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.activity.FamilyWizardFormActivity;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.jsonmapping.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.repository.TaskNotesRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.activity.LoginActivity;

import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class CoreChwApplication extends DrishtiApplication implements CoreApplication {

    private static ClientProcessorForJava clientProcessor;

    private static CommonFtsObject commonFtsObject = null;
    private static AncRegisterRepository ancRegisterRepository;
    private static TaskRepository taskRepository;
    private static PlanDefinitionRepository planDefinitionRepository;
    private static WashCheckRepository washCheckRepository;
    public JsonSpecHelper jsonSpecHelper;
    private LocationRepository locationRepository;
    private ECSyncHelper ecSyncHelper;
    private String password;

    private RulesEngineHelper rulesEngineHelper;

    public static JsonSpecHelper getJsonSpecHelper() {
        return getInstance().jsonSpecHelper;
    }

    public static synchronized CoreChwApplication getInstance() {
        return (CoreChwApplication) mInstance;
    }

    public static CommonFtsObject createCommonFtsObject() {
        return ApplicationUtils.getCommonFtsObject(commonFtsObject);
    }

    public static AncRegisterRepository ancRegisterRepository() {
        if (ancRegisterRepository == null) {
            ancRegisterRepository = new AncRegisterRepository(getInstance().getRepository());
        }
        return ancRegisterRepository;
    }

    public static WashCheckRepository getWashCheckRepository() {
        if (washCheckRepository == null) {
            washCheckRepository = new WashCheckRepository(getInstance().getRepository());
        }
        return washCheckRepository;
    }

    /**
     * Update application contants to fit current context
     */
    public static Locale getCurrentLocale() {
        return mInstance == null ? Locale.getDefault() : mInstance.getResources().getConfiguration().locale;
    }

    public TaskRepository getTaskRepository() {
        if (taskRepository == null) {
            taskRepository = new TaskRepository(getRepository(), new TaskNotesRepository(getRepository()));
        }
        return taskRepository;
    }

    public PlanDefinitionRepository getPlanDefinitionRepository() {
        if (planDefinitionRepository == null) {
            planDefinitionRepository = new PlanDefinitionRepository(getRepository());
        }
        return planDefinitionRepository;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void logoutCurrentUser() {
        //TODO need to open in production
//        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        getApplicationContext().startActivity(intent);
//        context.userService().logoutSession();
    }
    public void forceLogout() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }


    @Override
    public String getPassword() {
        if (password == null) {
            String username = getContext().allSharedPreferences().fetchRegisteredANM();
            password = getContext().userService().getGroupId(username);
        }
        return password;
    }

    @Override
    public ClientProcessorForJava getClientProcessor() {
        return CoreChwApplication.getClientProcessor(CoreChwApplication.getInstance().getApplicationContext());
    }

    public static ClientProcessorForJava getClientProcessor(android.content.Context context) {
        if (clientProcessor == null) {
            clientProcessor = ChwClientProcessor.getInstance(context);
        }
        return clientProcessor;
    }

    public VaccineRepository vaccineRepository() {
        return ImmunizationLibrary.getInstance().vaccineRepository();
    }

    public LocationRepository getLocationRepository() {
        if (locationRepository == null) {
            locationRepository = new LocationRepository(getRepository());
        }
        return locationRepository;
    }

    public void initOfflineSchedules() {
        try {
            // child schedules
            List<VaccineGroup> childVaccines = VaccinatorUtils.getSupportedVaccines(this);
            List<Vaccine> specialVaccines = VaccinatorUtils.getSpecialVaccines(this);
            VaccineSchedule.init(childVaccines, specialVaccines, CoreConstants.SERVICE_GROUPS.CHILD);
        } catch (Exception e) {
            Timber.e(e);
        }

        try {
            // mother vaccines
            List<VaccineGroup> womanVaccines = VaccinatorUtils.getSupportedWomanVaccines(this);
            VaccineSchedule.init(womanVaccines, null, CoreConstants.SERVICE_GROUPS.WOMAN);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public AllCommonsRepository getAllCommonsRepository(String table) {
        return CoreChwApplication.getInstance().getContext().allCommonsRepositoryobjects(table);
    }

    @Override
    public void saveLanguage(String language) {
        CoreChwApplication.getInstance().getContext().allSharedPreferences().saveLanguagePreference(language);
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
        CoreConstants.JSON_FORM.setLocaleAndAssetManager(current, getAssets());
        FamilyLibrary.getInstance().setMetadata(getMetadata());
    }

    @Override
    public RulesEngineHelper getRulesEngineHelper() {
        if (rulesEngineHelper == null) {
            rulesEngineHelper = new RulesEngineHelper(getApplicationContext());
        }
        return rulesEngineHelper;
    }

    public FamilyMetadata getMetadata() {
        FamilyMetadata metadata = new FamilyMetadata(FamilyWizardFormActivity.class, FamilyWizardFormActivity.class, CoreFamilyProfileActivity.class, CoreConstants.IDENTIFIER.UNIQUE_IDENTIFIER_KEY, false);
        metadata.updateFamilyRegister(CoreConstants.JSON_FORM.getFamilyRegister(), CoreConstants.TABLE_NAME.FAMILY, CoreConstants.EventType.FAMILY_REGISTRATION, CoreConstants.EventType.UPDATE_FAMILY_REGISTRATION, CoreConstants.CONFIGURATION.FAMILY_REGISTER, CoreConstants.RELATIONSHIP.FAMILY_HEAD, CoreConstants.RELATIONSHIP.PRIMARY_CAREGIVER);
        metadata.updateFamilyMemberRegister(CoreConstants.JSON_FORM.getFamilyMemberRegister(), CoreConstants.TABLE_NAME.FAMILY_MEMBER, CoreConstants.EventType.FAMILY_MEMBER_REGISTRATION, CoreConstants.EventType.UPDATE_FAMILY_MEMBER_REGISTRATION, CoreConstants.CONFIGURATION.FAMILY_MEMBER_REGISTER, CoreConstants.RELATIONSHIP.FAMILY);
        metadata.updateFamilyDueRegister(CoreConstants.TABLE_NAME.CHILD, Integer.MAX_VALUE, false);
        metadata.updateFamilyActivityRegister(CoreConstants.TABLE_NAME.CHILD_ACTIVITY, Integer.MAX_VALUE, false);
        metadata.updateFamilyOtherMemberRegister(CoreConstants.TABLE_NAME.FAMILY_MEMBER, Integer.MAX_VALUE, false);
        return metadata;
    }
}
