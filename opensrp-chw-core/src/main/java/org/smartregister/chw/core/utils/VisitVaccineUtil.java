package org.smartregister.chw.core.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.smartregister.chw.anc.domain.VaccineDisplay;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.dao.AbstractDao;
import org.smartregister.chw.core.dao.VisitDao;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineCondition;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.VaccineTrigger;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.domain.jsonmapping.Condition;
import org.smartregister.immunization.domain.jsonmapping.Due;
import org.smartregister.immunization.domain.jsonmapping.Expiry;
import org.smartregister.immunization.domain.jsonmapping.Schedule;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinatorUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class VisitVaccineUtil {

    private static HashMap<String, HashMap<String, VaccineSchedule>> vaccineSchedules;

    private static Map<String, VaccineRepo.Vaccine> vaccineMap;

    public static Map<String, VaccineRepo.Vaccine> getAllVaccines() {
        if (vaccineMap == null || vaccineMap.size() == 0) {
            vaccineMap = new HashMap<>();

            List<VaccineRepo.Vaccine> allVacs = VaccineRepo.getVaccines("woman");
            allVacs.addAll(VaccineRepo.getVaccines("child"));

            for (VaccineRepo.Vaccine vaccine : allVacs) {
                vaccineMap.put(
                        vaccine.display().toLowerCase().replace(" ", "").replace("_", ""),
                        vaccine
                );
            }
        }
        return vaccineMap;
    }

    public static List<Alert> getNextVaccines(String baseEntityID, DateTime anchorDate, String category, boolean includePending) {

        /// compute the alerts
        HashMap<String, HashMap<String, VaccineSchedule>> vaccineSchedules = getVaccineSchedules(category);

        List<Vaccine> vaccines = new ArrayList<>();
        List<Alert> alerts = VisitVaccineUtil.getInMemoryAlerts(vaccineSchedules, baseEntityID, anchorDate, category, vaccines);

        /// prepare the given vaccines map
        Map<String, Date> issuedVaccines = getIssuedVaccines(baseEntityID, includePending);

        List<Alert> pending = new ArrayList<>();
        for (Alert alert : alerts) {
            String code = alert.scheduleName().toLowerCase().replace(" ", "");
            Date dateIssued = issuedVaccines.get(code);

            if (dateIssued == null) {
                pending.add(alert);
            }
        }

        return pending;
    }

    private static HashMap<String, HashMap<String, VaccineSchedule>> getVaccineSchedules(String category) {
        List<VaccineGroup> vaccineGroups =
                VaccineScheduleUtil.getVaccineGroups(CoreChwApplication.getInstance().getApplicationContext(), category);

        List<org.smartregister.immunization.domain.jsonmapping.Vaccine> specialVaccines =
                VaccinatorUtils.getSpecialVaccines(CoreChwApplication.getInstance().getApplicationContext());

        return getSchedule(vaccineGroups, specialVaccines, category);
    }

    public static Map<String, Date> getIssuedVaccines(String baseEntityID, boolean includePending) {
        Map<String, Date> vaccines = new LinkedHashMap<>();
        VaccineRepository vaccineRepository = CoreChwApplication.getInstance().vaccineRepository();
        List<Vaccine> savedVaccines = vaccineRepository.findByEntityId(baseEntityID);
        if (savedVaccines != null && !savedVaccines.isEmpty()) {
            for (Vaccine vaccine : savedVaccines) {
                vaccines.put(vaccine.getName().replace("_", "").replace(" ", ""), vaccine.getDate());
            }
        }

        if (includePending) {
            Map<String, Date> vac = VisitDao.getUnprocessedVaccines(baseEntityID);
            for(Map.Entry<String,Date> entry: vac.entrySet()){
                vaccines.put(entry.getKey().replace("_", "").replace(" ", ""), entry.getValue());
            }
        }

        return vaccines;
    }

    public static String getAlertIteration(Alert alert) {
        return alert.scheduleName().substring(alert.scheduleName().length() - 1);
    }

    public static Map<String, List<Alert>> groupByType(List<Alert> alerts) {
        Map<String, List<Alert>> map = new LinkedHashMap<>();

        for (Alert alert : alerts) {
            String type = alert.scheduleName().substring(0, alert.scheduleName().length() - 1).trim();

            List<Alert> all = map.get(type);
            if (all == null) {
                all = new ArrayList<>();
            }
            all.add(alert);

            map.put(type, all);
        }

        return map;
    }

    /**
     * Returns an ordered map of the vaccine title and the list of vaccines to be displayed.
     *
     * @return
     */
    public static Map<VaccineGroup, List<Pair<VaccineRepo.Vaccine, Alert>>> generateVisitVaccines(
            String baseEntityID,
            Map<String, VaccineRepo.Vaccine> vaccinesRepo,
            DateTime dob,
            List<VaccineGroup> vaccineGroups,
            List<org.smartregister.immunization.domain.jsonmapping.Vaccine> specialVaccines,
            List<Vaccine> issuedVaccines,
            Map<String, List<VisitDetail>> edit_details
    ) {

        // prepare tools

        /// compute the alerts
        HashMap<String, HashMap<String, VaccineSchedule>> vaccineSchedules =
                getSchedule(vaccineGroups, specialVaccines, CoreConstants.SERVICE_GROUPS.CHILD);

        List<Alert> alerts =
                getInMemoryAlerts(vaccineSchedules, baseEntityID, dob, CoreConstants.SERVICE_GROUPS.CHILD, issuedVaccines);

        Map<String, Alert> alertMap = new HashMap<>();
        for (Alert alert : alerts) {
            alertMap.put(alert.scheduleName().toLowerCase().replace(" ", ""), alert);
        }

        /// prepare the given vaccines map
        Map<String, Vaccine> givenVaccines = new HashMap<>();
        for (Vaccine vaccine : issuedVaccines) {
            givenVaccines.put(vaccine.getName().replace("_", "").replace(" ", ""), vaccine);
        }


        // compute screen results
        Map<VaccineGroup, List<Pair<VaccineRepo.Vaccine, Alert>>> res = new LinkedHashMap<>();

        LocalDate today = new LocalDate();

        // add the vaccines to the final group
        for (VaccineGroup group : vaccineGroups) {

            List<Pair<VaccineRepo.Vaccine, Alert>> pairList = new ArrayList<>();
            for (org.smartregister.immunization.domain.jsonmapping.Vaccine jsonVaccine : group.vaccines) {
                String code = jsonVaccine.name.toLowerCase().replace(" ", "");

                Alert alert = alertMap.get(code);
                Vaccine vaccine = givenVaccines.get(code);
                VaccineRepo.Vaccine repoVac = vaccinesRepo.get(code);
                String date = repoVac != null && edit_details != null ? NCUtils.getText(edit_details.get(NCUtils.removeSpaces(repoVac.display()))).trim() : "";
                // get all vaccine that are yet to expire
                // and are active
                if (StringUtils.isNotBlank(date) ||
                        (alert != null
                                && vaccine == null
                                && repoVac != null
                                && today.isAfter(new LocalDate(alert.startDate()).plusDays(-1))
                        )
                    //&& (StringUtils.isBlank(alert.expiryDate()) || new LocalDate(alert.expiryDate()).isAfter(today))) // allow expired vaccines to be entered
                ) {
                    // in edit mode alerts may be null. create a default alert with the start and end date to be today
                    alert = getVisitPseudoAlert(alert, date, repoVac);
                    pairList.add(Pair.create(repoVac, alert));
                }
            }

            if (pairList.size() > 0 || res.size() > 0) {
                res.put(group, pairList);
            }
        }

        return res;
    }

    public static HashMap<String, HashMap<String, VaccineSchedule>> getSchedule(
            List<VaccineGroup> vaccines,
            List<org.smartregister.immunization.domain.jsonmapping.Vaccine> specialVaccines,
            String vaccineCategory
    ) {
        if (vaccineSchedules == null) {
            vaccineSchedules = new HashMap<>();
        }
        vaccineSchedules.put(vaccineCategory, new HashMap<>());

        for (VaccineGroup vaccineGroup : vaccines) {
            for (org.smartregister.immunization.domain.jsonmapping.Vaccine vaccine : vaccineGroup.vaccines) {
                initVaccine(vaccineCategory, vaccine);
            }
        }

        if (specialVaccines != null) {
            for (org.smartregister.immunization.domain.jsonmapping.Vaccine vaccine : specialVaccines) {
                initVaccine(vaccineCategory, vaccine);
            }
        }

        return vaccineSchedules;
    }

    public static List<Alert> getInMemoryAlerts(
            HashMap<String, HashMap<String, VaccineSchedule>> vaccineSchedules,
            String baseEntityId,
            DateTime dob,
            String vaccineCategory,
            List<Vaccine> issuedVaccines
    ) {
        List<Alert> generatedAlerts = new ArrayList<>();
        try {
            if (vaccineSchedules != null && vaccineSchedules.containsKey(vaccineCategory)) {
                for (VaccineSchedule curSchedule : vaccineSchedules.get(vaccineCategory).values()) {
                    Alert curAlert = curSchedule.getOfflineAlert(baseEntityId, dob.toDate(), issuedVaccines);
                    if (curAlert != null) {
                        generatedAlerts.add(curAlert);
                    }
                }
            }
            return generatedAlerts;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private static Alert getVisitPseudoAlert(Alert alert, String date, VaccineRepo.Vaccine repoVac) {
        if (alert != null) {
            return alert;
        }

        String startDate = AbstractDao.getDobDateFormat().format(new Date());
        String endDate = AbstractDao.getDobDateFormat().format(new Date());
        if (!StringUtils.isBlank(date) && !Constants.HOME_VISIT.VACCINE_NOT_GIVEN.equalsIgnoreCase(date)) {
            try {
                startDate = date;
                endDate = date;
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        String code = repoVac.name().toLowerCase().replace(" ", "");
        return new Alert("", repoVac.display(), code, AlertStatus.complete, startDate, endDate);
    }

    private static void initVaccine(String vaccineCategory,
                                    org.smartregister.immunization.domain.jsonmapping.Vaccine curVaccine) {
        if (TextUtils.isEmpty(curVaccine.vaccine_separator)) {
            String vaccineName = curVaccine.name;
            VaccineSchedule vaccineSchedule;
            if (curVaccine.schedule != null) {
                vaccineSchedule = getVaccineSchedule(vaccineName, vaccineCategory, curVaccine.schedule);
                vaccineSchedules.get(vaccineCategory).put(vaccineName.toUpperCase(), vaccineSchedule);
            }
        } else {
            String[] splitNames = curVaccine.name
                    .split(curVaccine.vaccine_separator);
            for (int nameIndex = 0; nameIndex < splitNames.length; nameIndex++) {
                String vaccineName = splitNames[nameIndex];
                VaccineSchedule vaccineSchedule = getVaccineSchedule(vaccineName, vaccineCategory, curVaccine.schedules.get(vaccineName));
                if (vaccineSchedule != null) {
                    vaccineSchedules.get(vaccineCategory).put(vaccineName.toUpperCase(), vaccineSchedule);
                }
            }
        }
    }

    private static VaccineSchedule getVaccineSchedule(String vaccineName, String vaccineCategory, Schedule schedule) {
        ArrayList<VaccineTrigger> dueTriggers = new ArrayList<>();
        for (Due due : schedule.due) {
            VaccineTrigger curTrigger = VaccineTrigger.init(vaccineCategory, due);
            if (curTrigger != null) {
                dueTriggers.add(curTrigger);
            }
        }

        ArrayList<VaccineTrigger> expiryTriggers = new ArrayList<>();
        if (schedule.expiry != null) {
            for (Expiry expiry : schedule.expiry) {
                VaccineTrigger curTrigger = VaccineTrigger.init(expiry);
                if (curTrigger != null) {
                    expiryTriggers.add(curTrigger);
                }
            }
        }

        VaccineRepo.Vaccine vaccine = VaccineRepo.getVaccine(vaccineName, vaccineCategory);
        if (vaccine != null) {
            ArrayList<VaccineCondition> conditions = new ArrayList<>();
            if (schedule.conditions != null) {
                for (Condition condition : schedule.conditions) {
                    VaccineCondition curCondition = VaccineCondition.init(vaccineCategory,
                            condition);
                    if (curCondition != null) {
                        conditions.add(curCondition);
                    }
                }
            }

            return new VaccineSchedule(dueTriggers, expiryTriggers, vaccine, conditions);
        }

        return null;
    }

    /**
     * generate wrapper objects
     *
     * @param pairs
     * @return
     */
    public static List<VaccineWrapper> wrapVaccines(List<Pair<VaccineRepo.Vaccine, Alert>> pairs) {
        List<VaccineWrapper> vaccineWrappers = new ArrayList<>();
        for (Pair<VaccineRepo.Vaccine, Alert> pair : pairs) {
            vaccineWrappers.add(wrapVaccine(pair.first, pair.second));
        }
        return vaccineWrappers;
    }

    /**
     * generate wrapper object
     *
     * @param vaccine, alert
     * @return
     */
    public static VaccineWrapper wrapVaccine(VaccineRepo.Vaccine vaccine, Alert alert) {
        VaccineWrapper vaccineWrapper = new VaccineWrapper();
        vaccineWrapper.setVaccine(vaccine);
        vaccineWrapper.setName(vaccine.display());
        vaccineWrapper.setDefaultName(vaccine.display());
        vaccineWrapper.setAlert(alert);
        return vaccineWrapper;
    }

    /**
     * convert wrappers to displa objects
     *
     * @param wrappers
     * @return
     */
    public static List<VaccineDisplay> toDisplays(List<VaccineWrapper> wrappers) {
        List<VaccineDisplay> displays = new ArrayList<>();
        for (VaccineWrapper vaccineWrapper : wrappers) {
            Alert alert = vaccineWrapper.getAlert();
            VaccineDisplay display = new VaccineDisplay();
            display.setVaccineWrapper(vaccineWrapper);
            display.setStartDate(new LocalDate(alert.startDate()).toDate());
            display.setEndDate(alert.expiryDate() != null ? new LocalDate(alert.expiryDate()).toDate() : new Date());
            display.setValid(false);
            displays.add(display);
        }

        return displays;
    }

    public static String getVaccineTitle(String name, Context context) {
        if ("Birth".equals(name)) {
            return context.getString(R.string.at_birth);
        }

        return name.replace("Weeks", context.getString(org.smartregister.chw.core.R.string.abbrv_weeks))
                .replace("Months", context.getString(org.smartregister.chw.core.R.string.abbrv_months))
                .replace(" ", "");
    }
}
