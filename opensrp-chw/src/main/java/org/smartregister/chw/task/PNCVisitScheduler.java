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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class PNCVisitScheduler extends BaseTaskExecutor {

    @Override
    public List<ScheduleTask> generateTasks(String baseEntityID, String eventName, Date eventDate) {
        BaseScheduleTask baseScheduleTask = prepareNewTaskObject(baseEntityID);
        baseScheduleTask.setScheduleGroupName(CoreConstants.SCHEDULE_GROUPS.HOME_VISIT);

        Rules rules = ChwApplication.getInstance().getRulesEngineHelper().rules(Constants.RULE_FILE.PNC_HOME_VISIT);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String dayPnc = PNCDao.getPNCDeliveryDate(baseEntityID);
        Date deliveryDate = null;
        Date lastVisitDate = null;
        try {
            deliveryDate = sdf.parse(dayPnc);
        } catch (ParseException e) {
            Timber.e(e);
        }

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
}
