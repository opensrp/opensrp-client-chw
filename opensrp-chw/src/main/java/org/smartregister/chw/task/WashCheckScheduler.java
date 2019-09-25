package org.smartregister.chw.task;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.contract.ScheduleTask;
import org.smartregister.chw.core.domain.BaseScheduleTask;
import org.smartregister.chw.core.rule.WashCheckAlertRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.FamilyDao;
import org.smartregister.chw.dao.WashCheckDao;

import java.util.Date;
import java.util.List;

public class WashCheckScheduler extends BaseTaskExecutor {
    @Override
    public List<ScheduleTask> generateTasks(String baseEntityID, String eventName, Date eventDate) {
        BaseScheduleTask baseScheduleTask = prepareNewTaskObject(baseEntityID);

        long lastWashCheck = WashCheckDao.getLastWashCheckDate(baseEntityID);
        long dateCreatedFamily = FamilyDao.getFamilyCreateDate(baseEntityID);

        WashCheckAlertRule alertRule = new WashCheckAlertRule(
                ChwApplication.getInstance().getApplicationContext(), lastWashCheck, dateCreatedFamily);
        baseScheduleTask.setScheduleDueDate(alertRule.getDueDate());
        baseScheduleTask.setScheduleExpiryDate(alertRule.getExpiryDate());
        baseScheduleTask.setScheduleCompletionDate(alertRule.getCompletionDate());
        baseScheduleTask.setScheduleOverDueDate(alertRule.getOverDueDate());

        return toScheduleList(baseScheduleTask);
    }

    @Override
    public String getScheduleName() {
        return CoreConstants.SCHEDULE_TYPES.WASH_CHECK;
    }

    @Override
    public String getScheduleGroup() {
        return CoreConstants.SCHEDULE_GROUPS.HOME_VISIT;
    }
}
