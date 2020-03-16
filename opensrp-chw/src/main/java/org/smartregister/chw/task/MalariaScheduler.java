package org.smartregister.chw.task;

import org.joda.time.LocalDate;
import org.smartregister.chw.core.contract.ScheduleTask;
import org.smartregister.chw.core.domain.BaseScheduleTask;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.malaria.dao.MalariaDao;
import org.smartregister.chw.malaria.util.Constants;

import java.util.Date;
import java.util.List;

public class MalariaScheduler extends BaseTaskExecutor {

    @Override
    public List<ScheduleTask> generateTasks(String baseEntityID, String eventName, Date eventDate) {

        BaseScheduleTask baseScheduleTask = prepareNewTaskObject(baseEntityID);

        Date malaria_date = MalariaDao.getMalariaTestDate(baseEntityID);
        if (malaria_date != null) {
            LocalDate localDate = new LocalDate(malaria_date.getTime());

            // due date is the start of the schedule
            baseScheduleTask.setScheduleDueDate(localDate.plusDays(7).toDate());

            // expiry date
            baseScheduleTask.setScheduleExpiryDate(localDate.plusDays(14).toDate());

            // completion date
            if (eventName.equalsIgnoreCase(Constants.EVENT_TYPE.MALARIA_FOLLOW_UP_VISIT)) {
                baseScheduleTask.setScheduleCompletionDate(eventDate);
            }

            // overdue date
            baseScheduleTask.setScheduleOverDueDate(localDate.plusDays(10).toDate());
        }

        return toScheduleList(baseScheduleTask);
    }

    @Override
    public String getScheduleName() {
        return CoreConstants.SCHEDULE_TYPES.MALARIA_VISIT;
    }

    @Override
    public String getScheduleGroup() {
        return CoreConstants.SCHEDULE_GROUPS.HOME_VISIT;
    }
}
