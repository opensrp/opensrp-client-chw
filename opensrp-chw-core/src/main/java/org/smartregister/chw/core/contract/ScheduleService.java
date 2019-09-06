package org.smartregister.chw.core.contract;

import java.util.Date;
import java.util.List;

public interface ScheduleService {

    /**
     * generating the schedules
     *
     * @param baseEntityID
     * @return
     */
    List<ScheduleTask> generateTasks(String baseEntityID, String eventName , Date eventDate);

    /**
     * add logic to delete the schedule from the database
     */
    void scheduleMaintenance();

    String getScheduleName();

    String getScheduleGroup();

    /**
     * clears the schedules from the database
     */
    void resetSchedule(String baseEntityID, String scheduleName);
}
