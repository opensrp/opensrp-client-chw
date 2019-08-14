package org.smartregister.chw.rule;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.smartregister.chw.util.Constants;

import java.util.Date;

public class PNCHealthFacilityVisitRule implements ICommonRule {

    public static final String RULE_KEY = "visitRule";

    private DateTime deliveryDate;
    private DateTime lastVisitDate;
    private int lastVisitDifference = 0;
    private int overdueDiff = -1;
    private int dueDiff = -1;
    private int expiryDiff = 1000;
    private String visitName;

    public PNCHealthFacilityVisitRule(Date deliveryDate, Date lastVisitDate) {
        if (deliveryDate != null)
            this.deliveryDate = new DateTime(deliveryDate);
        if (lastVisitDate != null)
            this.lastVisitDate = new DateTime(lastVisitDate);

        if (this.lastVisitDate != null && this.deliveryDate != null)
            lastVisitDifference = Days.daysBetween(this.deliveryDate.toLocalDate(), this.lastVisitDate.toLocalDate()).getDays();
    }

    public int getLastVisitDifference() {
        return lastVisitDifference;
    }

    public int getOverdueDiff() {
        return overdueDiff;
    }

    public void setOverdueDiff(int overdueDiff) {
        this.overdueDiff = overdueDiff;
    }

    public int getDueDiff() {
        return dueDiff;
    }

    public void setDueDiff(int dueDiff) {
        this.dueDiff = dueDiff;
    }

    public int getExpiryDiff() {
        return expiryDiff;
    }

    public void setExpiryDiff(int expiryDiff) {
        this.expiryDiff = expiryDiff;
    }

    public String getVisitName() {
        return visitName;
    }

    public void setVisitName(String visitName) {
        this.visitName = visitName;
    }

    public DateTime getOverDueDate() {
        return deliveryDate.plusDays(overdueDiff);
    }

    public DateTime getDueDate() {
        return deliveryDate.plusDays(dueDiff);
    }

    public DateTime getExpiryDate() {
        return deliveryDate.plusDays(expiryDiff);
    }

    public boolean isExpired() {
        return new DateTime(new LocalDate()).isAfter(getExpiryDate());
    }

    public boolean isValidateExpired(int diff) {
        return new DateTime(new LocalDate().toDate()).isAfter(deliveryDate.plusDays(diff));
    }

    public boolean isDue() {
        DateTime today = new DateTime(new LocalDate().toDate());
        return today.isAfter(getDueDate()) && today.isBefore(getOverDueDate());
    }

    public boolean isOverDue() {
        DateTime today = new DateTime(new LocalDate().toDate());
        return today.isAfter(getOverDueDate()) && today.isBefore(getExpiryDate());
    }

    @Override
    public String getRuleKey() {
        return "PNCHealthFacilityVisitRule";
    }

    @Override
    public String getButtonStatus() {
        if (isExpired()) {
            return Constants.VISIT_STATE.EXPIRED;
        } else if (isDue()) {
            return Constants.VISIT_STATE.DUE;
        } else if (isOverDue()) {
            return Constants.VISIT_STATE.OVERDUE;
        }
        return null;
    }
}
