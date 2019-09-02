package org.smartregister.chw.core.interactor;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.interactor.BaseAncUpcomingServicesInteractor;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.dao.VisitDao;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.RecurringServiceUtil;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.core.utils.VaccineScheduleUtil;
import org.smartregister.chw.core.utils.VisitVaccineUtil;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.jsonmapping.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinatorUtils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class CoreChildUpcomingServiceInteractor extends BaseAncUpcomingServicesInteractor {

    private Context context;
    private MemberObject memberObject;
    private List<BaseUpcomingService> upcomingServices = new ArrayList<>();
    private Date dob;

    @Override
    protected List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject) {
        this.context = context;
        this.memberObject = memberObject;
        try {
            this.dob = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(memberObject.getDob());
        } catch (ParseException e) {
            Timber.e(e);
        }

        evaluateUpcomingServicesVaccines();
        evaluateUpcomingServicesServices();

        return upcomingServices;
    }

    private void evaluateUpcomingServicesVaccines() {

        List<VaccineGroup> vaccineGroups = VaccineScheduleUtil.getVaccineGroups(CoreChwApplication.getInstance().getApplicationContext(), CoreConstants.SERVICE_GROUPS.CHILD);
        List<Vaccine> specialVaccines = VaccinatorUtils.getSpecialVaccines(context);
        VaccineRepository vaccineRepository = CoreChwApplication.getInstance().vaccineRepository();
        List<org.smartregister.immunization.domain.Vaccine> vaccines = vaccineRepository.findByEntityId(memberObject.getBaseEntityId());

        // get the schedule
        HashMap<String, HashMap<String, VaccineSchedule>> vaccineSchedules = VisitVaccineUtil.getSchedule(vaccineGroups, specialVaccines, CoreConstants.SERVICE_GROUPS.CHILD);

        // get all the alerts for the child
        List<Alert> alerts = VisitVaccineUtil.getInMemoryAlerts(vaccineSchedules, memberObject.getBaseEntityId(), new DateTime(dob), CoreConstants.SERVICE_GROUPS.CHILD, vaccines);

        Map<String, Alert> alertMap = new HashMap<>();
        for (Alert alert : alerts) {
            alertMap.put(alert.scheduleName().toLowerCase().replace(" ", ""), alert);
        }

        // remove all given vaccines
        // map of all vaccines
        Map<String, Date> givenVaccines = new HashMap<>();
        for (org.smartregister.immunization.domain.Vaccine vaccine : vaccines) {
            givenVaccines.put(vaccine.getName().replace("_", "").replace(" ", ""), vaccine.getDate());
        }

        // partially received vaccines
        Map<String, Date> partialGiven = VisitDao.getUnprocessedVaccines(memberObject.getBaseEntityId());
        for (Map.Entry<String, Date> entry : partialGiven.entrySet()) {
            givenVaccines.put(entry.getKey().replace("_", ""), entry.getValue());
        }

        // add the service for each vaccine
        for (VaccineGroup group : vaccineGroups) {
            List<BaseUpcomingService> groupServices = new ArrayList<>();
            for (org.smartregister.immunization.domain.jsonmapping.Vaccine jsonVaccine : group.vaccines) {
                String code = jsonVaccine.name.toLowerCase().replace(" ", "");
                Alert alert = alertMap.get(code);
                Date vaccine = givenVaccines.get(code);

                // if not given display
                if (alert != null && vaccine == null) {
                    BaseUpcomingService service = new BaseUpcomingService();
                    service.setServiceDate(new LocalDate(alert.startDate()).toDate());
                    service.setServiceName(getTranslatedText(alert.scheduleName()));
                    groupServices.add(service);
                }
            }

            if (groupServices.size() > 0) {
                BaseUpcomingService upcomingService = new BaseUpcomingService();

                String title = MessageFormat.format(
                        context.getString(org.smartregister.chw.core.R.string.immunizations_count),
                        VisitVaccineUtil.getVaccineTitle(group.name, context)
                );
                upcomingService.setServiceName(title);
                upcomingService.setServiceDate(groupServices.get(0).getServiceDate());
                upcomingService.setUpcomingServiceList(groupServices);
                upcomingServices.add(upcomingService);
            }
        }
    }

    private String getTranslatedText(String vaccine) {
        String val = vaccine.toLowerCase().replace(" ", "_").trim();
        return Utils.getStringResourceByName(val, context).trim();
    }

    private void evaluateUpcomingServicesServices() {

        Map<String, ServiceWrapper> serviceWrapperMap =
                RecurringServiceUtil.getRecurringServices(
                        memberObject.getBaseEntityId(),
                        new DateTime(dob),
                        CoreConstants.SERVICE_GROUPS.CHILD,
                        true
                );

        for (Map.Entry<String, ServiceWrapper> entry : serviceWrapperMap.entrySet()) {

            if ("Exclusive breastfeeding".equalsIgnoreCase(entry.getKey()))
                continue; // escape breastfeeding

            String serviceIteration = entry.getValue().getName().substring(entry.getValue().getName().length() - 1);

            BaseUpcomingService upcomingService = new BaseUpcomingService();
            upcomingService.setServiceDate(entry.getValue().getVaccineDate().toDate());
            upcomingService.setServiceName(getTranslatedService(entry.getKey(), serviceIteration));

            upcomingServices.add(upcomingService);
        }
    }

    private String getTranslatedService(String val, String serviceNum) {
        switch (val) {
            case "Deworming":
                return context.getString(R.string.deworming_number_dose, Utils.getDayOfMonthWithSuffix(Integer.valueOf(serviceNum), context));
            case "MNP":
                return context.getString(R.string.mnp_number_pack, Utils.getDayOfMonthWithSuffix(Integer.valueOf(serviceNum), context));
            case "Vitamin A":
                return context.getString(R.string.vitamin_a_number_dose, Utils.getDayOfMonthWithSuffix(Integer.valueOf(serviceNum), context));
        }
        return val;
    }

}
