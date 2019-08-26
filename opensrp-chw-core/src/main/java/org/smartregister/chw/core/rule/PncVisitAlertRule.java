package org.smartregister.chw.core.rule;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.Date;

public class PncVisitAlertRule implements ICommonRule {

    private String visitID;
    private DateTime lastVisitDate;
    private DateTime deliveryDate;
    private int deliveryDiff;
    private DateTime dueDate;
    private DateTime overDueDate;
    private DateTime expiryDate;

    public PncVisitAlertRule(Date lastVisitDate, Date deliveryDate) {
        this.lastVisitDate = lastVisitDate == null ? null : new DateTime(lastVisitDate);
        this.deliveryDate = new DateTime(deliveryDate);

        deliveryDiff = Days.daysBetween(new DateTime(deliveryDate), new DateTime()).getDays();
    }

    public String getVisitID() {
        return visitID;
    }

    public void setVisitID(String visitID) {
        this.visitID = visitID;
    }

    public boolean isValid(int dueDay, int overdueDate, int expiry) {
        this.dueDate = new DateTime(deliveryDate).plusDays(dueDay);
        this.overDueDate = new DateTime(deliveryDate).plusDays(overdueDate);
        this.expiryDate = new DateTime(deliveryDate).plusDays(expiry);
        return (deliveryDiff >= dueDay && deliveryDiff < expiry);
    }

    @Override
    public String getRuleKey() {
        return "pncVisitAlertRule";
    }

   /* public DateTime getPrevVisitDate() {
        if (lastVisitDate == null)
            return deliveryDate;

        return lastVisitDate;
    }*/

    @Override
    public String getButtonStatus() {
        //DateTime lastVisit = getPrevVisitDate();
        DateTime lastVisit = lastVisitDate;
        DateTime currentDate = new DateTime(new LocalDate().toDate());

        if (lastVisitDate != null) {
            if ((lastVisit.isAfter(dueDate) || lastVisit.isEqual(dueDate)) && lastVisit.isBefore(expiryDate))
                return CoreConstants.VISIT_STATE.VISIT_DONE;
            if (lastVisit.isBefore(dueDate)) {
                if (currentDate.isBefore(overDueDate) && (currentDate.isAfter(dueDate) || currentDate.isEqual(dueDate)))
                    return CoreConstants.VISIT_STATE.DUE;

                if (currentDate.isBefore(expiryDate) && (currentDate.isAfter(overDueDate) || currentDate.isEqual(overDueDate)))
                    return CoreConstants.VISIT_STATE.OVERDUE;
            }
        } else {
            if (currentDate.isBefore(overDueDate) && (currentDate.isAfter(dueDate) || currentDate.isEqual(dueDate)))
                return CoreConstants.VISIT_STATE.DUE;

            if (currentDate.isBefore(expiryDate) && (currentDate.isAfter(overDueDate) || currentDate.isEqual(overDueDate)))
                return CoreConstants.VISIT_STATE.OVERDUE;
        }
        return CoreConstants.VISIT_STATE.EXPIRED;
    }
}