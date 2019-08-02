package org.smartregister.chw.rule;

import com.opensrp.chw.core.rule.ICommonRule;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.chw.contract.RegisterAlert;

import java.util.Date;

public class PncVisitAlertRule implements ICommonRule, RegisterAlert {

    public String visitID;
    public int lastVisitDay = 0;
    public int lastNotVisitDay = 0;
    public int deliveryDiff;
    public int dueDay = -1;
    public int overDueDay = 0;
    public int periodEnd = -1;

    public PncVisitAlertRule(Date lastVisitDate, Date lastNotVisitDate, Date deliveryDate) {
        if (lastVisitDate != null)
            lastVisitDay = Days.daysBetween(new DateTime(lastVisitDate), new DateTime()).getDays();
        if (lastNotVisitDate != null)
            lastNotVisitDay = Days.daysBetween(new DateTime(lastNotVisitDate), new DateTime()).getDays();
        deliveryDiff = Days.daysBetween(new DateTime(deliveryDate), new DateTime()).getDays();
    }

    @Override
    public String getRuleKey() {
        return "pncVisitAlertRule";
    }

    @Override
    public String getButtonStatus() {
        if (lastVisitDay >= dueDay && lastVisitDay <= periodEnd) {
            return "VISIT_THIS_MONTH";
        } else if (lastNotVisitDay >= dueDay && lastNotVisitDay <= periodEnd) {
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
