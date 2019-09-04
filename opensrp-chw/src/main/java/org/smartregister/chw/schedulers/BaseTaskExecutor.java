package org.smartregister.chw.schedulers;

import org.joda.time.LocalDate;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.contract.ScheduleService;

public abstract class BaseTaskExecutor implements ScheduleService {

    @Override
    public void scheduleMaintenance() {
        LocalDate localDate = new LocalDate();
        localDate.plusDays(-31);

        ChwApplication.getInstance().getScheduleRepository().deleteSchedulesByName(getScheduleName(), localDate.toDate());

    }

    @Override
    public void resetSchedule(String baseEntityID, String scheduleName) {
        // delete from the repo all the old schedules by this name
        ChwApplication.getInstance().getScheduleRepository().deleteScheduleByName(baseEntityID, scheduleName);
    }
}
