package org.smartregister.chw.task;

import org.jeasy.rules.api.Rules;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.ScheduleTask;
import org.smartregister.chw.core.domain.BaseScheduleTask;
import org.smartregister.chw.core.rule.FpAlertRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.HomeVisitUtil;
import org.smartregister.chw.dao.FamilyPlanningDao;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FPVisitScheduler extends BaseTaskExecutor {

    @Override
    public void resetSchedule(String baseEntityID, String scheduleName) {
        super.resetSchedule(baseEntityID, scheduleName);
        ChwApplication.getInstance().getScheduleRepository().deleteScheduleByGroup(getScheduleGroup(), baseEntityID);
    }

    @Override
    public List<ScheduleTask> generateTasks(String baseEntityID, String eventName, Date eventDate) {
        BaseScheduleTask baseScheduleTask = prepareNewTaskObject(baseEntityID);
        List<Rules> fpRules = new ArrayList<>();
        fpRules.add(CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.FP_COC_POP_REFILL));
        fpRules.add(CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.FP_CONDOM_REFILL));
        fpRules.add(CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.FP_INJECTION_DUE));
        fpRules.add(CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.FP_FEMALE_STERILIZATION));
        fpRules.add(CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.FP_IUCD));

        Date fp_date = FamilyPlanningDao.getFamilyPlanningDate(baseEntityID);
        Integer fp_pillCycles = FamilyPlanningDao.getFamilyPlanningPillCycles(baseEntityID);
        String fp_method = FamilyPlanningDao.getFamilyPlanningMethod(baseEntityID);

        if (fp_date == null)
            return new ArrayList<>();

        Date lastVisitDate = null;

        Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(baseEntityID, FamilyPlanningConstants.EventType.FP_HOME_VISIT);
        if (lastVisit != null) {
            lastVisitDate = lastVisit.getDate();
        }
        for (Rules rules : fpRules) {
            FpAlertRule alertRule = HomeVisitUtil.getFpVisitStatus(rules, lastVisitDate, fp_date, fp_pillCycles, fp_method);

            baseScheduleTask.setScheduleDueDate(alertRule.getDueDate());
            baseScheduleTask.setScheduleExpiryDate(alertRule.getExpiryDate());
            baseScheduleTask.setScheduleCompletionDate(alertRule.getCompletionDate());
            baseScheduleTask.setScheduleOverDueDate(alertRule.getOverDueDate());
        }


        return toScheduleList(baseScheduleTask);
    }

    @Override
    public String getScheduleName() {
        return CoreConstants.SCHEDULE_TYPES.FP_VISIT;
    }

    @Override
    public String getScheduleGroup() {
        return CoreConstants.SCHEDULE_GROUPS.HOME_VISIT;
    }
}
