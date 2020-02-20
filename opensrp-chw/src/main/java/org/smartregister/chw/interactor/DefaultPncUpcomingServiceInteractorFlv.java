package org.smartregister.chw.interactor;


import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.VaccineScheduleUtil;
import org.smartregister.chw.core.utils.VisitVaccineUtil;
import org.smartregister.chw.dao.ChwPNCDao;
import org.smartregister.chw.dao.PersonDao;
import org.smartregister.chw.domain.PNCHealthFacilityVisitSummary;
import org.smartregister.chw.domain.PncBaby;
import org.smartregister.chw.pnc.PncLibrary;
import org.smartregister.domain.Alert;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class DefaultPncUpcomingServiceInteractorFlv implements PncUpcomingServiceInteractor.Flavor {
    protected MemberObject memberObject;
    protected Context context;
    protected DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd-MM-yyyy");
    protected LocalDate today = new DateTime().toLocalDate();
    protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());


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


    private Date formattedDate(String deliveryDate, int period) {
        return (dateTimeFormatter.parseLocalDate(deliveryDate).plusDays(period)).toDate();
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

        //There are four health facility visits hence the upcoming services is only valid when only 2 visits have been done
        if ( summary != null && summary.getDeliveryDate() != null && visitDetailList != null && visitDetailList.size() < 3 ) {
            try {
                String deliveryDate = simpleDateFormat.format(summary.getDeliveryDate());
                if (visitDetailList.size() == 0 && ((dateTimeFormatter.parseLocalDate(deliveryDate).plusDays(7)).isAfter(today))) {
                    serviceDueDate = formattedDate(deliveryDate, 1);
                    serviceOverDueDate = formattedDate(deliveryDate, 2);
                    serviceName = serviceName("Day 1");
                } else {
                        details = String.valueOf(visitDetailList.get(0).getVisitKey()).replaceAll("\\D+", "");

                    if (!(details.equals("3")) && (  details.equals("2") || isValid(deliveryDate, 42, 43))) {
                        serviceDueDate = formattedDate(deliveryDate, 42);
                        serviceOverDueDate = formattedDate(deliveryDate, 43);
                        serviceName = serviceName("Day 42");
                    } else if (details.equals("1") || isValid(deliveryDate, 7, 42)) {
                        serviceDueDate = formattedDate(deliveryDate, 7);
                        serviceOverDueDate = formattedDate(deliveryDate, 8);
                        serviceName = serviceName("Day 7");
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

    private String immunizationServiceName(PncBaby baby) {
        String serviceName = null;
        List<Alert> alertList = VisitVaccineUtil.getNextVaccines(baby.getBaseEntityID(), new DateTime(baby.getDob()), CoreConstants.SERVICE_GROUPS.CHILD, true);
        if (alertList.size() == 2) {
            serviceName = context.getString(R.string.upcoming_immunizations, context.getString(R.string.at_birth), baby.getFirstName(), context.getString(R.string.bcg), context.getString(R.string.opv_0));
        } else {
            for (Alert alert : alertList) {
                if (alert.scheduleName().toLowerCase().replace(" ", "").replace("_", "").equalsIgnoreCase(context.getString(R.string.opv_0).replace(" ", ""))) {
                    serviceName = context.getString(R.string.up_immunizations, context.getString(R.string.at_birth), baby.getFirstName(), context.getString(R.string.opv_0));
                } else if (alert.scheduleName().toLowerCase().replace(" ", "").replace("_", "").equalsIgnoreCase(context.getString(R.string.bcg))) {
                    serviceName = context.getString(R.string.up_immunizations, context.getString(R.string.at_birth), baby.getFirstName(), context.getString(R.string.bcg));
                }
            }
        }
        return serviceName;
    }

    private void evaluateImmunization(List<BaseUpcomingService> serviceList) {
        Date deliveryDate = new Date();
        Date OverDueDate = new Date();
        BaseUpcomingService upcomingService = new BaseUpcomingService();
        try {
            deliveryDate = simpleDateFormat.parse(PncLibrary.getInstance().profileRepository().getDeliveryDate(memberObject.getBaseEntityId()));
            OverDueDate = new DateTime(deliveryDate).plusDays(13).toDate();
        } catch (Exception e) {
            Timber.v(e.toString());
        }
        List<PncBaby> pncBabies = PersonDao.getMothersPNCBabies(memberObject.getBaseEntityId());
        for (PncBaby baby : pncBabies) {
            if (immunizationServiceName(baby) != null) {
                upcomingService.setServiceName(immunizationServiceName(baby));
                upcomingService.setServiceDate(deliveryDate);
                upcomingService.setOverDueDate(OverDueDate);
            }
        }
        serviceList.add(upcomingService);
    }

}
