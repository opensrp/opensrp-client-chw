package org.smartregister.chw.interactor;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.VaccineScheduleUtil;
import org.smartregister.chw.dao.ChwPNCDao;
import org.smartregister.chw.domain.PNCHealthFacilityVisitSummary;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

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

    private Date formattedDate(String sd, int period) {
        return (dateTimeFormatter.parseLocalDate(sd).plusDays(period)).toDate();
    }

    private boolean isValid(String deliveryDate, int due, int expiry) {
        return ((((dateTimeFormatter).parseLocalDate(deliveryDate).plusDays(due)).isBefore(today)) &&
                (((dateTimeFormatter).parseLocalDate(deliveryDate).plusDays(expiry)).isAfter(today)));
    }

    private String serviceName(String val) {
        return MessageFormat.format(context.getString(R.string.pnc_health_facility_visit_num), val);
    }

    private void evaluateHealthFacility(List<BaseUpcomingService> serviceList) {
        //Get done Visits
        Date serviceDueDate;
        Date serviceOverDueDate;
        String serviceName;
        String details;
        List<VisitDetail> visitDetailList = ChwPNCDao.getLastPNCHealthFacilityVisits(memberObject.getBaseEntityId());
        PNCHealthFacilityVisitSummary summary = ChwPNCDao.getLastHealthFacilityVisitSummary(memberObject.getBaseEntityId());

        //There are four health facility visits hence the  upcoming services is only valid when only 3 visits have been done
        if ( summary != null && summary.getDeliveryDate() != null && visitDetailList != null && visitDetailList.size() < 4) {
            try {
               @NotNull String deliveryDate = simpleDateFormat.format(summary.getDeliveryDate());
                if (visitDetailList.size() == 0 && ((dateTimeFormatter.parseLocalDate(deliveryDate).plusDays(3)).isAfter(today))) {
                    serviceDueDate = (dateTimeFormatter.parseLocalDate(deliveryDate)).toDate();
                    serviceOverDueDate = formattedDate(deliveryDate, 2);
                    serviceName = serviceName("48 hours");
                } else {
                    details = String.valueOf(visitDetailList.get(0).getVisitKey()).replaceAll("\\D+", "");
                    if (!("4".equals(details)) && ( "3".equals(details) || isValid(deliveryDate, 29, 36) )) {
                        serviceDueDate = formattedDate(deliveryDate, 29);
                        serviceOverDueDate = formattedDate(deliveryDate, 36);
                        serviceName = serviceName("Day 29-42");
                    } else if ( "2".equals(details) || isValid(deliveryDate, 8, 28)) {
                        serviceDueDate = formattedDate(deliveryDate, 8);
                        serviceOverDueDate = formattedDate(deliveryDate, 18);
                        serviceName = serviceName("Day 8-28");
                    } else if ( "1".equals(details) || isValid(deliveryDate, 3, 8)) {
                        serviceDueDate = formattedDate(deliveryDate, 3);
                        serviceOverDueDate = formattedDate(deliveryDate, 5);
                        serviceName = serviceName("Day 3-7");
                    } else {
                        serviceDueDate = (dateTimeFormatter.parseLocalDate(deliveryDate)).toDate();
                        serviceOverDueDate = (dateTimeFormatter.parseLocalDate(deliveryDate)).toDate();
                        serviceName = null;
                    }
                }
                BaseUpcomingService upcomingService = new BaseUpcomingService();
                if (StringUtils.isNotBlank(serviceName)) {
                    upcomingService.setServiceDate(serviceDueDate);
                    upcomingService.setOverDueDate(serviceOverDueDate);
                    upcomingService.setServiceName(serviceName);
                    serviceList.add(upcomingService);
                }
            } catch (Exception e) {
                Timber.v(e.toString());
            }
        }

    }
}
