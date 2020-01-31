package org.smartregister.chw.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.ScheduleDao;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.chw.schedulers.ChwScheduleTaskExecutor;

import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Recompute all schedules to adjust the new dates
 */
public class SchedulesIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * <p>
     * Used to name the worker thread, important only for debugging.
     */

    public SchedulesIntentService() {
        super("SchedulesIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // execute all children schedules
        executeChildVisitSchedules();

        // execute all anc schedules
        if (ChwApplication.getApplicationFlavor().hasANC())
            executeAncVisitSchedules();

        // execute all pnc schedules
        if (ChwApplication.getApplicationFlavor().hasPNC())
            executePncVisitSchedules();

        // execute all wash check
        if (ChwApplication.getApplicationFlavor().hasWashCheck())
            executeWashCheckSchedules();

        // execute all fp schedules
        if (ChwApplication.getApplicationFlavor().hasFamilyPlanning())
            executeFpVisitSchedules();

        if (ChwApplication.getApplicationFlavor().hasRoutineVisit())
            executeRoutineHouseholdSchedules();
    }

    private void executeChildVisitSchedules() {
        Timber.v("Computing child schedules");
        ChwApplication.getInstance().getScheduleRepository().deleteSchedulesNotCreatedToday(CoreConstants.SCHEDULE_TYPES.CHILD_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        List<String> baseEntityIDs = ScheduleDao.getActiveChildren(CoreConstants.SCHEDULE_TYPES.CHILD_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        if (baseEntityIDs == null) return;

        for (String baseID : baseEntityIDs) {
            Timber.v("  Computing child schedules for %s", baseID);
            ChwScheduleTaskExecutor.getInstance().execute(baseID, CoreConstants.EventType.CHILD_HOME_VISIT, new Date());
            ChildAlertService.updateAlerts(baseID);
        }
    }

    private void executeAncVisitSchedules() {
        Timber.v("Computing ANC schedules");
        ChwApplication.getInstance().getScheduleRepository().deleteSchedulesNotCreatedToday(CoreConstants.SCHEDULE_TYPES.ANC_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        List<String> baseEntityIDs = ScheduleDao.getActiveANCWomen(CoreConstants.SCHEDULE_TYPES.ANC_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        if (baseEntityIDs == null) return;

        for (String baseID : baseEntityIDs) {
            Timber.v("  Computing ANC schedules for %s", baseID);
            ChwScheduleTaskExecutor.getInstance().execute(baseID, CoreConstants.EventType.ANC_REGISTRATION, new Date());
        }
    }

    private void executePncVisitSchedules() {
        Timber.v("Computing PNC schedules");
        ChwApplication.getInstance().getScheduleRepository().deleteSchedulesNotCreatedToday(CoreConstants.SCHEDULE_TYPES.PNC_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        List<String> baseEntityIDs = ScheduleDao.getActivePNCWomen(CoreConstants.SCHEDULE_TYPES.PNC_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        if (baseEntityIDs == null) return;

        for (String baseID : baseEntityIDs) {
            Timber.v("  Computing PNC schedules for %s", baseID);
            ChwScheduleTaskExecutor.getInstance().execute(baseID, CoreConstants.EventType.PREGNANCY_OUTCOME, new Date());
        }
    }

    private void executeWashCheckSchedules() {
        Timber.v("Computing Wash Check schedules");
        ChwApplication.getInstance().getScheduleRepository().deleteSchedulesNotCreatedToday(CoreConstants.SCHEDULE_TYPES.WASH_CHECK, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        List<String> baseEntityIDs = ScheduleDao.getActiveFamilies(CoreConstants.SCHEDULE_TYPES.WASH_CHECK, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        if (baseEntityIDs == null) return;

        for (String baseID : baseEntityIDs) {
            Timber.v("  Computing Wash Check schedules for %s", baseID);
            ChwScheduleTaskExecutor.getInstance().execute(baseID, CoreConstants.EventType.WASH_CHECK, new Date());
        }
    }

    private void executeFpVisitSchedules() {
        Timber.v("Computing Fp schedules");
        ChwApplication.getInstance().getScheduleRepository().deleteSchedulesNotCreatedToday(CoreConstants.SCHEDULE_TYPES.FP_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        List<String> baseEntityIDs = ScheduleDao.getActiveFPWomen(CoreConstants.SCHEDULE_TYPES.FP_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        if (baseEntityIDs == null) return;

        for (String baseID : baseEntityIDs) {
            Timber.v("  Computing Fp schedules for %s", baseID);
            ChwScheduleTaskExecutor.getInstance().execute(baseID, FamilyPlanningConstants.EventType.FAMILY_PLANNING_REGISTRATION, new Date());
        }
    }

    private void executeRoutineHouseholdSchedules() {
        Timber.v("Computing Routine household schedules");
        ChwApplication.getInstance().getScheduleRepository().deleteSchedulesNotCreatedToday(CoreConstants.SCHEDULE_TYPES.ROUTINE_HOUSEHOLD_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        List<String> baseEntityIDs = ScheduleDao.getActiveFamilies(CoreConstants.SCHEDULE_TYPES.ROUTINE_HOUSEHOLD_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        if (baseEntityIDs == null) return;

        for (String baseID : baseEntityIDs) {
            Timber.v("  Computing Routine household schedules for %s", baseID);
            ChwScheduleTaskExecutor.getInstance().execute(baseID, CoreConstants.EventType.ROUTINE_HOUSEHOLD_VISIT, new Date());
        }
    }

}
