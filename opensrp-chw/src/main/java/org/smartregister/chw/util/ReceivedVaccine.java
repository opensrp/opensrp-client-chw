package org.smartregister.chw.util;

import java.util.Date;

public class ReceivedVaccine {
    private String vaccineCategory;
    private String vaccineName;
    private Date vaccineDate;
    private int vaccineIndex;

    public int getVaccineIndex() {
        return vaccineIndex;
    }

    public void setVaccineIndex(int vaccineIndex) {
        this.vaccineIndex = vaccineIndex;
    }

    public String getVaccineCategory() {
        return vaccineCategory;
    }

    public void setVaccineCategory(String vaccineCategory) {
        this.vaccineCategory = vaccineCategory;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public Date getVaccineDate() {
        return vaccineDate;
    }

    public void setVaccineDate(Date vaccineDate) {
        this.vaccineDate = vaccineDate;
    }

}
