package org.smartregister.chw.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.EventDao;
import org.smartregister.chw.dao.ScheduleDao;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.chw.schedulers.ChwScheduleTaskExecutor;
import org.smartregister.domain.Event;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing())
            return;

        if (ChwApplication.getInstance().allowLazyProcessing())
            processLazyEvents();

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

        // execute all family kit check
        if (ChwApplication.getApplicationFlavor().hasFamilyKitCheck())
            executeFamilyKitSchedules();

        // execute all fp schedules
        if (ChwApplication.getApplicationFlavor().hasFamilyPlanning())
            executeFpVisitSchedules();

        if (ChwApplication.getApplicationFlavor().hasRoutineVisit())
            executeRoutineHouseholdSchedules();


    }

    private void processLazyEvents() {
        if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing())
            return;

        // process missing visits
        List<Event> eventClients = EventDao.getUnprocessedEvents(ChwApplication.getInstance().lazyProcessedEvents());
        ((ChwApplication) ChwApplication.getInstance()).setBulkProcessing(!eventClients.isEmpty());

        Set<String> entityIds = new HashSet<>();
        for (Event event : eventClients) {
            if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing())
                break;

            if (CoreConstants.EventType.CHILD_HOME_VISIT.equals(event.getEventType())) {
                processHomeVisit(event, CoreConstants.EventType.CHILD_HOME_VISIT);
            } else {
                processHomeVisit(event, null);
            }

            if (!entityIds.contains(event.getBaseEntityId())) {
                ChwApplication.getInstance().getScheduleRepository().deleteSchedulesByEntityID(event.getBaseEntityId());
                entityIds.add(event.getBaseEntityId());
            }
        }
        ((ChwApplication) ChwApplication.getInstance()).setBulkProcessing(false);
    }

    public void processHomeVisit(Event event, String parentEventType) {
        try {
            Visit visit = NCUtils.eventToVisit(event);

            if (StringUtils.isNotBlank(parentEventType) && !parentEventType.equalsIgnoreCase(visit.getVisitType())) {
                String parentVisitID = AncLibrary.getInstance().visitRepository().getParentVisitEventID(visit.getBaseEntityId(), parentEventType, visit.getDate());
                visit.setParentVisitID(parentVisitID);
            }

            AncLibrary.getInstance().visitRepository().addVisit(visit);
            if (visit.getVisitDetails() != null) {
                for (Map.Entry<String, List<VisitDetail>> entry : visit.getVisitDetails().entrySet()) {
                    if (entry.getValue() != null) {
                        for (VisitDetail detail : entry.getValue()) {
                            AncLibrary.getInstance().visitDetailsRepository().addVisitDetails(detail);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private void executeChildVisitSchedules() {
        if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing())
            return;

        Timber.v("Computing child schedules");
        ScheduleDao.deleteChildrenVaccines();
        ChwApplication.getInstance().getScheduleRepository().deleteSchedulesNotCreatedToday(CoreConstants.SCHEDULE_TYPES.CHILD_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        List<String> baseEntityIDs = ChwApplication.getApplicationFlavor().showChildrenUnderFiveAndGirlsAgeNineToEleven() ? ScheduleDao.getActiveChildrenUnder5AndGirlsAge9to11(CoreConstants.SCHEDULE_TYPES.CHILD_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT) : ScheduleDao.getActiveChildren(CoreConstants.SCHEDULE_TYPES.CHILD_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        if (baseEntityIDs == null) return;

        for (String baseID : baseEntityIDs) {
            if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing())
                break;

            Timber.v("  Computing child schedules for %s", baseID);
            ChwScheduleTaskExecutor.getInstance().execute(baseID, CoreConstants.EventType.CHILD_HOME_VISIT, new Date());
        }
    }

    private void executeAncVisitSchedules() {
        if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing())
            return;

        Timber.v("Computing ANC schedules");
        ChwApplication.getInstance().getScheduleRepository().deleteSchedulesNotCreatedToday(CoreConstants.SCHEDULE_TYPES.ANC_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        List<String> baseEntityIDs = ScheduleDao.getActiveANCWomen(CoreConstants.SCHEDULE_TYPES.ANC_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        if (baseEntityIDs == null) return;

        for (String baseID : baseEntityIDs) {
            if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing())
                break;

            Timber.v("  Computing ANC schedules for %s", baseID);
            ChwScheduleTaskExecutor.getInstance().execute(baseID, CoreConstants.EventType.ANC_REGISTRATION, new Date());
        }
    }

    private void executePncVisitSchedules() {
        if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing())
            return;

        Timber.v("Computing PNC schedules");
        ChwApplication.getInstance().getScheduleRepository().deleteSchedulesNotCreatedToday(CoreConstants.SCHEDULE_TYPES.PNC_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        List<String> baseEntityIDs = ScheduleDao.getActivePNCWomen(CoreConstants.SCHEDULE_TYPES.PNC_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        if (baseEntityIDs == null) return;

        for (String baseID : baseEntityIDs) {
            if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing())
                break;

            Timber.v("  Computing PNC schedules for %s", baseID);
            ChwScheduleTaskExecutor.getInstance().execute(baseID, CoreConstants.EventType.PREGNANCY_OUTCOME, new Date());
        }
    }

    private void executeWashCheckSchedules() {
        if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing())
            return;

        Timber.v("Computing Wash Check schedules");
        ChwApplication.getInstance().getScheduleRepository().deleteSchedulesNotCreatedToday(CoreConstants.SCHEDULE_TYPES.WASH_CHECK, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        List<String> baseEntityIDs = ScheduleDao.getActiveFamilies(CoreConstants.SCHEDULE_TYPES.WASH_CHECK, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        if (baseEntityIDs == null) return;

        for (String baseID : baseEntityIDs) {
            if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing())
                break;

            Timber.v("  Computing Wash Check schedules for %s", baseID);
            ChwScheduleTaskExecutor.getInstance().execute(baseID, CoreConstants.EventType.WASH_CHECK, new Date());
        }
    }

    private void executeFamilyKitSchedules() {
        if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing())
            return;

        Timber.v("Computing Family Kit schedules");
        ChwApplication.getInstance().getScheduleRepository().deleteSchedulesNotCreatedToday(CoreConstants.SCHEDULE_TYPES.FAMILY_KIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        List<String> baseEntityIDs = ScheduleDao.getActiveFamilies(CoreConstants.SCHEDULE_TYPES.FAMILY_KIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        if (baseEntityIDs == null) return;

        for (String baseID : baseEntityIDs) {
            if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing())
                break;

            Timber.v("  Computing Family Kit schedules for %s", baseID);
            ChwScheduleTaskExecutor.getInstance().execute(baseID, CoreConstants.EventType.FAMILY_KIT, new Date());
        }
    }

    private void executeFpVisitSchedules() {
        if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing())
            return;

        Timber.v("Computing Fp schedules");
        ChwApplication.getInstance().getScheduleRepository().deleteSchedulesNotCreatedToday(CoreConstants.SCHEDULE_TYPES.FP_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        List<String> baseEntityIDs = ScheduleDao.getActiveFPWomen(CoreConstants.SCHEDULE_TYPES.FP_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        if (baseEntityIDs == null) return;

        for (String baseID : baseEntityIDs) {
            if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing())
                break;

            Timber.v("  Computing Fp schedules for %s", baseID);
            ChwScheduleTaskExecutor.getInstance().execute(baseID, FamilyPlanningConstants.EventType.FAMILY_PLANNING_REGISTRATION, new Date());
        }
    }

    private void executeRoutineHouseholdSchedules() {
        if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing())
            return;

        Timber.v("Computing Routine household schedules");
        ChwApplication.getInstance().getScheduleRepository().deleteSchedulesNotCreatedToday(CoreConstants.SCHEDULE_TYPES.ROUTINE_HOUSEHOLD_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        List<String> baseEntityIDs = ScheduleDao.getActiveFamilies(CoreConstants.SCHEDULE_TYPES.ROUTINE_HOUSEHOLD_VISIT, CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);
        if (baseEntityIDs == null) return;

        for (String baseID : baseEntityIDs) {
            if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing())
                break;

            Timber.v("  Computing Routine household schedules for %s", baseID);
            ChwScheduleTaskExecutor.getInstance().execute(baseID, CoreConstants.EventType.ROUTINE_HOUSEHOLD_VISIT, new Date());
        }
    }

}
