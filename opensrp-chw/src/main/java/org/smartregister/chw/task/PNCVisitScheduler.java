package org.smartregister.chw.task;

import org.jeasy.rules.api.Rules;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.contract.ScheduleTask;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.core.domain.BaseScheduleTask;
import org.smartregister.chw.core.rule.PncVisitAlertRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.HomeVisitUtil;
import org.smartregister.chw.util.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PNCVisitScheduler extends BaseTaskExecutor {
    @Override
    public void resetSchedule(String baseEntityID, String scheduleName) {
        super.resetSchedule(baseEntityID, scheduleName);
        ChwApplication.getInstance().getScheduleRepository().deleteScheduleByGroup(getScheduleGroup(), baseEntityID);
    }

    @Override
    public List<ScheduleTask> generateTasks(String baseEntityID, String eventName, Date eventDate) {
        BaseScheduleTask baseScheduleTask = prepareNewTaskObject(baseEntityID);

        Rules rules = ChwApplication.getInstance().getRulesEngineHelper().rules(Constants.RULE_FILE.PNC_HOME_VISIT);
        Date deliveryDate = PNCDao.getPNCDeliveryDate(baseEntityID);

        if (deliveryDate == null)
            return new ArrayList<>();

        Date lastVisitDate = null;

        Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(baseEntityID, org.smartregister.chw.anc.util.Constants.EVENT_TYPE.PNC_HOME_VISIT);
        if (lastVisit != null) {
            lastVisitDate = lastVisit.getDate();
        }

        PncVisitAlertRule alertRule = HomeVisitUtil.getPncVisitStatus(rules, lastVisitDate, deliveryDate);

        baseScheduleTask.setScheduleDueDate(alertRule.getDueDate());
        baseScheduleTask.setScheduleExpiryDate(alertRule.getExpiryDate());
        baseScheduleTask.setScheduleCompletionDate(alertRule.getCompletionDate());
        baseScheduleTask.setScheduleOverDueDate(alertRule.getOverDueDate());

        return toScheduleList(baseScheduleTask);
    }

    @Override
    public String getScheduleName() {
        return CoreConstants.SCHEDULE_TYPES.PNC_VISIT;
    }

    @Override
    public String getScheduleGroup() {
        return CoreConstants.SCHEDULE_GROUPS.HOME_VISIT;
    }
}
