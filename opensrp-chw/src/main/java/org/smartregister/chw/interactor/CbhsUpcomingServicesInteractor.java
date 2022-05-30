package org.smartregister.chw.interactor;

import android.content.Context;

import org.jeasy.rules.api.Rules;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.interactor.BaseAncUpcomingServicesInteractor;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.ChwCBHSDao;
import org.smartregister.chw.hiv.dao.HivDao;
import org.smartregister.chw.hiv.domain.HivAlertObject;
import org.smartregister.chw.rule.CbhsFollowupRule;
import org.smartregister.chw.util.ChwHomeVisitUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CbhsUpcomingServicesInteractor extends BaseAncUpcomingServicesInteractor {
    protected MemberObject memberObject;
    protected Context context;

    public static Rules getHivRules() {
        return CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.HIV_FOLLOW_UP_VISIT);
    }

    @Override
    public List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject) {
        this.memberObject = memberObject;
        this.context = context;
        List<BaseUpcomingService> serviceList = new ArrayList<>();
        evaluateHiv(serviceList);
        return serviceList;
    }

    private void evaluateHiv(List<BaseUpcomingService> serviceList) {
        String hiv_date = null;
        Date serviceDueDate = null;
        Date serviceOverDueDate = null;
        String serviceName = null;

        List<HivAlertObject> hivList = HivDao.getHivDetails(memberObject.getBaseEntityId());
        if (hivList.size() > 0) {
            for (HivAlertObject hiv : hivList) {
                hiv_date = hiv.getHivStartDate();
            }
        }

        Date hivDate = new Date(new BigDecimal(hiv_date).longValue());
        Date nextVisitDate = ChwCBHSDao.getNextVisitDate(memberObject.getBaseEntityId());

        CbhsFollowupRule alertRule = ChwHomeVisitUtil.getCBHSVisitStatus(nextVisitDate, hivDate);
        serviceDueDate = alertRule.getDueDate();
        serviceOverDueDate = alertRule.getOverDueDate();
        serviceName = context.getString(R.string.cbhs_follow_up_visit);

        BaseUpcomingService upcomingService = new BaseUpcomingService();
        if (serviceName != null) {
            upcomingService.setServiceDate(serviceDueDate);
            upcomingService.setOverDueDate(serviceOverDueDate);
            upcomingService.setServiceName(serviceName);
            serviceList.add(upcomingService);
        }

    }

}
