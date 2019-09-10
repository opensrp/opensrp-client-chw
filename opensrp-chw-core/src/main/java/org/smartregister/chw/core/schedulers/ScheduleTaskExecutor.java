package org.smartregister.chw.core.schedulers;

import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.ScheduleService;
import org.smartregister.chw.core.contract.ScheduleTask;

import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Run extensions of this class is a singleton instance to execute all the schedules
 */
public abstract class ScheduleTaskExecutor {

    /**
     * This object contains the list of schedules that must be regenerated with every event action
     * The event name is the reference to the schedules
     */
    protected Map<String, List<ScheduleService>> scheduleServiceMap;

    /**
     * this function is notified every time an action the affects the schedule is called
     *
     * @param baseEntityID
     * @param eventName
     * @param eventDate
     */
    public void execute(String baseEntityID, String eventName, Date eventDate) {
        List<ScheduleService> values = getClassifier().get(eventName);
        if (values == null || values.size() == 0) return;

        for (ScheduleService service : values) {
            try {
                service.resetSchedule(baseEntityID, service.getScheduleName());
                List<ScheduleTask> services = service.generateTasks(baseEntityID, eventName, eventDate);
                if (services != null && services.size() > 0)
                    CoreChwApplication.getInstance().getScheduleRepository().addSchedules(services);
            }catch (Exception e){
                Timber.e(e);
            }
        }
    }

    /**
     * use this function in all overrides to initialize event classifiers
     *
     * @return
     */
    protected abstract Map<String, List<ScheduleService>> getClassifier();
}
