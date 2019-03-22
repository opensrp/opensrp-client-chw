package org.smartregister.chw.util;

public class VaccineHeader implements BaseVaccine {
    private String vaccineHeaderName;

    public String getVaccineHeaderName() {
        return vaccineHeaderName;
    }

    public void setVaccineHeaderName(String vaccineHeaderName) {
        this.vaccineHeaderName = vaccineHeaderName;
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }
}
