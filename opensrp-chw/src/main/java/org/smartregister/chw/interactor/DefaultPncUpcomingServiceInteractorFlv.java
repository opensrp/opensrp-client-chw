package org.smartregister.chw.interactor;


import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.rule.PNCHealthFacilityVisitRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.VaccineScheduleUtil;
import org.smartregister.chw.dao.ChwPNCDao;
import org.smartregister.chw.domain.PNCHealthFacilityVisitSummary;
import org.smartregister.chw.pnc.PncLibrary;
import org.smartregister.chw.util.PNCVisitUtil;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DefaultPncUpcomingServiceInteractorFlv implements PncUpcomingServiceInteractor.Flavor {
    protected MemberObject memberObject;
    protected Context context;
    private Integer count;


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
        PNCHealthFacilityVisitSummary summary = ChwPNCDao.getLastHealthFacilityVisitSummary(memberObject.getBaseEntityId());
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
                upcomingService.setOverDueDate(visitRule.getOverDueDate().toDate());
                upcomingService.setServiceName(MessageFormat.format(context.getString(R.string.pnc_health_facility_visit_num), visit_num));
                serviceList.add(upcomingService);
            }

        }
    }

    private void evaluateImmunization(List<BaseUpcomingService> serviceList) {
         Date deliveryDate = new Date();
         Date OverDueDate = new Date();
        Map<String, String> alerts = ChwPNCDao.getPNCImmunizationAtBirth(memberObject.getBaseEntityId());
        BaseUpcomingService upcomingService = new BaseUpcomingService();
        try {
            deliveryDate = new SimpleDateFormat("dd-MM-yyyy").parse(PncLibrary.getInstance().profileRepository().getDeliveryDate(memberObject.getBaseEntityId()));
            OverDueDate = new DateTime(deliveryDate).plusDays(13).toDate();
        } catch (Exception e) {
        }
        if (Days.daysBetween(new DateTime(new DateTime(deliveryDate).plusDays(27).toDate()).toLocalDate(), new DateTime().toLocalDate()).getDays() < 28) {
            if (alerts.size() > 0) {
                for (Map.Entry<String, String> alert : alerts.entrySet()) {
                    if (alert.getValue().equalsIgnoreCase("Vaccine not given")) {
                        count += count;
                    }
                }
            }
            if (alerts.size() == 0 || count == 2) {
                upcomingService.setServiceName(context.getString(R.string.upcoming_immunizations, context.getString(R.string.at_birth), context.getString(R.string.bcg), context.getString(R.string.opv_0)));
            } else {
                for (Map.Entry<String, String> alert : alerts.entrySet()) {
                    if (alert.getKey().equalsIgnoreCase(context.getString(R.string.opv_0))) {
                        upcomingService.setServiceName(context.getString(R.string.up_immunizations, context.getString(R.string.at_birth), context.getString(R.string.opv_0)));
                    } else {
                        upcomingService.setServiceName(context.getString(R.string.up_immunizations, context.getString(R.string.at_birth), context.getString(R.string.bcg)));
                    }
                }
            }
            upcomingService.setServiceDate(deliveryDate);
            upcomingService.setOverDueDate(OverDueDate);
            serviceList.add(upcomingService);
        }
    }
}
