package org.smartregister.chw.util;

import com.opensrp.chw.core.helper.ContactRule;
import com.opensrp.chw.core.model.VaccineTaskModel;
import com.opensrp.chw.core.utils.ChwServiceSchedule;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.service.AlertService;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.immunization.util.VaccinatorUtils.generateScheduleList;
import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;

public class ContactUtil {

    /**
     * Returns the contact schedule in weeks for the given contact
     *
     * @param lastContact
     * @param lastMenstrualPeriod
     * @return
     */
    public static Map<Integer, LocalDate> getContactWeeks(Boolean isFirst, LocalDate lastContact, LocalDate lastMenstrualPeriod) {

        try {

            int gestationAge = Days.daysBetween(lastMenstrualPeriod, lastContact).getDays() / 7;

            Map<Integer, LocalDate> dateMap = new LinkedHashMap<>();
            ContactRule contactRule = new ContactRule(gestationAge, isFirst);

            LocalDate expectedDeliveryDate = lastMenstrualPeriod.plusDays(280);

            // gets the list of contacts
            List<Integer> weeks = ChwApplication.getInstance().getRulesEngineHelper()
                    .getContactVisitSchedule(contactRule, Constants.RULE_FILE.CONTACT_RULES);

            boolean visitAfterEdd = false;
            for (Integer i : weeks) {

                LocalDate visitDate = lastMenstrualPeriod.plusWeeks(i).plusDays(1);
                if (visitDate.isBefore(expectedDeliveryDate) || visitDate.isEqual(expectedDeliveryDate)) {
                    dateMap.put(i, visitDate);
                } else {
                    visitAfterEdd = true;
                }
            }

            // remove a day form the last day
            if (visitAfterEdd) {
                dateMap.put(weeks.get(dateMap.size()), expectedDeliveryDate);
            }

            return dateMap;
        } catch (Exception e) {
            Timber.e(e);
        }

        return new LinkedHashMap<>();
    }

    public static Map<Integer, LocalDate> getContactSchedule(MemberObject memberObject) {
        return getContactSchedule(memberObject, LocalDate.now());
    }

    public static Map<Integer, LocalDate> getContactSchedule(MemberObject memberObject, LocalDate startDate) {

        LocalDate lastContact = new DateTime(memberObject.getDateCreated()).toLocalDate();
        boolean isFirst = (StringUtils.isBlank(memberObject.getLastContactVisit()));
        LocalDate lastMenstrualPeriod = DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(memberObject.getLastMenstrualPeriod());

        if (StringUtils.isNotBlank(memberObject.getLastContactVisit()))
            lastContact = DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(memberObject.getLastContactVisit());

        Map<Integer, LocalDate> dateMap = new LinkedHashMap<>();

        // today is the due date for the very first visit
        if (isFirst) {
            dateMap.put(0, startDate);
        }

        dateMap.putAll(ContactUtil.getContactWeeks(isFirst, lastContact, lastMenstrualPeriod));

        return dateMap;
    }

    // vaccine utils
    public static Triple<DateTime, VaccineRepo.Vaccine, String> getIndividualVaccine(VaccineTaskModel vaccineTaskModel, String type) {
        // compute the due date
        Map<String, Object> map = null;
        for (Map<String, Object> mapVac : vaccineTaskModel.getScheduleList()) {
            VaccineRepo.Vaccine myVac = (VaccineRepo.Vaccine) mapVac.get("vaccine");
            String status = (String) mapVac.get("status");
            if (myVac != null && myVac.display().toLowerCase().contains(type.toLowerCase()) && status != null && status.equals("due")) {
                map = mapVac;
                break;
            }
        }

        if (map == null) {
            return null;
        }

        DateTime date = (DateTime) map.get("date");
        VaccineRepo.Vaccine vaccine = (VaccineRepo.Vaccine) map.get("vaccine");
        if (vaccine == null || date == null) {
            return null;
        }
        String vc_count = vaccine.name().substring(vaccine.name().length() - 1);

        return Triple.of(date, vaccine, vc_count);
    }

    public static VaccineTaskModel getWomanVaccine(String baseEntityID, DateTime lmpDate, List<VaccineWrapper> notDoneVaccines) {
        AlertService alertService = ChwApplication.getInstance().getContext().alertService();
        VaccineRepository vaccineRepository = ChwApplication.getInstance().vaccineRepository();

        // get offline alerts
        VaccineSchedule.updateOfflineAlerts(baseEntityID, lmpDate, "woman");
        ChwServiceSchedule.updateOfflineAlerts(baseEntityID, lmpDate, "woman"); // get services

        //
        List<Alert> alerts = alertService.findByEntityIdAndAlertNames(baseEntityID, VaccinateActionUtils.allAlertNames("woman"));
        List<Vaccine> vaccines = vaccineRepository.findByEntityId(baseEntityID);
        Map<String, Date> receivedVaccines = receivedVaccines(vaccines);

        if (notDoneVaccines != null) {
            for (int i = 0; i < notDoneVaccines.size(); i++) {
                receivedVaccines.put(notDoneVaccines.get(i).getName().toLowerCase(), new Date());
            }
        }

        List<Map<String, Object>> sch = generateScheduleList("woman", lmpDate, receivedVaccines, alerts);
        VaccineTaskModel vaccineTaskModel = new VaccineTaskModel();
        vaccineTaskModel.setAlerts(alerts);
        vaccineTaskModel.setVaccines(vaccines);
        vaccineTaskModel.setReceivedVaccines(receivedVaccines);
        vaccineTaskModel.setScheduleList(sch);

        return vaccineTaskModel;
    }
}
