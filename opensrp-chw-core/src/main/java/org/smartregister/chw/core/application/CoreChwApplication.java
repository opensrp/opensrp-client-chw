package org.smartregister.chw.core.application;

import android.content.Intent;

import org.smartregister.Context;
import org.smartregister.chw.core.contract.CoreApplication;
import org.smartregister.chw.core.helper.RulesEngineHelper;
import org.smartregister.chw.core.repository.AncRegisterRepository;
import org.smartregister.chw.core.repository.HomeVisitIndicatorInfoRepository;
import org.smartregister.chw.core.repository.HomeVisitRepository;
import org.smartregister.chw.core.repository.HomeVisitServiceRepository;
import org.smartregister.chw.core.repository.WashCheckRepository;
import org.smartregister.chw.core.sync.ChwClientProcessor;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
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

import static org.smartregister.chw.core.utils.ApplicationUtils.getCommonFtsObject;

public class CoreChwApplication extends DrishtiApplication implements CoreApplication {

    private static ClientProcessorForJava clientProcessor;

    private static CommonFtsObject commonFtsObject = null;
    private static HomeVisitRepository homeVisitRepository;
    private static HomeVisitServiceRepository homeVisitServiceRepository;
    private static AncRegisterRepository ancRegisterRepository;
    private static HomeVisitIndicatorInfoRepository homeVisitIndicatorInfoRepository;
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
        return getCommonFtsObject(commonFtsObject);
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
//            clientProcessor = FamilyLibrary.getInstance().getClientProcessorForJava();
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
    }    @Override
    public void saveLanguage(String language) {
        CoreChwApplication.getInstance().getContext().allSharedPreferences().saveLanguagePreference(language);
    }

    public void scheduleJobs() {
        // TODO implement job scheduling
        Timber.d("scheduleJobs pending implementation");
    }

    public AllCommonsRepository getAllCommonsRepository(String table) {
        return CoreChwApplication.getInstance().getContext().allCommonsRepositoryobjects(table);
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
    }


    @Override
    public RulesEngineHelper getRulesEngineHelper() {
        if (rulesEngineHelper == null) {
            rulesEngineHelper = new RulesEngineHelper(getApplicationContext());
        }
        return rulesEngineHelper;
    }


}
