package org.smartgresiter.wcaro.util;

import java.util.Date;

public class ReceivedVaccine {
    String vaccineCategory;
    String vaccineName;
    Date vaccineDate;
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
