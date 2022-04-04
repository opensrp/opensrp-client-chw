package org.smartregister.chw.task;

import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.ScheduleTask;
import org.smartregister.chw.core.domain.BaseScheduleTask;
import org.smartregister.chw.core.rule.HomeAlertRule;
import org.smartregister.chw.core.utils.ChildHomeVisit;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.ChwChildDao;
import org.smartregister.chw.dao.PersonDao;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChildHomeVisitSchedulerFlv extends DefaultChildHomeVisitSchedulerFlv {

    @VisibleForTesting()
    HomeAlertRule getAlertRule(final String baseEntityID){
        ChildHomeVisit childHomeVisit = ChildUtils.getLastHomeVisit(Constants.TABLE_NAME.CHILD, baseEntityID);
        String yearOfBirth = PersonDao.getDob(baseEntityID);

        HomeAlertRule alertRule = new HomeAlertRule(
                ChwApplication.getInstance().getApplicationContext(), yearOfBirth, childHomeVisit.getLastHomeVisitDate(), childHomeVisit.getVisitNotDoneDate(), childHomeVisit.getDateCreated());
        CoreChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(alertRule, CoreConstants.RULE_FILE.HOME_VISIT);

        return alertRule;
    }

    @Override
    public List<ScheduleTask> generateTasks(String baseEntityID, String eventName, Date eventDate, BaseScheduleTask baseScheduleTask) {
        // recompute the home visit task for this child
        HomeAlertRule alertRule = getAlertRule(baseEntityID);

        // Check If any task are due for that child
        if (ChwChildDao.hasDueVaccines(baseEntityID)) {
            baseScheduleTask.setScheduleDueDate(alertRule.getDueDate());
            baseScheduleTask.setScheduleNotDoneDate(alertRule.getNotDoneDate());
            baseScheduleTask.setScheduleExpiryDate(alertRule.getExpiryDate());
            baseScheduleTask.setScheduleCompletionDate(alertRule.getCompletionDate());
            baseScheduleTask.setScheduleOverDueDate(alertRule.overDueDate());
            return toScheduleList(baseScheduleTask);
        }
        return new ArrayList<>();
    }
}
