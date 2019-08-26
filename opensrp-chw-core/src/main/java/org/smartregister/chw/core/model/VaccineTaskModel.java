package org.smartregister.chw.core.model;

import org.joda.time.DateTime;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VaccineTaskModel {
    private String vaccineGroupName;
    private VaccineGroup groupMap;
    private DateTime anchorDate;
    private List<Alert> alerts;
    private Map<String, Alert> alertsMap = null;
    private List<Vaccine> vaccines;
    private Map<String, Date> receivedVaccines;
    private List<Map<String, Object>> scheduleList;
    private ArrayList<VaccineWrapper> notGivenVaccine = new ArrayList<>();

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    // lazy intialization
    public Map<String, Alert> getAlertsMap() {
        if (alertsMap == null) {
            alertsMap = new HashMap<>();
        }
        if (alerts != null && alerts.size() > 0) {
            for (Alert alert : alerts) {
                alertsMap.put(alert.scheduleName(), alert);
            }
        }
        return alertsMap;
    }

    public String getVaccineGroupName() {
        return vaccineGroupName;
    }

    public void setVaccineGroupName(String vaccineGroupName) {
        this.vaccineGroupName = vaccineGroupName;
    }

    public VaccineGroup getGroupMap() {
        return groupMap;
    }

    public void setGroupMap(VaccineGroup groupMap) {
        this.groupMap = groupMap;
    }

    public DateTime getAnchorDate() {
        return anchorDate;
    }

    public void setAnchorDate(DateTime anchorDate) {
        this.anchorDate = anchorDate;
    }

    public List<Vaccine> getVaccines() {
        return vaccines;
    }

    public void setVaccines(List<Vaccine> vaccines) {
        this.vaccines = vaccines;
    }

    public Map<String, Date> getReceivedVaccines() {
        return receivedVaccines;
    }

    public void setReceivedVaccines(Map<String, Date> receivedVaccines) {
        this.receivedVaccines = receivedVaccines;
    }

    public List<Map<String, Object>> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<Map<String, Object>> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public ArrayList<VaccineWrapper> getNotGivenVaccine() {
        return notGivenVaccine;
    }

    public void setNotGivenVaccine(ArrayList<VaccineWrapper> notGivenVaccine) {
        this.notGivenVaccine = notGivenVaccine;
    }
}
