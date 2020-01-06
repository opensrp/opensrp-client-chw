package org.smartregister.chw.schedulers;

import org.smartregister.chw.core.contract.ScheduleService;
import org.smartregister.chw.core.schedulers.ScheduleTaskExecutor;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.chw.task.ANCVisitScheduler;
import org.smartregister.chw.task.ChildHomeVisitScheduler;
import org.smartregister.chw.task.FpVisitScheduler;
import org.smartregister.chw.task.MalariaScheduler;
import org.smartregister.chw.task.PNCVisitScheduler;
import org.smartregister.chw.task.WashCheckScheduler;
import org.smartregister.chw.util.WashCheckFlv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChwScheduleTaskExecutor extends ScheduleTaskExecutor {

    private static ChwScheduleTaskExecutor scheduleTaskExecutor;
    private WashCheckFlv washCheckFlv = new WashCheckFlv();

    public static ChwScheduleTaskExecutor getInstance() {
        if (scheduleTaskExecutor == null) {
            scheduleTaskExecutor = new ChwScheduleTaskExecutor();
        }
        return scheduleTaskExecutor;
    }

    protected ChwScheduleTaskExecutor() {
        //scheduleServiceMap.put();
    }

    @Override
    protected Map<String, List<ScheduleService>> getClassifier() {
        if (scheduleServiceMap == null || scheduleServiceMap.size() == 0) {
            scheduleServiceMap = new HashMap<>();

            initializeChildClassifier(scheduleServiceMap);
            initializeANCClassifier(scheduleServiceMap);
            initializePNCClassifier(scheduleServiceMap);
            initializeMalariaClassifier(scheduleServiceMap);
            initializeFPClassifier(scheduleServiceMap);

            if (washCheckFlv.isWashCheckVisible())
                initializeWashClassifier(scheduleServiceMap);
        }
        return scheduleServiceMap;
    }

    private void initializeChildClassifier(Map<String, List<ScheduleService>> classifier) {
        List<ScheduleService> scheduleServices = new ArrayList<>();
        scheduleServices.add(new ChildHomeVisitScheduler());

        classifier.put(CoreConstants.EventType.CHILD_HOME_VISIT, scheduleServices);
        classifier.put(CoreConstants.EventType.CHILD_VISIT_NOT_DONE, scheduleServices);
        classifier.put(CoreConstants.EventType.CHILD_REGISTRATION, scheduleServices);
    }

    private void initializeANCClassifier(Map<String, List<ScheduleService>> classifier) {
        List<ScheduleService> scheduleServices = new ArrayList<>();
        scheduleServices.add(new ANCVisitScheduler());

        classifier.put(CoreConstants.EventType.ANC_REGISTRATION, scheduleServices);
        classifier.put(CoreConstants.EventType.ANC_HOME_VISIT, scheduleServices);
        classifier.put(CoreConstants.EventType.ANC_HOME_VISIT_NOT_DONE, scheduleServices);
        classifier.put(CoreConstants.EventType.ANC_HOME_VISIT_NOT_DONE_UNDO, scheduleServices);
    }

    private void initializePNCClassifier(Map<String, List<ScheduleService>> classifier) {
        List<ScheduleService> scheduleServices = new ArrayList<>();
        scheduleServices.add(new PNCVisitScheduler());

        classifier.put(CoreConstants.EventType.PREGNANCY_OUTCOME, scheduleServices);
        classifier.put(CoreConstants.EventType.PNC_REGISTRATION, scheduleServices);
        classifier.put(CoreConstants.EventType.PNC_HOME_VISIT, scheduleServices);
        classifier.put(CoreConstants.EventType.PNC_HOME_VISIT_NOT_DONE, scheduleServices);
        classifier.put(CoreConstants.EventType.PNC_HOME_VISIT_NOT_DONE_UNDO, scheduleServices);
    }

    private void initializeMalariaClassifier(Map<String, List<ScheduleService>> classifier) {
        List<ScheduleService> scheduleServices = new ArrayList<>();
        scheduleServices.add(new MalariaScheduler());
        classifier.put(CoreConstants.EventType.MALARIA_FOLLOW_UP_VISIT, scheduleServices);
        classifier.put(CoreConstants.EventType.MALARIA_CONFIRMATION, scheduleServices);
    }

    private void initializeWashClassifier(Map<String, List<ScheduleService>> classifier) {
        List<ScheduleService> scheduleServices = new ArrayList<>();
        scheduleServices.add(new WashCheckScheduler());

        classifier.put(CoreConstants.EventType.FAMILY_REGISTRATION, scheduleServices);
        classifier.put(CoreConstants.EventType.WASH_CHECK, scheduleServices);
    }

    private void initializeFPClassifier(Map<String, List<ScheduleService>> classifier) {
        List<ScheduleService> scheduleServices = new ArrayList<>();
        scheduleServices.add(new FpVisitScheduler());
        classifier.put(FamilyPlanningConstants.EventType.FP_FOLLOW_UP_VISIT, scheduleServices);
        classifier.put(FamilyPlanningConstants.EventType.FAMILY_PLANNING_REGISTRATION, scheduleServices);
        classifier.put(FamilyPlanningConstants.EventType.FAMILY_PLANNING_CHANGE_METHOD, scheduleServices);

    }
}
