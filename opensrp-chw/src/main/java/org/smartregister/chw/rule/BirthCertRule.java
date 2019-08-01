package org.smartregister.chw.rule;

import com.opensrp.chw.core.rule.ICommonRule;

import org.joda.time.LocalDate;
import org.smartregister.chw.util.Utils;
import org.smartregister.util.DateUtil;

//All date formats ISO 8601 yyyy-mm-dd

public class BirthCertRule implements ICommonRule {

    public String buttonStatus = "";
    private LocalDate todayDate;
    private LocalDate birthDay;

    public BirthCertRule(String dateOfBirth) {

        todayDate = new LocalDate();
        birthDay = new LocalDate(Utils.dobStringToDate(dateOfBirth));

    }

    public boolean isOverdue(Integer month) {


        return todayDate.isAfter(birthDay.plusMonths(month));

    }

    public boolean isDue(Integer month) {


        return todayDate.isBefore(birthDay.plusMonths(month));

    }

    public boolean isExpire(Integer month) {


        return todayDate.isAfter(birthDay.plusMonths(month));

    }

    private String getFormateDate(String dateOfBirth) {
        return DateUtil.formatDate(dateOfBirth);
    }

    @Override
    public String getRuleKey() {
        return "birthCertAlertRule";
    }

    @Override
    public String getButtonStatus() {
        return buttonStatus;
    }
}
