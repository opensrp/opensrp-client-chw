package org.smartregister.chw.rule;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.smartregister.chw.core.rule.HivFollowupRule;
import org.smartregister.chw.core.utils.CoreConstants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CbhsFollowupRule extends HivFollowupRule {
    public static final String RULE_KEY = "hivFollowupRule";
    private String visitID;
    private DateTime hivDate;
    private DateTime dueDate;
    private DateTime overDueDate;
    private DateTime nextVisitDate;
    private DateTime expiryDate;
    private int daysDifference;

    public CbhsFollowupRule(Date hivDate, Date nextVisitDate) {
        super(hivDate, nextVisitDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.hivDate = hivDate != null ? new DateTime(sdf.format(hivDate)) : null;
        this.nextVisitDate = nextVisitDate == null ? null : new DateTime(nextVisitDate);
        isValid();
    }

    public String getVisitID() {
        return visitID;
    }

    public void setVisitID(String visitID) {
        this.visitID = visitID;
    }

    public int getDaysDifference() {
        return daysDifference;
    }

    public boolean isValid() {
        if (nextVisitDate != null) {
            this.dueDate = nextVisitDate.plusDays(0);
            this.overDueDate = nextVisitDate.plusDays(28);
            this.expiryDate = nextVisitDate.plusDays(365);
        } else {
            this.dueDate = hivDate.plusDays(28);
            this.overDueDate = hivDate.plusDays(35);
            this.expiryDate = hivDate.plusDays(365);
        }

        daysDifference = Days.daysBetween(new DateTime(), new DateTime(dueDate)).getDays();
        return true;
    }

    public Date getDueDate() {
        return dueDate != null ? dueDate.toDate() : null;
    }

    public Date getOverDueDate() {
        return overDueDate != null ? overDueDate.toDate() : null;
    }

    public Date getExpiryDate() {
        return expiryDate != null ? expiryDate.toDate() : null;
    }

    @Override
    public String getRuleKey() {
        return "hivFollowupRule";
    }

    @Override
    public String getButtonStatus() {
        DateTime currentDate = new DateTime(new LocalDate().toDate());
        DateTime lastVisit = nextVisitDate;

        if (currentDate.isBefore(expiryDate)) {
            if ((currentDate.isAfter(overDueDate) || currentDate.isEqual(overDueDate)))
                return CoreConstants.VISIT_STATE.OVERDUE;
            if ((currentDate.isAfter(dueDate) || currentDate.isEqual(dueDate)) && currentDate.isBefore(overDueDate))
                return CoreConstants.VISIT_STATE.DUE;
            if (lastVisit != null && currentDate.isEqual(lastVisit))
                return CoreConstants.VISIT_STATE.VISIT_DONE;
            return CoreConstants.VISIT_STATE.NOT_DUE_YET;

        }
        return CoreConstants.VISIT_STATE.EXPIRED;
    }


}
