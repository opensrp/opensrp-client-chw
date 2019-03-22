package org.smartregister.chw.util;

public class VaccineContent implements BaseVaccine {
    private String vaccineName;
    private String vaccineDate;

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public String getVaccineDate() {
        return vaccineDate;
    }

    public void setVaccineDate(String vaccineDate) {
        this.vaccineDate = vaccineDate;
    }

    @Override
    public int getType() {
        return TYPE_CONTENT;
    }
}
