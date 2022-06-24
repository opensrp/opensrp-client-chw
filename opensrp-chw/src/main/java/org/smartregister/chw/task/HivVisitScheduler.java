package org.smartregister.chw.task;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.contract.ScheduleTask;
import org.smartregister.chw.core.domain.BaseScheduleTask;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.ChwCBHSDao;
import org.smartregister.chw.hiv.dao.HivDao;
import org.smartregister.chw.hiv.domain.HivMemberObject;
import org.smartregister.chw.rule.CbhsFollowupRule;
import org.smartregister.chw.util.ChwHomeVisitUtil;

import java.util.Date;
import java.util.List;

public class HivVisitScheduler extends BaseTaskExecutor {

    @Override
    public void resetSchedule(String baseEntityID, String scheduleName) {
        super.resetSchedule(baseEntityID, scheduleName);
        ChwApplication.getInstance().getScheduleRepository().deleteScheduleByGroup(getScheduleGroup(), baseEntityID);
    }

    @Override
    public List<ScheduleTask> generateTasks(String baseEntityID, String eventName, Date eventDate) {

        BaseScheduleTask baseScheduleTask = prepareNewTaskObject(baseEntityID);
        HivMemberObject hivMemberObject = HivDao.getMember(baseEntityID);
        Date nextVisitDate = ChwCBHSDao.getNextVisitDate(baseEntityID);
        CbhsFollowupRule cbhsFollowupRule = ChwHomeVisitUtil.getCBHSVisitStatus(nextVisitDate, hivMemberObject.getHivRegistrationDate());

        baseScheduleTask.setScheduleDueDate(cbhsFollowupRule.getDueDate());
        baseScheduleTask.setScheduleExpiryDate(cbhsFollowupRule.getExpiryDate());
        baseScheduleTask.setScheduleOverDueDate(cbhsFollowupRule.getOverDueDate());

        return toScheduleList(baseScheduleTask);
    }

    @Override
    public String getScheduleName() {
        return CoreConstants.SCHEDULE_TYPES.HIV_VISIT;
    }

    @Override
    public String getScheduleGroup() {
        return CoreConstants.SCHEDULE_GROUPS.HOME_VISIT;
    }
}
