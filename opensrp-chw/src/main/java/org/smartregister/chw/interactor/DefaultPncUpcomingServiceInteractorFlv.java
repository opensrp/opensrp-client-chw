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

        if (visitDetailList.size() < 3) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String sd = sdf.format(summary.getDeliveryDate());
                if (visitDetailList.size() == 0 && ((DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(sd).plusDays(7)).isAfter(new org.joda.time.DateTime().toLocalDate()))) {
                    serviceDueDate = formattedDate(sd, 1);
                    serviceOverDueDate = formattedDate(sd, 2);
                    serviceName = serviceName("Day 1");
                } else {
                    for (VisitDetail detail : visitDetailList) {
                        details = String.valueOf(detail.getVisitKey()).replaceAll("\\D+", "");
                    }
                    if ((details.equalsIgnoreCase("2") || isValid(sd, 42, 43)) && !(details.equalsIgnoreCase("3"))) {
                        serviceDueDate = formattedDate(sd, 42);
                        serviceOverDueDate = formattedDate(sd, 43);
                        serviceName = serviceName("Day 42");
                    } else if (details.equalsIgnoreCase("1") || isValid(sd, 7, 42)) {
                        serviceDueDate = formattedDate(sd, 7);
                        serviceOverDueDate = formattedDate(sd, 8);
                        serviceName = serviceName("Day 7");
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


    private String ImmunizationServiceName(PncBaby baby) {
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
        int count = 0;
        try {
            deliveryDate = new SimpleDateFormat("dd-MM-yyyy").parse(PncLibrary.getInstance().profileRepository().getDeliveryDate(memberObject.getBaseEntityId()));
            OverDueDate = new DateTime(deliveryDate).plusDays(13).toDate();
        } catch (Exception e) {
            Timber.v(e.toString());
        }
        List<PncBaby> pncBabies = PersonDao.getMothersPNCBabies(memberObject.getBaseEntityId());
        for (PncBaby baby : pncBabies) {
            if (ImmunizationServiceName(baby) != null) {
                upcomingService.setServiceName(ImmunizationServiceName(baby));
                upcomingService.setServiceDate(deliveryDate);
                upcomingService.setOverDueDate(OverDueDate);
                count += 1;
            }
        }
        if (count > 0) {
            serviceList.add(upcomingService);
        }
    }

}
