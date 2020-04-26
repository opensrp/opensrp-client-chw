package org.smartregister.chw.rules;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.smartregister.chw.core.rule.ICommonRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fp_pathfinder.util.FamilyPlanningConstants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FpAlertRule implements ICommonRule {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private String visitID;
    private DateTime fpDate;
    private DateTime dueDate;
    private DateTime overDueDate;
    private DateTime lastVisitDate;
    private DateTime expiryDate;
    private int fpDifference;
    private Integer pillCycles;
    private String fpMethod;

    public FpAlertRule(Date fpDate, Date lastVisitDate, Integer pillCycles, String fpMethod) {
        this.pillCycles = pillCycles == null ? 0 : pillCycles;
        this.fpDate = fpDate != null ? new DateTime(sdf.format(fpDate)) : null;
        this.lastVisitDate = lastVisitDate == null ? null : new DateTime(lastVisitDate);
        this.fpMethod = fpMethod;
        fpDifference = Days.daysBetween(new DateTime(fpDate), new DateTime()).getDays();
    }

    public String getVisitID() {
        return visitID;
    }

    public void setVisitID(String visitID) {
        this.visitID = visitID;
    }

    public boolean isCocPopValid(int dueDay, int overdueDate) {
        if (lastVisitDate != null) {
            this.dueDate = (new DateTime(this.lastVisitDate)).plusDays(this.pillCycles * 28).minusDays(dueDay);
            this.overDueDate = (new DateTime(this.lastVisitDate)).plusDays(this.pillCycles * 28).minusDays(overdueDate);
        } else {
            this.dueDate = (new DateTime(this.fpDate)).plusDays(this.pillCycles * 28).minusDays(dueDay);
            this.overDueDate = (new DateTime(this.fpDate)).plusDays(this.pillCycles * 28).minusDays(overdueDate);
        }
        return true;
    }

    public boolean isCondomValid(int dueDay, int overdueDate) {
        if (lastVisitDate != null) {
            int monthOfYear = new DateTime(lastVisitDate).getMonthOfYear();
            int year = new DateTime(lastVisitDate).getYear();
            if ((monthOfYear == DateTime.now().getMonthOfYear()) && (year == DateTime.now().getYear())) {
                this.dueDate = new DateTime().plusMonths(1).withDayOfMonth(dueDay);
                this.overDueDate = new DateTime().plusMonths(1).withDayOfMonth(overdueDate);
            } else {
                if ((year == DateTime.now().getYear()) && ((DateTime.now().getMonthOfYear()) - (monthOfYear) == 1)) {
                    this.dueDate = new DateTime().withDayOfMonth(dueDay);
                    this.overDueDate = new DateTime().withDayOfMonth(overdueDate);
                } else {
                    this.dueDate = lastVisitDate.withDayOfMonth(dueDay).plusMonths(1);
                    this.overDueDate = lastVisitDate.withDayOfMonth(overdueDate).plusMonths(1);
                }
            }
        } else {
            this.dueDate = fpDate.plusMonths(1).withDayOfMonth(dueDay);
            this.overDueDate = fpDate.plusMonths(1).withDayOfMonth(overdueDate);
        }
        return true;
    }

    public boolean isInjectionValid(int dueDay, int overdueDate) {
        if (lastVisitDate != null) {
            this.dueDate = new DateTime(lastVisitDate).plusDays(dueDay);
            this.overDueDate = new DateTime(lastVisitDate).plusDays(overdueDate);
        } else {
            this.dueDate = new DateTime(fpDate).plusDays(dueDay);
            this.overDueDate = new DateTime(fpDate).plusDays(overdueDate);
        }

        return true;
    }

    public boolean isFemaleSterilizationFollowUpOneValid(int dueDay, int overdueDate, int expiry) {
        if (fpDifference >= dueDay && fpDifference < expiry) {
            this.dueDate = new DateTime(fpDate).plusDays(dueDay);
            this.overDueDate = new DateTime(fpDate).plusDays(overdueDate);
            this.expiryDate = new DateTime(fpDate).plusDays(expiry);
            return true;
        }
        return false;
    }

    public boolean isFemaleSterilizationFollowUpTwoValid(int dueDay, int overdueDate, int expiry) {
        int expiryDiff = Days.daysBetween(new DateTime(fpDate), new DateTime(fpDate).plusMonths(expiry)).getDays();
        if (fpDifference >= dueDay && fpDifference < expiryDiff) {
            this.dueDate = new DateTime(fpDate).plusDays(dueDay);
            this.overDueDate = new DateTime(fpDate).plusDays(overdueDate);
            this.expiryDate = new DateTime(fpDate).plusMonths(expiry);
            return true;
        }
        return false;
    }

    public boolean isFemaleSterilizationFollowUpThreeValid(int dueDay, int overdueDate, int expiry) {
        int dueDiff = Days.daysBetween(new DateTime(fpDate), new DateTime(fpDate).plusMonths(dueDay)).getDays();
        int expiryDiff = Days.daysBetween(new DateTime(fpDate), new DateTime(fpDate).plusMonths(expiry)).getDays();
        if (fpDifference >= dueDiff && fpDifference < expiryDiff) {
            this.dueDate = new DateTime(fpDate).plusMonths(dueDay);
            this.overDueDate = new DateTime(fpDate).plusMonths(overdueDate).plusDays(2);
            this.expiryDate = new DateTime(fpDate).plusMonths(expiry);
            return true;
        }
        return false;
    }

    public boolean isIUCDValid(int dueDay, int overdueDate, int expiry) {
        int dueDiff = Days.daysBetween(new DateTime(fpDate), new DateTime(fpDate).plusMonths(dueDay)).getDays();
        int expiryDiff = Days.daysBetween(new DateTime(fpDate), new DateTime(fpDate).plusMonths(expiry)).getDays();
        if (fpDifference >= dueDiff && fpDifference < expiryDiff) {
            this.dueDate = new DateTime(fpDate).plusMonths(dueDay);
            this.overDueDate = new DateTime(fpDate).plusMonths(overdueDate).plusDays(2);
            this.expiryDate = new DateTime(fpDate).plusMonths(expiry);
            return true;
        } else if (fpDifference < dueDiff && fpDifference < expiryDiff && dueDiff <= 31) {
            this.dueDate = new DateTime(fpDate).plusMonths(dueDay);
            return true;
        }
        return false;
    }

    public Integer getPillCycles() {
        return pillCycles;
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

    public Date getCompletionDate() {
        if (lastVisitDate != null && ((lastVisitDate.isAfter(dueDate) || lastVisitDate.isEqual(dueDate)) && lastVisitDate.isBefore(expiryDate)))
            return lastVisitDate.toDate();

        return null;
    }

    @Override
    public String getRuleKey() {
        return "fpAlertRule";
    }

    @Override
    public String getButtonStatus() {
        DateTime lastVisit = lastVisitDate;
        DateTime currentDate = new DateTime(new LocalDate().toDate());
        int monthOfYear = new DateTime(lastVisitDate).getMonthOfYear();
        int year = new DateTime(lastVisitDate).getYear();

        if (lastVisitDate != null) {
            if (expiryDate != null) {
                if ((lastVisit.isAfter(dueDate) || lastVisit.isEqual(dueDate)) && lastVisit.isBefore(expiryDate))
                    return CoreConstants.VISIT_STATE.VISIT_DONE;
                if (lastVisit.isBefore(dueDate)) {
                    if (currentDate.isBefore(overDueDate) && (currentDate.isAfter(dueDate) || currentDate.isEqual(dueDate)))
                        return CoreConstants.VISIT_STATE.DUE;

                    if (currentDate.isBefore(expiryDate) && (currentDate.isAfter(overDueDate) || currentDate.isEqual(overDueDate)))
                        return CoreConstants.VISIT_STATE.OVERDUE;
                    if (currentDate.isBefore(dueDate) && currentDate.isBefore(expiryDate)) {
                        return CoreConstants.VISIT_STATE.NOT_DUE_YET;
                    }
                }
            } else {
                if (!(fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_MALE_CONDOM)) && !(fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_FEMALE_CONDOM))) {
                    if (currentDate.isBefore(overDueDate) && (currentDate.isAfter(dueDate) || currentDate.isEqual(dueDate)))
                        return CoreConstants.VISIT_STATE.DUE;
                    if (currentDate.isAfter(overDueDate) || currentDate.isEqual(overDueDate))
                        return CoreConstants.VISIT_STATE.OVERDUE;

                    return CoreConstants.VISIT_STATE.VISIT_DONE;
                } else {
                    if ((monthOfYear == DateTime.now().getMonthOfYear()) && (year == DateTime.now().getYear())) {
                        return CoreConstants.VISIT_STATE.VISIT_DONE;
                    }
                    if (currentDate.isBefore(overDueDate))
                        return CoreConstants.VISIT_STATE.DUE;
                    if (currentDate.isAfter(overDueDate) || currentDate.isEqual(overDueDate))
                        return CoreConstants.VISIT_STATE.OVERDUE;
                }


            }

        } else {
            if (expiryDate != null) {
                if (currentDate.isBefore(dueDate) && currentDate.isBefore(expiryDate)) {
                    return CoreConstants.VISIT_STATE.NOT_DUE_YET;
                }
                if (currentDate.isBefore(overDueDate) && (currentDate.isAfter(dueDate) || currentDate.isEqual(dueDate)))
                    return CoreConstants.VISIT_STATE.DUE;

                if (currentDate.isBefore(expiryDate) && (currentDate.isAfter(overDueDate) || currentDate.isEqual(overDueDate)))
                    return CoreConstants.VISIT_STATE.OVERDUE;
            } else {
                if (currentDate.isBefore(dueDate)) {
                    return CoreConstants.VISIT_STATE.NOT_DUE_YET;
                }
                if (currentDate.isBefore(overDueDate) && (currentDate.isAfter(dueDate) || currentDate.isEqual(dueDate)))
                    return CoreConstants.VISIT_STATE.DUE;

                if ((currentDate.isAfter(overDueDate) || currentDate.isEqual(overDueDate)))
                    return CoreConstants.VISIT_STATE.OVERDUE;
            }
        }

        return CoreConstants.VISIT_STATE.EXPIRED;
    }
}