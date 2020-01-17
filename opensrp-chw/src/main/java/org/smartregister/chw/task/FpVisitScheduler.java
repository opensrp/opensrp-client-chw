package org.smartregister.chw.task;

import org.jeasy.rules.api.Rules;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.contract.ScheduleTask;
import org.smartregister.chw.core.domain.BaseScheduleTask;
import org.smartregister.chw.core.rule.FpAlertRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.FpUtil;
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
        String fp_date = null;
        Integer fp_pillCycles = null;
        Rules rule = null;
        BaseScheduleTask baseScheduleTask = prepareNewTaskObject(baseEntityID);
        List<FpAlertObject> familyPlanningList = FpDao.getFpDetails(baseEntityID);
        if (familyPlanningList.size() > 0) {
            for (FpAlertObject familyPlanning : familyPlanningList) {
                fpMethod = familyPlanning.getFpMethod();
                fp_date = familyPlanning.getFpStartDate();
                fp_pillCycles = familyPlanning.getFpPillCycles();
                rule = FpUtil.getFpRules(fpMethod);
            }
        }
        if (fp_date == null)
            return new ArrayList<>();
        Date lastVisitDate = null;
        Visit lastVisit = null;
        Date fpDate = FpUtil.parseFpStartDate(fp_date);
        if (fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_INJECTABLE)) {
            lastVisit = FpDao.getLatestInjectionVisit(baseEntityID, fpMethod);
        } else {
            lastVisit = FpDao.getLatestFpVisit(baseEntityID, FamilyPlanningConstants.EventType.FP_FOLLOW_UP_VISIT, fpMethod);
        }
        if (lastVisit != null) {
            lastVisitDate = lastVisit.getDate();
        }
        FpAlertRule alertRule = HomeVisitUtil.getFpVisitStatus(rule, lastVisitDate, fpDate, fp_pillCycles, fpMethod);

        baseScheduleTask.setScheduleDueDate(alertRule.getDueDate());
        baseScheduleTask.setScheduleExpiryDate(alertRule.getExpiryDate());
        baseScheduleTask.setScheduleCompletionDate(alertRule.getCompletionDate());
        baseScheduleTask.setScheduleOverDueDate(alertRule.getOverDueDate());

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
