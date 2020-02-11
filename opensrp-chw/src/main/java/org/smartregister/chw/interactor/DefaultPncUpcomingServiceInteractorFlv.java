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
import org.smartregister.chw.dao.PersonDao;
import org.smartregister.chw.domain.PNCHealthFacilityVisitSummary;
import org.smartregister.chw.domain.PncBaby;
import org.smartregister.chw.pnc.PncLibrary;
import org.smartregister.immunization.domain.Vaccine;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    private void evaluateHealthFacility(List<BaseUpcomingService> serviceList) {
        //Get done Visits
        Date serviceDueDate = null;
        Date serviceOverDueDate = null;
        String serviceName = null;
        List<VisitDetail> visitDetailList = ChwPNCDao.getAllPNCHealthFacilityVisits(memberObject.getBaseEntityId());
        PNCHealthFacilityVisitSummary summary = ChwPNCDao.getLastHealthFacilityVisitSummary(memberObject.getBaseEntityId());
        if (summary != null && visitDetailList.size() < 3) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String sd = sdf.format(summary.getDeliveryDate());
                switch (visitDetailList.size()) {
                    case 2:
                        serviceDueDate = (DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(sd).plusDays(42)).toDate();
                        serviceOverDueDate = (DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(sd).plusDays(43)).toDate();
                        serviceName = MessageFormat.format(context.getString(R.string.pnc_health_facility_visit_num), 42);
                        break;
                    case 1:
                        serviceDueDate = (DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(sd).plusDays(7)).toDate();
                        serviceOverDueDate = (DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(sd).plusDays(8)).toDate();
                        serviceName = MessageFormat.format(context.getString(R.string.pnc_health_facility_visit_num), 7);
                        break;
                    default:
                        serviceDueDate = (DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(sd).plusDays(1)).toDate();
                        serviceOverDueDate = (DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(sd).plusDays(2)).toDate();
                        serviceName = MessageFormat.format(context.getString(R.string.pnc_health_facility_visit_num), 1);
                        break;
                }
                BaseUpcomingService upcomingService = new BaseUpcomingService();
                upcomingService.setServiceDate(serviceDueDate);
                upcomingService.setOverDueDate(serviceOverDueDate);
                upcomingService.setServiceName(serviceName);
                serviceList.add(upcomingService);

            } catch (Exception e) {
                Timber.v(e.toString());
            }
        }
    }

    private Integer bcgCount(Map<String, String> alerts) {
        Integer bcgDone = 0;
        if (alerts.size() > 0) {
            for (Map.Entry<String, String> alert : alerts.entrySet()) {
                if (alert.getKey().equalsIgnoreCase(context.getString(R.string.bcg))) {
                    bcgDone += 1;
                }
            }
        }
        return bcgDone;
    }

    private Integer opvCount(Map<String, String> alerts) {
        Integer opvDone = 0;
        if (alerts.size() > 0) {
            for (Map.Entry<String, String> alert : alerts.entrySet()) {
                if (alert.getKey().equalsIgnoreCase(context.getString(R.string.opv_0).replace(" ", ""))) {
                    opvDone += 1;
                }
            }
        }
        return opvDone;

    }

    private String serviceName(PncBaby baby) {
        String serviceName = null;
            List<Vaccine> vaccines = ChwPNCDao.getPncChildVaccines(baby.getBaseEntityID());
            Map<String, String> alerts = ChwPNCDao.getPNCImmunizationAtBirth(baby.getBaseEntityID());
            int bcgCount = bcgCount(alerts);
            int opvCount = opvCount(alerts);
            if (vaccines.size() > 1) {
                //   return;
            } else if (vaccines.size() == 0) {
                if(alerts.size() == 0 || (opvCount == 0  && bcgCount == 0)){
                    serviceName = context.getString(R.string.upcoming_immunizations, context.getString(R.string.at_birth), baby.getFirstName(), context.getString(R.string.bcg), context.getString(R.string.opv_0));
                }
                else {
                    if (opvCount > 0 && bcgCount == 0) {
                        serviceName = context.getString(R.string.up_immunizations, context.getString(R.string.at_birth), baby.getFirstName(), context.getString(R.string.bcg));
                    } else if (opvCount == 0 && bcgCount > 0) {
                        serviceName = context.getString(R.string.up_immunizations, context.getString(R.string.at_birth), baby.getFirstName(), context.getString(R.string.opv_0));
                    }
                }

            } else {
                if (alerts.size() > 0) {
                    if (vaccines.get(0).getName().equalsIgnoreCase(context.getString(R.string.opv_0))) {
                        if (bcgCount == 0) {
                            serviceName = context.getString(R.string.up_immunizations, context.getString(R.string.at_birth), baby.getFirstName(), context.getString(R.string.bcg));
                        }
                    } else if (vaccines.get(0).getName().equalsIgnoreCase(context.getString(R.string.bcg))) {
                        if (opvCount == 0) {
                            serviceName = context.getString(R.string.up_immunizations, context.getString(R.string.at_birth), baby.getFirstName(), context.getString(R.string.opv_0));
                        }
                    }
                } else {
                    if (vaccines.get(0).getName().equalsIgnoreCase(context.getString(R.string.opv_0))) {
                        serviceName = context.getString(R.string.up_immunizations, context.getString(R.string.at_birth), baby.getFirstName(), context.getString(R.string.bcg));
                    } else  {
                        serviceName = context.getString(R.string.up_immunizations, context.getString(R.string.at_birth), baby.getFirstName(), context.getString(R.string.opv_0));
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
            deliveryDate = new SimpleDateFormat("dd-MM-yyyy").parse(PncLibrary.getInstance().profileRepository().getDeliveryDate(memberObject.getBaseEntityId()));
            OverDueDate = new DateTime(deliveryDate).plusDays(13).toDate();
        } catch (Exception e) {
            Timber.v(e.toString());
        }
        List<PncBaby> pncBabies = PersonDao.getMothersPNCBabies(memberObject.getBaseEntityId());
        for(PncBaby baby: pncBabies){
            if(serviceName(baby) != null){
                upcomingService.setServiceName(serviceName(baby));
                upcomingService.setServiceDate(deliveryDate);
                upcomingService.setOverDueDate(OverDueDate);
            }
        }
        serviceList.add(upcomingService);
    }

}
