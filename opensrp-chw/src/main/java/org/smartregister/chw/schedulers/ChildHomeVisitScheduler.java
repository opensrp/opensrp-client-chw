package org.smartregister.chw.schedulers;

import org.smartregister.chw.core.contract.ScheduleService;
import org.smartregister.chw.core.contract.ScheduleTask;

import java.util.List;

public class ChildHomeVisitScheduler implements ScheduleService {
    @Override
    public List<ScheduleTask> generateTasks(String baseEntityID) {
        return null;
    }

    @Override
    public void scheduleMaintenance() {

    }

    @Override
    public String getScheduleName() {
        return null;
    }

    @Override
    public void resetSchedule(String baseEntityID, String scheduleName) {

    }
}
