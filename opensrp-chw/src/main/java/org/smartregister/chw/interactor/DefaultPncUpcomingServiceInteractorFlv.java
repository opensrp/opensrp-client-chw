package org.smartregister.chw.interactor;


import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.dao.AlertDao;
import org.smartregister.chw.core.rule.PNCHealthFacilityVisitRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.VaccineScheduleUtil;
import org.smartregister.chw.dao.PNCDao;
import org.smartregister.chw.domain.PNCHealthFacilityVisitSummary;
import org.smartregister.chw.util.PNCVisitUtil;
import org.smartregister.domain.Alert;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class DefaultPncUpcomingServiceInteractorFlv implements PncUpcomingServiceInteractor.Flavor {
    protected MemberObject memberObject;
    protected Context context;

    @Override
    public List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject) {
        this.memberObject = memberObject;
        this.context = context;
        VaccineScheduleUtil.updateOfflineAlerts(memberObject.getBaseEntityId(), new DateTime(memberObject.getDob()), CoreConstants.SERVICE_GROUPS.CHILD);
        List<BaseUpcomingService> serviceList = new ArrayList<>();

        evaluateHealthFacility(serviceList);
        evaluateImmunization(serviceList);

        return serviceList;
    }

    private void evaluateHealthFacility(List<BaseUpcomingService> serviceList) {
        // create data to display
        PNCHealthFacilityVisitSummary summary = PNCDao.getLastHealthFacilityVisitSummary(memberObject.getBaseEntityId());
        if (summary != null) {
            PNCHealthFacilityVisitRule visitRule = PNCVisitUtil.getNextPNCHealthFacilityVisit(summary.getDeliveryDate(), summary.getLastVisitDate());

            if (visitRule != null && visitRule.getVisitName() != null) {

                int visit_num;
                switch (visitRule.getVisitName()) {
                    case "1":
                        visit_num = 1;
                        break;
                    case "7":
                        visit_num = 2;
                        break;
                    case "42":
                        visit_num = 3;
                        break;
                    default:
                        visit_num = 1;
                        break;
                }
                BaseUpcomingService upcomingService = new BaseUpcomingService();
                upcomingService.setServiceDate(visitRule.getDueDate().toDate());
                upcomingService.setServiceName(MessageFormat.format(context.getString(R.string.pnc_health_facility_visit_num), visit_num));
                serviceList.add(upcomingService);
            }

        }
    }

    private void evaluateImmunization(List<BaseUpcomingService> serviceList) {
        List<Alert> alerts = AlertDao.getActiveAlertsForVaccines(memberObject.getBaseEntityId());
        if (alerts == null || alerts.size() == 0) {
            return;
        }

        BaseUpcomingService upcomingService = new BaseUpcomingService();
        upcomingService.setServiceDate(new LocalDate(alerts.get(0).startDate()).toDate());
        upcomingService.setServiceName(context.getString(R.string.immunizations, context.getString(R.string.at_birth)));
        List<BaseUpcomingService> subService = new ArrayList<>();
        for (Alert alert : alerts) {
            subService.add(new BaseUpcomingService(alert.scheduleName()));
        }
        upcomingService.setUpcomingServiceList(subService);
        serviceList.add(upcomingService);

    }
}
