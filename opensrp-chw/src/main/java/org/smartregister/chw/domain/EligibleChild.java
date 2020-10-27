package org.smartregister.chw.domain;

import org.smartregister.chw.contract.ListContract;
import org.smartregister.domain.Alert;

import java.util.Date;
import java.util.List;

public class EligibleChild implements ListContract.Identifiable {

    private String ID;
    private String fullName;
    private Date dateOfBirth;
    private String familyName;
    private String[] dueVaccines;
    private List<Alert> alerts;
    private String locationId;

    @Override
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String[] getDueVaccines() {
        return dueVaccines;
    }

    public void setDueVaccines(String[] dueVaccines) {
        this.dueVaccines = dueVaccines;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}
