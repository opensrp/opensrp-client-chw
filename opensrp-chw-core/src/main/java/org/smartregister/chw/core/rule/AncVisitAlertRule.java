package org.smartregister.chw.core.rule;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.contract.RegisterAlert;
import org.smartregister.chw.core.interactor.CoreChildProfileInteractor;

public class AncVisitAlertRule implements ICommonRule, RegisterAlert {

    private final int[] monthNames = {R.string.january, R.string.february, R.string.march, R.string.april, R.string.may, R.string.june, R.string.july, R.string.august, R.string.september, R.string.october, R.string.november, R.string.december};
    public String buttonStatus = CoreChildProfileInteractor.VisitType.DUE.name();
    public String noOfMonthDue;
    public String noOfDayDue;
    public String visitMonthName;
    private LocalDate dateCreated;
    private LocalDate todayDate;
    private LocalDate lastVisitDate;
    private LocalDate visitNotDoneDate;
    private Context context;

    public AncVisitAlertRule(Context context, String lmpDate, String visitDate, String visitNotDoneDate, LocalDate dateCreated) {
        this.context = context;

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        LocalDate lmpDate1 = formatter.parseDateTime(lmpDate).toLocalDate();

        this.todayDate = new LocalDate();
        if (StringUtils.isNotBlank(visitDate)) {
            this.lastVisitDate = formatter.parseDateTime(visitDate).toLocalDate();
        }

        noOfDayDue = ((lastVisitDate == null) ? dayDifference(lmpDate1, todayDate) : dayDifference(lastVisitDate, todayDate)) + " days";

        if (StringUtils.isNotBlank(visitNotDoneDate)) {
            this.visitNotDoneDate = formatter.parseDateTime(visitNotDoneDate).toLocalDate();
        }

        this.dateCreated = dateCreated;
    }

    private int dayDifference(LocalDate date1, LocalDate date2) {
        return Days.daysBetween(date1, date2).getDays();
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

    private int getMonthsDifference(LocalDate date1, LocalDate date2) {
        return Months.monthsBetween(
                date1.withDayOfMonth(1),
                date2.withDayOfMonth(1)).getMonths();
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
        return "ancVisitAlertRule";
    }

    @Override
    public String getButtonStatus() {
        return buttonStatus;
    }
}
