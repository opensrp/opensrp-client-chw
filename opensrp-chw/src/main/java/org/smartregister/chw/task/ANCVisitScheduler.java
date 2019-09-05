package org.smartregister.chw.task;

import org.smartregister.chw.core.contract.ScheduleTask;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.Date;
import java.util.List;

public class ANCVisitScheduler extends BaseTaskExecutor {

    @Override
    public List<ScheduleTask> generateTasks(String baseEntityID, String eventName, Date eventDate) {
        return null;
    }

    @Override
    public String getScheduleName() {
        return CoreConstants.SCHEDULE_TYPES.ANC_VISIT;
    }
}
