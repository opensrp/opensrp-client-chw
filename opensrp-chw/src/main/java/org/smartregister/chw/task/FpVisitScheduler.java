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
import org.smartregister.chw.fp.dao.FpDao;
import org.smartregister.chw.fp.domain.FpAlertObject;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FpVisitScheduler extends BaseTaskExecutor {

    @Override
    public void resetSchedule(String baseEntityID, String scheduleName) {
        super.resetSchedule(baseEntityID, scheduleName);
        ChwApplication.getInstance().getScheduleRepository().deleteScheduleByGroup(getScheduleGroup(), baseEntityID);
    }

    @Override
    public List<ScheduleTask> generateTasks(String baseEntityID, String eventName, Date eventDate) {
        String fpMethod = null;
        Date fp_date = null;
        Integer fp_pillCycles = null;
        BaseScheduleTask baseScheduleTask = prepareNewTaskObject(baseEntityID);
        List<Rules> fpRules = new ArrayList<>();
        List<FpAlertObject> familyPlanningList = FpDao.getFpDetails(baseEntityID);
        if (familyPlanningList.size() > 0) {
            for (FpAlertObject familyPlanning : familyPlanningList) {
                fpMethod = familyPlanning.getFpMethod();
                fp_date = familyPlanning.getFpStartDate();
                fp_pillCycles = familyPlanning.getFpPillCycles();

                if (fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_POP) || fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_COC)) {
                    fpRules.add(CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.FP_COC_POP_REFILL));
                } else if (fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_IUCD)) {
                    fpRules.add(CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.FP_IUCD));
                } else if (fpMethod.equalsIgnoreCase((FamilyPlanningConstants.DBConstants.FP_FEMALE_CONDOM)) || fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_MALE_CONDOM)) {
                    fpRules.add(CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.FP_CONDOM_REFILL));
                } else if (fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_INJECTABLE)) {
                    fpRules.add(CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.FP_INJECTION_DUE));
                } else if (fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_FEMALE_STERLIZATION)) {
                    fpRules.add(CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.FP_FEMALE_STERILIZATION));
                }
            }
        }
        if (fp_date == null)
            return new ArrayList<>();

        Date lastVisitDate = null;

        Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(baseEntityID, FamilyPlanningConstants.EventType.FP_HOME_VISIT);
        if (lastVisit != null) {
            lastVisitDate = lastVisit.getDate();
        }
        for (Rules rules : fpRules) {
            FpAlertRule alertRule = HomeVisitUtil.getFpVisitStatus(rules, lastVisitDate, fp_date, fp_pillCycles, fpMethod);

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
