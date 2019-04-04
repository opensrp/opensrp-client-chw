package org.smartregister.chw.model;

import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.Vaccine;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class VaccineTaskModel {
    private List<Alert> alerts;
    private List<Vaccine> vaccines;
    private Map<String, Date> receivedVaccines;
    private List<Map<String, Object>> scheduleList;

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
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



}
