package org.smartregister.chw.core.application;

import android.content.Intent;
import android.util.Pair;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.Context;
import org.smartregister.chw.core.contract.CoreApplication;
import org.smartregister.chw.core.helper.RulesEngineHelper;
import org.smartregister.chw.core.repository.AncRegisterRepository;
import org.smartregister.chw.core.repository.MalariaRegisterRepository;
import org.smartregister.chw.core.repository.PncRegisterRepository;
import org.smartregister.chw.core.repository.ScheduleRepository;
import org.smartregister.chw.core.repository.WashCheckRepository;
import org.smartregister.chw.core.sync.CoreClientProcessor;
import org.smartregister.chw.core.utils.ApplicationUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.family.FamilyLibrary;
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
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.activity.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public abstract class CoreChwApplication extends DrishtiApplication implements CoreApplication {

    private static ClientProcessorForJava clientProcessor;

    private static CommonFtsObject commonFtsObject = null;
    private static AncRegisterRepository ancRegisterRepository;
    protected static TaskRepository taskRepository;
    private static PncRegisterRepository pncRegisterRepository;
    private static PlanDefinitionRepository planDefinitionRepository;
    private static WashCheckRepository washCheckRepository;
    private static ScheduleRepository scheduleRepository;
    private static MalariaRegisterRepository malariaRegisterRepository;
    public JsonSpecHelper jsonSpecHelper;
    private LocationRepository locationRepository;
    private ECSyncHelper ecSyncHelper;
    private String password;
    protected ClientProcessorForJava clientProcessorForJava;
    private UniqueIdRepository uniqueIdRepository;

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

    public static PncRegisterRepository pncRegisterRepository() {
        if (pncRegisterRepository == null) {
            pncRegisterRepository = new PncRegisterRepository(getInstance().getRepository());
        }
        return pncRegisterRepository;
    }

    public static WashCheckRepository getWashCheckRepository() {
        if (washCheckRepository == null) {
            washCheckRepository = new WashCheckRepository(getInstance().getRepository());
        }
        return washCheckRepository;
    }

    public static MalariaRegisterRepository malariaRegisterRepository() {
        if (malariaRegisterRepository == null) {
            malariaRegisterRepository = new MalariaRegisterRepository(getInstance().getRepository());
        }

        return malariaRegisterRepository;
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

    public ScheduleRepository getScheduleRepository() {
        if (scheduleRepository == null) {
            scheduleRepository = new ScheduleRepository(getRepository());
        }
        return scheduleRepository;
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
            clientProcessor = CoreClientProcessor.getInstance(context);
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

    public ClientProcessorForJava getClientProcessorForJava() {
        if (this.clientProcessorForJava == null) {
            this.clientProcessorForJava = ClientProcessorForJava.getInstance(getContext().applicationContext());
        }

        return this.clientProcessorForJava;
    }

    public UniqueIdRepository getUniqueIdRepository() {
        if (this.uniqueIdRepository == null) {
            this.uniqueIdRepository = new UniqueIdRepository(this.getRepository());
        }

        return this.uniqueIdRepository;
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

    @Override
    public ArrayList<Pair<String, String>> getFamilyLocationFields() {
        ArrayList<Pair<String, String>> list = new ArrayList<>();
        list.add(Pair.create(JsonFormConstants.STEP1, "nearest_facility"));
        return list;
    }
}
