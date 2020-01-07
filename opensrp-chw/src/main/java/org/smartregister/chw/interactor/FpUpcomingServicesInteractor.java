package org.smartregister.chw.interactor;

import android.content.Context;

import org.jeasy.rules.api.Rules;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.interactor.BaseAncUpcomingServicesInteractor;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.rule.FpAlertRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.FpUtil;
import org.smartregister.chw.core.utils.HomeVisitUtil;
import org.smartregister.chw.core.utils.VaccineScheduleUtil;
import org.smartregister.chw.fp.dao.FpDao;
import org.smartregister.chw.fp.domain.FpAlertObject;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FpUpcomingServicesInteractor extends BaseAncUpcomingServicesInteractor {
    protected MemberObject memberObject;
    protected Context context;

    @Override
    public List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject) {
        this.memberObject = memberObject;
        this.context = context;
        VaccineScheduleUtil.updateOfflineAlerts(memberObject.getBaseEntityId(), new DateTime(memberObject.getDob()), CoreConstants.SERVICE_GROUPS.CHILD);
        List<BaseUpcomingService> serviceList = new ArrayList<>();
        evaluateFp(serviceList);
        return serviceList;
    }

    private void evaluateFp(List<BaseUpcomingService> serviceList) {
        String fpMethod = null;
        String methodUsed;
        String fp_date = null;
        Integer fp_pillCycles = null;
        Rules rule = null;
        Integer count = null;
        Date serviceDueDate = null;
        Date serviceOverDueDate = null;
        String serviceName = null;
        List<FpAlertObject> familyPlanningList = FpDao.getFpDetails(memberObject.getBaseEntityId());
        if (familyPlanningList.size() > 0) {
            for (FpAlertObject familyPlanning : familyPlanningList) {
                methodUsed = familyPlanning.getFpMethod();
                switch (methodUsed){
                    case "COC":
                        fpMethod = context.getString(R.string.coc);
                        break;
                    case "POP":
                        fpMethod = context.getString(R.string.pop);
                        break;
                    case "Female sterilization":
                        fpMethod = context.getString(R.string.female_sterilization);
                        break;
                    case "Injectable":
                        fpMethod = context.getString(R.string.injectable);
                        break;
                    case "Male condom":
                        fpMethod = context.getString(R.string.male_condom);
                        break;
                    case "Female condom":
                        fpMethod = context.getString(R.string.female_condom);
                        break;
                    case "IUCD":
                        fpMethod = context.getString(R.string.iucd);
                        break;
                }
                fp_date = familyPlanning.getFpStartDate();
                fp_pillCycles = familyPlanning.getFpPillCycles();
                rule = FpUtil.getFpRules(fpMethod);
            }
        }
        Date lastVisitDate = null;
        Visit lastVisit = null;
        Date fpDate = FpUtil.parseFpStartDate(fp_date);
        if (fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_INJECTABLE)) {
            lastVisit = FpDao.getLatestInjectionVisit(memberObject.getBaseEntityId(), fpMethod);
        } else {
            lastVisit = FpDao.getLatestFpVisit(memberObject.getBaseEntityId(), FamilyPlanningConstants.EventType.FP_FOLLOW_UP_VISIT, fpMethod);
        }
        if (lastVisit != null) {
            lastVisitDate = lastVisit.getDate();
        }
        FpAlertRule alertRule = HomeVisitUtil.getFpVisitStatus(rule, lastVisitDate, fpDate, fp_pillCycles, fpMethod);
        if (fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_COC) || fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_POP) ||
                fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_MALE_CONDOM) || fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_FEMALE_CONDOM) || fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_INJECTABLE)) {
            serviceDueDate = alertRule.getDueDate();
            serviceOverDueDate = alertRule.getOverDueDate();
            if (fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_INJECTABLE)) {
                serviceName = fpMethod;
            } else {
                serviceName = MessageFormat.format(context.getString(R.string.refill), fpMethod);
            }
        } else if (fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_IUCD) || fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_FEMALE_STERLIZATION)) {
            if (lastVisit == null) {
                serviceDueDate = alertRule.getDueDate();
                serviceOverDueDate = alertRule.getOverDueDate();
                serviceName = MessageFormat.format(context.getString(R.string.follow_up_one), fpMethod);
            } else {
                if (fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_IUCD)) {
                    serviceDueDate = (DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(fp_date).plusMonths(4)).toDate();
                    serviceOverDueDate = (DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(fp_date).plusMonths(4).plusWeeks(1)).toDate();
                    serviceName = MessageFormat.format(context.getString(R.string.follow_up_two), fpMethod);
                } else {
                    count = FpDao.getCountFpVisits(memberObject.getBaseEntityId(), FamilyPlanningConstants.EventType.FP_FOLLOW_UP_VISIT, fpMethod);
                    if (count == 2) {
                        serviceDueDate = (DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(fp_date).plusMonths(1)).toDate();
                        serviceOverDueDate = (DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(fp_date).plusMonths(1).plusWeeks(2)).toDate();
                        serviceName = MessageFormat.format(context.getString(R.string.follow_up_three), fpMethod);
                    } else {
                        serviceDueDate = (DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(fp_date).plusDays(7)).toDate();
                        serviceOverDueDate = (DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(fp_date).plusDays(9)).toDate();
                        serviceName = MessageFormat.format(context.getString(R.string.follow_up_two), fpMethod);
                    }
                }
            }
        }
        BaseUpcomingService upcomingService = new BaseUpcomingService();
        upcomingService.setServiceDate(serviceDueDate);
        upcomingService.setOverDueDate(serviceOverDueDate);
        upcomingService.setServiceName(serviceName);
        serviceList.add(upcomingService);

    }

}
