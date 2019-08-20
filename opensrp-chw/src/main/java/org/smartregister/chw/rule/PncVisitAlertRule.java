package org.smartregister.chw.rule;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.chw.contract.RegisterAlert;
import org.smartregister.chw.core.rule.ICommonRule;

import java.util.Date;

public class PncVisitAlertRule implements ICommonRule, RegisterAlert {

    private String visitID;
    private int lastVisitDay = 0;
    private int lastNotVisitDay = 0;
    private int deliveryDiff;
    private int dueDay = -1;
    private int overDueDay = 0;
    private int expiry = -1;

    public PncVisitAlertRule(Date lastVisitDate, Date lastNotVisitDate, Date deliveryDate) {
        if (lastVisitDate != null)
            lastVisitDay = Days.daysBetween(new DateTime(lastVisitDate), new DateTime()).getDays();
        if (lastNotVisitDate != null)
            lastNotVisitDay = Days.daysBetween(new DateTime(lastNotVisitDate), new DateTime()).getDays();
        deliveryDiff = Days.daysBetween(new DateTime(deliveryDate), new DateTime()).getDays();
    }

    public String getVisitID() {
        return visitID;
    }

    public void setVisitID(String visitID) {
        this.visitID = visitID;
    }

    public boolean isValid(int dueDay, int overdueDate, int expiry){
        this.dueDay = dueDay;
        this.overDueDay = overDueDay;
        this.expiry = expiry;

        return (deliveryDiff >= dueDay && deliveryDiff < expiry);
    }

    @Override
    public String getRuleKey() {
        return "pncVisitAlertRule";
    }

    @Override
    public String getButtonStatus() {
        if (lastVisitDay >= dueDay && lastVisitDay <= expiry) {
            return "VISIT_THIS_MONTH";
        } else if (lastNotVisitDay >= dueDay && lastNotVisitDay <= expiry) {
            return "NOT_VISIT_THIS_MONTH";
        } else if (deliveryDiff == dueDay) {
            return "DUE";
        } else if (lastVisitDay < dueDay) {
            return "OVERDUE";
        }
        return "EXPIRY";
    }

    @Override
    public String getNumberOfMonthsDue() {
        return "";
    }

    @Override
    public String getNumberOfDaysDue() {
        return String.valueOf(dueDay);
    }

    @Override
    public String getVisitMonthName() {
        return visitID;
    }
}
