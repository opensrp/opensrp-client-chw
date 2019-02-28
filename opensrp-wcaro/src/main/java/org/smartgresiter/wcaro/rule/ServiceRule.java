package org.smartgresiter.wcaro.rule;

import org.joda.time.LocalDate;
import org.smartgresiter.wcaro.util.ImmunizationState;
import org.smartgresiter.wcaro.util.Utils;
import org.smartregister.util.DateUtil;

//All date formats ISO 8601 yyyy-mm-dd

public class ServiceRule implements ICommonRule {

    public String buttonStatus= ImmunizationState.DUE.name();
    private LocalDate todayDate;
    private LocalDate dueDate;

    public ServiceRule(String dueDay){

        todayDate = new LocalDate();
        dueDate= new LocalDate(dueDay);

    }
    public boolean isOverdue(Integer day) {

        return todayDate.isAfter(dueDate.plusDays(day));

    }


    @Override
    public String getRuleKey() {
        return "serviceAlertRule";
    }

    @Override
    public String getButtonStatus() {
        return buttonStatus;
    }
}
