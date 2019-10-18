package org.smartregister.chw.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.ScheduleDao;
import org.smartregister.chw.schedulers.ChwScheduleTaskExecutor;

import java.util.Date;
import java.util.List;

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
        executeAncVisitSchedules();

        // execute all pnc schedules
        executePncVisitSchedules();

        // execute all wash check
        executeWashCheckSchedules();
    }

    private void executeChildVisitSchedules() {
        List<String> baseEntityIDs = ScheduleDao.getActiveChildren();
        if (baseEntityIDs == null) return;

        for (String baseID : baseEntityIDs) {
            ChwScheduleTaskExecutor.getInstance().execute(baseID, CoreConstants.EventType.CHILD_HOME_VISIT, new Date());
        }
    }

    private void executeAncVisitSchedules() {
        List<String> baseEntityIDs = ScheduleDao.getActiveANCWomen();
        if (baseEntityIDs == null) return;

        for (String baseID : baseEntityIDs) {
            ChwScheduleTaskExecutor.getInstance().execute(baseID, CoreConstants.EventType.ANC_REGISTRATION, new Date());
        }
    }

    private void executePncVisitSchedules() {
        List<String> baseEntityIDs = ScheduleDao.getActivePNCWomen();
        if (baseEntityIDs == null) return;

        for (String baseID : baseEntityIDs) {
            ChwScheduleTaskExecutor.getInstance().execute(baseID, CoreConstants.EventType.PREGNANCY_OUTCOME, new Date());
        }
    }

    private void executeWashCheckSchedules() {
        List<String> baseEntityIDs = ScheduleDao.getActiveWashCheckFamilies();
        if (baseEntityIDs == null) return;

        for (String baseID : baseEntityIDs) {
            ChwScheduleTaskExecutor.getInstance().execute(baseID, CoreConstants.EventType.WASH_CHECK, new Date());
        }
    }
}
