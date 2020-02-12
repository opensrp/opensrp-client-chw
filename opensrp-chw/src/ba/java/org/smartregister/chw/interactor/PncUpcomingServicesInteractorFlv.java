package org.smartregister.chw.interactor;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.VaccineScheduleUtil;
import org.smartregister.chw.dao.ChwPNCDao;
import org.smartregister.chw.domain.PNCHealthFacilityVisitSummary;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    private Date formattedDate(String sd, int dt) {
        return (DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(sd).plusDays(dt)).toDate();
    }

    private boolean isValid(String sd, int due, int expiry) {
        return (((DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(sd).plusDays(due)).isBefore(new org.joda.time.DateTime().toLocalDate())) &&
                (((DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(sd).plusDays(expiry)).isAfter(new org.joda.time.DateTime().toLocalDate()))));
    }

    private String serviceName(String val) {
        return MessageFormat.format(context.getString(R.string.pnc_health_facility_visit_num), val);
    }

    private void evaluateHealthFacility(List<BaseUpcomingService> serviceList) {
        //Get done Visits
        Date serviceDueDate = null;
        Date serviceOverDueDate = null;
        String serviceName = null;
        String details = "";
        int count = 0;
        List<VisitDetail> visitDetailList = ChwPNCDao.getLastPNCHealthFacilityVisits(memberObject.getBaseEntityId());
        PNCHealthFacilityVisitSummary summary = ChwPNCDao.getLastHealthFacilityVisitSummary(memberObject.getBaseEntityId());

        if (visitDetailList.size() < 4) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String sd = sdf.format(summary.getDeliveryDate());
                if (visitDetailList.size() == 0 && ((DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(sd).plusDays(3)).isAfter(new org.joda.time.DateTime().toLocalDate()))) {
                    serviceDueDate = (DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(sd)).toDate();
                    serviceOverDueDate = formattedDate(sd, 2);
                    serviceName = serviceName("48 hours");
                } else {
                    for (VisitDetail detail : visitDetailList) {
                        details = String.valueOf(detail.getVisitKey()).replaceAll("\\D+", "");
                    }
                    if ((details.equalsIgnoreCase("3") || isValid(sd, 29, 36)) && !(details.equalsIgnoreCase("4"))) {
                        serviceDueDate = formattedDate(sd, 29);
                        serviceOverDueDate = formattedDate(sd, 36);
                        serviceName = serviceName("Day 29-42");
                    } else if (details.equalsIgnoreCase("2") || isValid(sd, 8, 28)) {
                        serviceDueDate = formattedDate(sd, 8);
                        serviceOverDueDate = formattedDate(sd, 18);
                        serviceName = serviceName("Day 8-28");
                    } else if (details.equalsIgnoreCase("1") || isValid(sd, 3, 8)) {
                        serviceDueDate = formattedDate(sd, 3);
                        serviceOverDueDate = formattedDate(sd, 5);
                        serviceName = serviceName("Day 3-7");
                    } else {
                        serviceDueDate = (DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(sd)).toDate();
                        serviceOverDueDate = (DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(sd)).toDate();
                        serviceName = "";
                    }
                }
                BaseUpcomingService upcomingService = new BaseUpcomingService();
                if (!serviceName.equalsIgnoreCase("")) {
                    upcomingService.setServiceDate(serviceDueDate);
                    upcomingService.setOverDueDate(serviceOverDueDate);
                    upcomingService.setServiceName(serviceName);
                    count += 1;
                }
                if (count > 0) {
                    serviceList.add(upcomingService);
                }
            } catch (Exception e) {
                Timber.v(e.toString());
            }
        }

    }
}
