package org.smartregister.chw.core.rule;

import org.joda.time.LocalDate;
import org.smartregister.chw.core.utils.Utils;

//All date formats ISO 8601 yyyy-mm-dd

public class ImmunizationExpiredRule implements ICommonRule {

    private final String OPV0 = "opv0";
    private final String MCV2 = "mcv2";
    public boolean isExpired = false;
    private LocalDate todayDate;
    private LocalDate birthDate;
    private String vaccineName;

    public ImmunizationExpiredRule(String dateOfBirth, String vaccineName) {

        todayDate = new LocalDate();
        birthDate = new LocalDate(Utils.dobStringToDate(dateOfBirth));
        this.vaccineName = vaccineName.replace(" ", "");

    }

    public boolean isOpv0Expired(Integer day) {
        return vaccineName.equalsIgnoreCase(OPV0) && todayDate.isAfter(birthDate.plusDays(day));
    }

    public boolean isMcv2Expired(Integer month) {
        return vaccineName.equalsIgnoreCase(MCV2) && todayDate.isAfter(birthDate.plusMonths(month));
    }

    public boolean isAllVaccineExpired(Integer month) {
        return todayDate.isAfter(birthDate.plusMonths(month));
    }


    @Override
    public String getRuleKey() {
        return "immunizationExpireRule";
    }

    @Override
    public String getButtonStatus() {
        return isExpired + "";
    }

}
