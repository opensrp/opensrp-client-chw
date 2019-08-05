package com.opensrp.chw.core.rule;

import com.opensrp.chw.core.enums.ImmunizationState;

import org.joda.time.LocalDate;

//All date formats ISO 8601 yyyy-mm-dd

public class ServiceRule implements ICommonRule {

    public String buttonStatus = ImmunizationState.DUE.name();
    private LocalDate todayDate;
    private LocalDate dueDate;

    public ServiceRule(String dueDay) {

        todayDate = new LocalDate();
        dueDate = new LocalDate(dueDay);

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
