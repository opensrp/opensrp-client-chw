package org.smartregister.chw.core.utils;

import android.content.Context;

import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.dao.AlertDao;
import org.smartregister.chw.core.dao.VisitDao;
import org.smartregister.chw.core.model.VaccineTaskModel;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.service.AlertService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VaccineScheduleUtil {

    private VaccineScheduleUtil() {

    }

    /**
     * gets vaccines for the woman
     *
     * @param baseEntityID
     * @param anchorDate
     * @param notDoneVaccines
     * @return
     */
    public static VaccineTaskModel getWomanVaccine(String baseEntityID, DateTime anchorDate, List<VaccineWrapper> notDoneVaccines) {
        return getLocalUpdatedVaccines(baseEntityID, anchorDate, notDoneVaccines, CoreConstants.SERVICE_GROUPS.WOMAN);
    }

    /**
     * Updates locals vaccines grouped by type and deducts all those not given
     * Returns a vaccines summary object that can be used for immunization
     *
     * @param baseEntityID
     * @param anchorDate
     * @param inMemoryVaccines (this array contains the vaccines that should be treated as received but yet to be persisted in memory)
     * @param vaccineGroupName
     * @return
     */
    public static VaccineTaskModel getLocalUpdatedVaccines(String baseEntityID, DateTime anchorDate, List<VaccineWrapper> inMemoryVaccines, String vaccineGroupName) {
        AlertService alertService = CoreChwApplication.getInstance().getContext().alertService();
        VaccineRepository vaccineRepository = CoreChwApplication.getInstance().vaccineRepository();

        // updates the local vaccines and services for the mother
        VaccineScheduleUtil.updateOfflineAlerts(baseEntityID, anchorDate, vaccineGroupName);
        ChwServiceSchedule.updateOfflineAlerts(baseEntityID, anchorDate, vaccineGroupName); // get services

        // retrieve related information from the local db
        List<Alert> alerts = alertService.findByEntityIdAndAlertNames(baseEntityID, VaccinateActionUtils.allAlertNames(vaccineGroupName)); // list of all alerts
        List<Vaccine> vaccines = vaccineRepository.findByEntityId(baseEntityID); // add vaccines given to the user
        Map<String, Date> receivedVaccines = VaccinatorUtils.receivedVaccines(vaccines); // groups vaccines and date received

        // add vaccines not done to list of processed vaccines (eliminate these vaccines from the next group)
        if (inMemoryVaccines != null) {
            for (int i = 0; i < inMemoryVaccines.size(); i++) {
                receivedVaccines.put(inMemoryVaccines.get(i).getName().toLowerCase(), new Date());
            }
        }

        List<Map<String, Object>> sch = VaccinatorUtils.generateScheduleList(vaccineGroupName, anchorDate, receivedVaccines, alerts);

        VaccineTaskModel vaccineTaskModel = new VaccineTaskModel();
        vaccineTaskModel.setVaccineGroupName(vaccineGroupName);
        vaccineTaskModel.setAnchorDate(anchorDate);
        vaccineTaskModel.setAlerts(alerts);
        vaccineTaskModel.setVaccines(vaccines);
        vaccineTaskModel.setReceivedVaccines(receivedVaccines);
        vaccineTaskModel.setScheduleList(sch);

        return vaccineTaskModel;
    }

    public static void updateOfflineAlerts(String baseEntityID, DateTime anchorDate, String vaccineGroupName) {
        // recompute offline alerts
        VaccineSchedule.updateOfflineAlerts(baseEntityID, anchorDate, vaccineGroupName);
        // delete all vaccine alerts that have been administered
        AlertDao.updateOfflineVaccineAlerts(baseEntityID);
    }

    public static List<VaccineWrapper> recomputeSchedule(
            HashMap<String, HashMap<String, VaccineSchedule>> vaccineSchedules,
            DateTime anchorDate,
            VaccineGroup vaccineGroup,
            Map<String, Date> receivedVacs
    ) {
        List<VaccineWrapper> vaccineWrappers = new ArrayList<>();

        List<Vaccine> vaccines = new ArrayList<>();
        for (Map.Entry<String, Date> entry : receivedVacs.entrySet()) {
            Vaccine vaccine = new Vaccine();
            vaccine.setName(entry.getKey());
            vaccine.setDate(entry.getValue());
            vaccines.add(vaccine);
        }

        // get new alerts
        List<Alert> alerts = VisitVaccineUtil.getInMemoryAlerts(vaccineSchedules, "", anchorDate, CoreConstants.SERVICE_GROUPS.CHILD, vaccines);

        Map<String, Alert> alertMap = new HashMap<>();
        for (Alert alert : alerts) {
            alertMap.put(alert.scheduleName().toLowerCase().replace(" ", ""), alert);
        }

        // get all the vaccines
        for (org.smartregister.immunization.domain.jsonmapping.Vaccine domain_vaccine : vaccineGroup.vaccines) {
            Alert alert = alertMap.get(domain_vaccine.getName().toLowerCase().replace(" ", ""));
            if (alert != null && new DateTime(new LocalDate(alert.startDate()).toDate()).isBefore(new DateTime())) {
                VaccineRepo.Vaccine vaccine = VaccineRepo.getVaccine(domain_vaccine.getName(), CoreConstants.SERVICE_GROUPS.CHILD);
                VaccineWrapper vaccineWrapper = new VaccineWrapper();
                vaccineWrapper.setVaccine(vaccine);
                vaccineWrapper.setName(vaccine.display());
                vaccineWrapper.setDefaultName(vaccine.display());
                vaccineWrapper.setAlert(alert);

                vaccineWrappers.add(vaccineWrapper);
            }
        }

        return vaccineWrappers;
    }

    /**
     * gets vaccines for the child
     *
     * @param baseEntityID
     * @param anchorDate
     * @param notDoneVaccines
     * @return
     */
    public static VaccineTaskModel getChildVaccine(String baseEntityID, DateTime anchorDate, List<VaccineWrapper> notDoneVaccines) {
        return getLocalUpdatedVaccines(baseEntityID, anchorDate, notDoneVaccines, CoreConstants.SERVICE_GROUPS.CHILD);
    }

    /**
     * Returns the supported type of vaccines. for either woman or child.
     * The function loads child vaccines by default when the vaccine type is not provided
     *
     * @param context
     * @param vaccineType
     * @return
     */
    public static Map<String, VaccineGroup> getVaccineNamedGroups(Context context, String vaccineType) {
        Map<String, VaccineGroup> groupedVaccines = new LinkedHashMap<>();

        for (VaccineGroup vg : getVaccineGroups(context, vaccineType)) {
            groupedVaccines.put(vg.name, vg);
        }

        return groupedVaccines;
    }

    /**
     * Returns the supported type of vaccines. for either woman or child.
     * The function loads child vaccines by default when the vaccine type is not provided
     *
     * @param context
     * @param vaccineType
     * @return
     */
    public static List<VaccineGroup> getVaccineGroups(Context context, String vaccineType) {
        return CoreConstants.SERVICE_GROUPS.WOMAN.equals(vaccineType) ?
                VaccinatorUtils.getSupportedWomanVaccines(context) :
                VaccinatorUtils.getSupportedVaccines(context);
    }

    /**
     * returns list of vaccines that are pending
     *
     * @param baseEntityID
     * @param dob
     * @param group
     * @return
     */
    public static List<VaccineWrapper> getChildDueVaccines(String baseEntityID, Date dob, int group) {
        List<Alert> alerts = VisitVaccineUtil.getNextVaccines(baseEntityID, new DateTime(dob), CoreConstants.SERVICE_GROUPS.CHILD, false);
        List<VaccineWrapper> wrappers = new ArrayList<>();
        List<VaccineGroup> vaccineGroups = VaccineScheduleUtil.getVaccineGroups(CoreChwApplication.getInstance().getApplicationContext(), CoreConstants.SERVICE_GROUPS.CHILD);
        VaccineGroup vaccineGroup = vaccineGroups.get(group);

        if (alerts != null && alerts.size() > 0) {
            Map<String, VaccineRepo.Vaccine> vaccineMap = VisitVaccineUtil.getAllVaccines();
            LocalDate today = new LocalDate();
            for (org.smartregister.immunization.domain.jsonmapping.Vaccine jsonVaccine : vaccineGroup.vaccines) {
                String code = jsonVaccine.name.toLowerCase().replace(" ", "");
                for (Alert alert : alerts) {
                    if (
                            today.isAfter(new LocalDate(alert.startDate()).plusDays(-1)) &&
                            alert.scheduleName().toLowerCase().replace(" ", "").replace("_", "").equals(code)
                    ) {
                        String vaccine_key = alert.visitCode().toLowerCase().replace(" ", "").replace("_", "");
                        VaccineWrapper vaccineWrapper = new VaccineWrapper();
                        vaccineWrapper.setVaccine(vaccineMap.get(vaccine_key));
                        vaccineWrapper.setName(alert.visitCode());
                        vaccineWrapper.setDefaultName(alert.visitCode());
                        vaccineWrapper.setAlert(alert);
                        wrappers.add(vaccineWrapper);
                    }
                }
            }
        }
        return wrappers;
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

    public static Map<String, Date> getReceivedVaccines(String baseEntityID) {
        List<Vaccine> vaccines = CoreChwApplication.getInstance().vaccineRepository().findByEntityId(baseEntityID);

        Map<String, Date> receivedVaccines = VaccinatorUtils.receivedVaccines(vaccines);
        receivedVaccines.putAll(VisitDao.getUnprocessedVaccines(baseEntityID));

        return receivedVaccines;
    }
}