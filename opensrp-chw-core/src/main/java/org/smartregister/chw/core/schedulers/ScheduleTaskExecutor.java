package org.smartregister.chw.core.schedulers;

import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.ScheduleService;
import org.smartregister.chw.core.contract.ScheduleTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Run extensions of this class is a singleton instance to execute all the schedules
 */
public class ScheduleTaskExecutor {

    private ScheduleTaskExecutor(){
        //scheduleServiceMap.put();
    }

    private static ScheduleTaskExecutor scheduleTaskExecutor;

    public static ScheduleTaskExecutor getInstance() {
        if (scheduleTaskExecutor == null) {
            scheduleTaskExecutor = new ScheduleTaskExecutor();
        }
        return scheduleTaskExecutor;
    }

    /**
     * This object contains the list of schedules that must be regenerated with every event action
     * The event name is the reference to the schedules
     */
    protected Map<String, List<ScheduleService>> scheduleServiceMap = new HashMap<>();

    /**
     * this function is notified every time an action the affects the schedule is called
     *
     * @param baseEntityID
     * @param eventName
     */
    public void execute(String baseEntityID, String eventName) {
        List<ScheduleService> values = scheduleServiceMap.get(eventName);
        if (values == null || values.size() == 0) return;

        for (ScheduleService service : values) {
            service.resetSchedule(baseEntityID, service.getScheduleName());
            List<ScheduleTask> services = service.generateTasks(baseEntityID);
            CoreChwApplication.getInstance().getScheduleRepository().addSchedules(services);
        }
    }
}
