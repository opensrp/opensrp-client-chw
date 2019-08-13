package org.smartregister.chw.rule;

import android.content.Context;

import org.smartregister.chw.core.rule.ICommonRule;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.smartregister.chw.R;
import org.smartregister.chw.contract.RegisterAlert;
import org.smartregister.chw.interactor.ChildProfileInteractor;

public class AncVisitAlertRule implements ICommonRule, RegisterAlert {

    private final int[] monthNames = {R.string.january, R.string.february, R.string.march, R.string.april, R.string.may, R.string.june, R.string.july, R.string.august, R.string.september, R.string.october, R.string.november, R.string.december};
    public String buttonStatus = ChildProfileInteractor.VisitType.DUE.name();
    public String noOfMonthDue;
    public String noOfDayDue;
    public String visitMonthName;
    private LocalDate dateCreated;
    private LocalDate todayDate;
    private LocalDate lastVisitDate;
    private LocalDate visitNotDoneDate;
    private Context context;
    private LocalDate lmpDate;

    public AncVisitAlertRule(Context context, String lmpDate, String visitDate, String visitNotDoneDate, LocalDate dateCreated) {
        this.context = context;

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        this.lmpDate = formatter.parseDateTime(lmpDate).toLocalDate();

        this.todayDate = new LocalDate();
        if (StringUtils.isNotBlank(visitDate)) {
            this.lastVisitDate = formatter.parseDateTime(visitDate).toLocalDate();
        }

        noOfDayDue = ((lastVisitDate == null) ? dayDifference(this.lmpDate, todayDate) : dayDifference(lastVisitDate, todayDate)) + " days";

        if (StringUtils.isNotBlank(visitNotDoneDate)) {
            this.visitNotDoneDate = formatter.parseDateTime(visitNotDoneDate).toLocalDate();
        }

        this.dateCreated = dateCreated;
    }

    @Override
    public String getButtonStatus() {
        return buttonStatus;
    }

    @Override
    public String getNumberOfMonthsDue() {
        return noOfMonthDue;
    }

    @Override
    public String getNumberOfDaysDue() {
        return noOfDayDue;
    }

    @Override
    public String getVisitMonthName() {
        return visitMonthName;
    }

    public boolean isVisitNotDone() {
        return (visitNotDoneDate != null && getMonthsDifference(visitNotDoneDate, todayDate) < 1);
    }

    // never expire
    public boolean isExpiry() {
        //return (lmpDate != null) && Months.monthsBetween(lmpDate, todayDate).getMonths() > 11;
        return false;
    }

    public boolean isOverdueWithinMonth(Integer value) {
        int diff = getMonthsDifference((lastVisitDate != null ? lastVisitDate : dateCreated), todayDate);
        if (diff >= value) {
            noOfMonthDue = diff + "M";
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

    public boolean isVisitWithinTwentyFour() {
        visitMonthName = theMonth(todayDate.getMonthOfYear() - 1);
        noOfDayDue = context.getString(R.string.less_than_twenty_four);
        return (lastVisitDate != null) && !(lastVisitDate.isBefore(todayDate.minusDays(1)) && lastVisitDate.isBefore(todayDate));
    }

    public boolean isVisitWithinThisMonth() {
        return (lastVisitDate != null) && isVisitThisMonth(lastVisitDate, todayDate);
    }

    private boolean isVisitThisMonth(LocalDate lastVisit, LocalDate todayDate) {
        return getMonthsDifference(lastVisit, todayDate) < 1;
    }

    private int dayDifference(LocalDate date1, LocalDate date2) {
        return Days.daysBetween(date1, date2).getDays();
    }

    private String theMonth(int month) {
        return context.getResources().getString(monthNames[month]);
    }

    private int getMonthsDifference(LocalDate date1, LocalDate date2) {
        return Months.monthsBetween(
                date1.withDayOfMonth(1),
                date2.withDayOfMonth(1)).getMonths();
    }

    @Override
    public String getRuleKey() {
        return "ancVisitAlertRule";
    }
}
