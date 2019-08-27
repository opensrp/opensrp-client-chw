package org.smartregister.chw.interactor;

import android.content.Context;

import org.joda.time.DateTime;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.rule.PNCHealthFacilityVisitRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.VaccineScheduleUtil;
import org.smartregister.chw.dao.PNCDao;
import org.smartregister.chw.domain.PNCHealthFacilityVisitSummary;
import org.smartregister.chw.util.PNCVisitUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class PncUpcomingServicesInteractorFlv extends DefaultPncUpcomingServiceInteractorFlv {
    protected MemberObject memberObject;
    protected Context context;


    @Override
    public List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject) {
        this.memberObject = memberObject;
        this.context = context;
        VaccineScheduleUtil.updateOfflineAlerts(memberObject.getBaseEntityId(), new DateTime(memberObject.getDob()), CoreConstants.SERVICE_GROUPS.CHILD);
        List<BaseUpcomingService> serviceList = new ArrayList<>();
        evaluateHealthFacility(serviceList);
        return serviceList;
    }


    private void evaluateHealthFacility(List<BaseUpcomingService> serviceList) {
        // create data to display
        PNCHealthFacilityVisitSummary summary = PNCDao.getLastHealthFacilityVisitSummary(memberObject.getBaseEntityId());
        if (summary != null) {
            PNCHealthFacilityVisitRule visitRule = PNCVisitUtil.getNextPNCHealthFacilityVisit(summary.getDeliveryDate(), summary.getLastVisitDate());

            if (visitRule != null && visitRule.getVisitName() != null) {

                String visit_num;
                switch (visitRule.getVisitName()) {
                    case "1":
                        visit_num = "48 hours";
                        break;
                    case "3":
                        visit_num = "days 3-7";
                        break;
                    case "8":
                        visit_num = "days 8-28";
                        break;
                    case "29":
                        visit_num = "days 29-42";
                        break;
                    default:
                        visit_num = "48 hours";
                        break;
                }
                BaseUpcomingService upcomingService = new BaseUpcomingService();
                upcomingService.setServiceDate(visitRule.getDueDate().toDate());
                upcomingService.setServiceName(MessageFormat.format(context.getString(R.string.pnc_health_facility_visit_num), visit_num));
                serviceList.add(upcomingService);
            }
        }
    }
}
