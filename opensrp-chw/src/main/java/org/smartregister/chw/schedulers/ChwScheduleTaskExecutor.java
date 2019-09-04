package org.smartregister.chw.schedulers;

import org.smartregister.chw.core.contract.ScheduleService;
import org.smartregister.chw.core.schedulers.ScheduleTaskExecutor;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChwScheduleTaskExecutor extends ScheduleTaskExecutor {

    protected ChwScheduleTaskExecutor(){
        //scheduleServiceMap.put();
    }

    private static ChwScheduleTaskExecutor scheduleTaskExecutor;

    public static ChwScheduleTaskExecutor getInstance() {
        if (scheduleTaskExecutor == null) {
            scheduleTaskExecutor = new ChwScheduleTaskExecutor();
        }
        return scheduleTaskExecutor;
    }

    @Override
    protected Map<String, List<ScheduleService>> getClassifier(){
        if(scheduleServiceMap == null || scheduleServiceMap.size() == 0){
            scheduleServiceMap = new HashMap<>();

            initializeChildClassifier(scheduleServiceMap);
            initializeANCClassifier(scheduleServiceMap);
            initializePNCClassifier(scheduleServiceMap);
            initializeMalariaClassifier(scheduleServiceMap);
            initializeWashClassifier(scheduleServiceMap);
        }

        return scheduleServiceMap;
    }

    private void initializeChildClassifier(Map<String, List<ScheduleService>> classifier){
        List<ScheduleService> scheduleServices = new ArrayList<>();
        scheduleServices.add(new ChildHomeVisitScheduler());

        classifier.put(CoreConstants.EventType.CHILD_HOME_VISIT, scheduleServices);
    }

    private void initializeANCClassifier(Map<String, List<ScheduleService>> classifier){
        List<ScheduleService> scheduleServices = new ArrayList<>();
        classifier.put(CoreConstants.EventType.ANC_HOME_VISIT, scheduleServices);
    }

    private void initializePNCClassifier(Map<String, List<ScheduleService>> classifier){
        List<ScheduleService> scheduleServices = new ArrayList<>();
        classifier.put(CoreConstants.EventType.PNC_HOME_VISIT, scheduleServices);
    }

    private void initializeMalariaClassifier(Map<String, List<ScheduleService>> classifier){
        List<ScheduleService> scheduleServices = new ArrayList<>();
        classifier.put(CoreConstants.EventType.MALARIA_FOLLOW_UP_VISIT, scheduleServices);
    }

    private void initializeWashClassifier(Map<String, List<ScheduleService>> classifier){
        List<ScheduleService> scheduleServices = new ArrayList<>();
        classifier.put(CoreConstants.EventType.WASH_CHECK, scheduleServices);
    }
}
