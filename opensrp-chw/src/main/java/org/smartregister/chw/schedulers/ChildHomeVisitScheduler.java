package org.smartregister.chw.schedulers;

import org.smartregister.chw.core.contract.ScheduleTask;
import org.smartregister.chw.core.dao.VisitDao;
import org.smartregister.chw.core.domain.BaseScheduleTask;
import org.smartregister.chw.core.domain.VisitSummary;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChildHomeVisitScheduler extends BaseTaskExecutor {

    @Override
    public List<ScheduleTask> generateTasks(String baseEntityID) {

        // get the necessary info to generate the schedule
        Date fom = getFirstDateOfCurrentMonth();
        Date lom = getLastDateOfMonth();

        // recompute the home visit task for this child
        BaseScheduleTask baseScheduleTask = new BaseScheduleTask();
        baseScheduleTask.setBaseEntityID(baseEntityID);
        baseScheduleTask.setCreatedAt(new Date());
        baseScheduleTask.setUpdatedAt(new Date());
        baseScheduleTask.setID(UUID.randomUUID().toString());
        baseScheduleTask.setScheduleName(getScheduleName());

        Map<String, VisitSummary> map = VisitDao.getVisitSummary(baseEntityID);
        if (map != null) {
            VisitSummary notDone = map.get(CoreConstants.EventType.CHILD_VISIT_NOT_DONE);
            VisitSummary lastVisit = map.get(CoreConstants.EventType.CHILD_HOME_VISIT);

            if (lastVisit != null && lastVisit.getVisitDate().getTime() > fom.getTime()) {
                baseScheduleTask.setScheduleCompletionDate(lastVisit.getVisitDate());
            }

            if (notDone != null && notDone.getVisitDate().getTime() > fom.getTime()) {
                baseScheduleTask.setScheduleNotDoneDate(notDone.getVisitDate());
            }

            // return null if the child is deleted or the family is deleted
            // due date is the first of the month or if the last month visit was not done then its the first of the month
            // over due date is the last day of the month
            // expiry if when the child is 5 years old
            // not done is not done date
            // done is done date

            // visit is due the start of the next month from the last visit
            // visit i
            /*
            if(lastVisit < start_of_last_month && lastVisit < ){
                baseScheduleTask.setScheduleOverDueDate();
            }

             */
        }

        baseScheduleTask.setScheduleExpiryDate(lom);
        List<ScheduleTask> res = new ArrayList<>();
        res.add(baseScheduleTask);

        return res;
    }

    private Date getFirstDateOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    private Date getLastDateOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    private Date getDueDate(Date lastVisitDate) {
        return new Date();
    }

    @Override
    public String getScheduleName() {
        return CoreConstants.SCHEDULE_TYPES.CHILD_VISIT;
    }

}
