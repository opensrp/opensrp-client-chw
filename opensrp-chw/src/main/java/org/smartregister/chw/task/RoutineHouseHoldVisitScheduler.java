package org.smartregister.chw.task;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.contract.ScheduleTask;
import org.smartregister.chw.core.domain.BaseScheduleTask;
import org.smartregister.chw.core.rule.RoutineHouseHoldVisitRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.FamilyDao;
import org.smartregister.chw.dao.RoutineHouseHoldDao;

import java.util.Date;
import java.util.List;

public class RoutineHouseHoldVisitScheduler extends BaseTaskExecutor {
    @Override
    public List<ScheduleTask> generateTasks(String baseEntityID, String eventName, Date eventDate) {
        BaseScheduleTask baseScheduleTask = prepareNewTaskObject(baseEntityID);

        long lastWashCheck = RoutineHouseHoldDao.getLastRoutineVisitDate(baseEntityID);
        long dateCreatedFamily = FamilyDao.getFamilyCreateDate(baseEntityID);

        RoutineHouseHoldVisitRule alertRule = new RoutineHouseHoldVisitRule(
                ChwApplication.getInstance().getApplicationContext(), lastWashCheck, dateCreatedFamily);

        baseScheduleTask.setScheduleDueDate(alertRule.getDueDate());
        baseScheduleTask.setScheduleExpiryDate(alertRule.getExpiryDate());
        baseScheduleTask.setScheduleCompletionDate(alertRule.getCompletionDate());
        baseScheduleTask.setScheduleOverDueDate(alertRule.getOverDueDate());

        return toScheduleList(baseScheduleTask);
    }

    @Override
    public String getScheduleName() {
        return CoreConstants.SCHEDULE_TYPES.ROUTINE_HOUSEHOLD_VISIT;
    }

    @Override
    public String getScheduleGroup() {
        return CoreConstants.SCHEDULE_GROUPS.HOME_VISIT;
    }
}
