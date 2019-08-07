package org.smartregister.chw.util;

import android.content.Context;

import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.model.VaccineTaskModel;
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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.smartregister.immunization.util.VaccinatorUtils.generateScheduleList;
import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;

public class VaccineScheduleUtil {

    private VaccineScheduleUtil() {

    }

    private static Long getVaccineId(String vaccineName, VaccineTaskModel vaccineTaskModel) {
        for (Vaccine vaccine : vaccineTaskModel.getVaccines()) {
            if (vaccine.getName().equalsIgnoreCase(vaccineName)) {
                return vaccine.getId();
            }
        }
        return null;
    }

    public static VaccineWrapper getVaccineWrapper(VaccineRepo.Vaccine vaccine, VaccineTaskModel vaccineTaskModel) {
        VaccineWrapper vaccineWrapper = new VaccineWrapper();
        vaccineWrapper.setVaccine(vaccine);
        vaccineWrapper.setName(vaccine.display());
        vaccineWrapper.setDbKey(getVaccineId(vaccine.display(), vaccineTaskModel));
        vaccineWrapper.setDefaultName(vaccine.display());
        vaccineWrapper.setAlert(vaccineTaskModel.getAlertsMap().get(vaccine.display()));
        return vaccineWrapper;
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

    /**
     * gets vaccines for the woman
     *
     * @param baseEntityID
     * @param anchorDate
     * @param notDoneVaccines
     * @return
     */
    public static VaccineTaskModel getWomanVaccine(String baseEntityID, DateTime anchorDate, List<VaccineWrapper> notDoneVaccines) {
        return getLocalUpdatedVaccines(baseEntityID, anchorDate, notDoneVaccines, "woman");
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
        return getLocalUpdatedVaccines(baseEntityID, anchorDate, notDoneVaccines, "child");
    }

    /**
     * Updates locals vaccines grouped by type and deducts all those not give
     * Returns a vaccines summary object that can be used for immunization
     *
     * @param baseEntityID
     * @param anchorDate
     * @param inMemoryVaccines (this array contains the vaccines that should be treated as received but yet to be persisted in memory)
     * @param vaccineGroup
     * @return
     */
    public static VaccineTaskModel getLocalUpdatedVaccines(String baseEntityID, DateTime anchorDate, List<VaccineWrapper> inMemoryVaccines, String vaccineGroup) {
        AlertService alertService = ChwApplication.getInstance().getContext().alertService();
        VaccineRepository vaccineRepository = ChwApplication.getInstance().vaccineRepository();

        // updates the local vaccines and services for the mother
        VaccineSchedule.updateOfflineAlerts(baseEntityID, anchorDate, vaccineGroup);
        ChwServiceSchedule.updateOfflineAlerts(baseEntityID, anchorDate, vaccineGroup); // get services

        // retrieve related information from the local db
        List<Alert> alerts = alertService.findByEntityIdAndAlertNames(baseEntityID, VaccinateActionUtils.allAlertNames(vaccineGroup)); // list of all alerts
        List<Vaccine> vaccines = vaccineRepository.findByEntityId(baseEntityID); // add vaccines given to the user
        Map<String, Date> receivedVaccines = receivedVaccines(vaccines); // groups vaccines and date received

        // add vaccines not done to list of processed vaccines (eliminate these vaccines from the next group)
        if (inMemoryVaccines != null) {
            for (int i = 0; i < inMemoryVaccines.size(); i++) {
                receivedVaccines.put(inMemoryVaccines.get(i).getName().toLowerCase(), new Date());
            }
        }

        List<Map<String, Object>> sch = generateScheduleList(vaccineGroup, anchorDate, receivedVaccines, alerts);
        VaccineTaskModel vaccineTaskModel = new VaccineTaskModel();
        vaccineTaskModel.setAlerts(alerts);
        vaccineTaskModel.setVaccines(vaccines);
        vaccineTaskModel.setReceivedVaccines(receivedVaccines);
        vaccineTaskModel.setScheduleList(sch);

        return vaccineTaskModel;
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
        return vaccineType.equals("woman") ?
                VaccinatorUtils.getSupportedWomanVaccines(context) :
                VaccinatorUtils.getSupportedVaccines(context);
    }
}
