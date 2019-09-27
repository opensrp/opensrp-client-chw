package org.smartregister.chw.core.rule;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.Date;

//All date formats ISO 8601 yyyy-mm-dd

/**
 * Created by ndegwamartin on 09/11/2018.
 */
public class HomeAlertRule implements ICommonRule {

    private final int[] monthNames = {R.string.january, R.string.february, R.string.march, R.string.april, R.string.may, R.string.june, R.string.july, R.string.august, R.string.september, R.string.october, R.string.november, R.string.december};
    public String buttonStatus = CoreConstants.VisitType.DUE.name();
    public String noOfMonthDue;
    public String noOfDayDue;
    public String visitMonthName;
    private LocalDate dateCreated;
    private LocalDate todayDate;
    private LocalDate lastVisitDate;
    private LocalDate visitNotDoneDate;
    private Integer yearOfBirth;
    private Context context;

    public HomeAlertRule(Context context, String yearOfBirthString, long lastVisitDateLong, long visitNotDoneValue, long dateCreatedLong) {
        yearOfBirth = CoreChildUtils.dobStringToYear(yearOfBirthString);

        this.context = context;

        this.todayDate = new LocalDate();
        if (lastVisitDateLong > 0) {
            this.lastVisitDate = new LocalDate(lastVisitDateLong);
            noOfDayDue = dayDifference(lastVisitDate, todayDate) + " " + context.getString(R.string.days);
        }

        if (visitNotDoneValue > 0) {
            this.visitNotDoneDate = new LocalDate(visitNotDoneValue);
        }

        if (dateCreatedLong > 0) {
            this.dateCreated = new LocalDate(dateCreatedLong);
        }
    }

    private int dayDifference(LocalDate date1, LocalDate date2) {
        return Days.daysBetween(date1, date2).getDays();
    }

    public boolean isVisitNotDone() {
        return (visitNotDoneDate != null && getMonthsDifference(visitNotDoneDate, todayDate) < 1);
    }

    private int getMonthsDifference(LocalDate date1, LocalDate date2) {
        return Months.monthsBetween(
                date1.withDayOfMonth(1),
                date2.withDayOfMonth(1)).getMonths();
    }

    public boolean isExpiry(Integer calYr) {
        return (yearOfBirth != null && yearOfBirth > calYr);
    }

    public boolean isOverdueWithinMonth(Integer value) {
        int diff = getMonthsDifference((lastVisitDate != null ? lastVisitDate : dateCreated), todayDate);
        if (diff >= value) {
            noOfMonthDue = diff + StringUtils.upperCase(context.getString(R.string.abbrv_months));
            return true;
        }
        return false;
    }

    public boolean isDueWithinMonth() {
        if (todayDate.getDayOfMonth() == 1) {
            return true;
        }
        if (lastVisitDate == null) {
            return !isVisitThisMonth(dateCreated, todayDate);
        }

        return !isVisitThisMonth(lastVisitDate, todayDate);
    }

    private boolean isVisitThisMonth(LocalDate lastVisit, LocalDate todayDate) {
        return getMonthsDifference(lastVisit, todayDate) < 1;
    }

    public boolean isVisitWithinTwentyFour() {
        visitMonthName = theMonth(todayDate.getMonthOfYear() - 1);
        noOfDayDue = context.getString(R.string.less_than_twenty_four);
        return (lastVisitDate != null) && !(lastVisitDate.isBefore(todayDate.minusDays(1)) && lastVisitDate.isBefore(todayDate));
    }

    private String theMonth(int month) {
        return context.getResources().getString(monthNames[month]);
    }

    public boolean isVisitWithinThisMonth() {
        return (lastVisitDate != null) && isVisitThisMonth(lastVisitDate, todayDate);
    }

    @Override
    public String getRuleKey() {
        return "homeAlertRule";
    }

    @Override
    public String getButtonStatus() {
        return buttonStatus;
    }

    private Date getFirstDayOfMonth(Date refDate) {
        return new DateTime(refDate).withDayOfMonth(1).toDate();
    }

    protected Date getLastDayOfMonth(Date refDate) {
        DateTime first = new DateTime(refDate).withDayOfMonth(1);
        return first.plusMonths(1).minusDays(1).toDate();
    }

    /**
     * visit is due the first day of the month
     *
     * @return
     */
    public Date getDueDate() {
        Date lastDueDate = getLastDueDate();
        if (lastDueDate.getTime() < getFirstDayOfMonth(new Date()).getTime()) {
            return getFirstDayOfMonth(new Date());
        } else {
            return lastDueDate;
        }
    }

    private Date getLastDueDate() {
        if (lastVisitDate != null && getFirstDayOfMonth(lastVisitDate.toDate()).getTime() < dateCreated.toDate().getTime()) {
            return getFirstDayOfMonth(lastVisitDate.toDate());
        } else {
            return dateCreated != null ? dateCreated.toDate() : new LocalDate().toDate();
        }
    }

    public Date getNotDoneDate(){
        if (getCompletionDate() == null && visitNotDoneDate != null) {
            return visitNotDoneDate.toDate();
        }
        return null;
    }

    public Date getCompletionDate() {
        if (lastVisitDate != null && lastVisitDate.toDate().getTime() >= getDueDate().getTime()) {
            return lastVisitDate.toDate();
        }
        return null;
    }

    public Date getExpiryDate() {
        return getLastDayOfMonth(new Date());
    }

    public Date getOverDueDate() {
        Date anchor = (lastVisitDate != null ? lastVisitDate.toDate() : dateCreated.toDate());
        Date overDue = getLastDayOfMonth(anchor);
        if (overDue.getTime() < getDueDate().getTime()) {
            return getDueDate();
        }
        return overDue;
    }
}
