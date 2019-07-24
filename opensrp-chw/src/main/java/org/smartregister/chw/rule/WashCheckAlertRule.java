package org.smartregister.chw.rule;

import android.content.Context;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.smartregister.chw.R;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.chw.util.ChildUtils;

//All date formats ISO 8601 yyyy-mm-dd

/**
 * Created by mahmud on 23/07/2019.
 */
public class WashCheckAlertRule implements ICommonRule {

    public String buttonStatus = ChildProfileInteractor.VisitType.DUE.name();
    private final int[] monthNames = {R.string.january, R.string.february, R.string.march, R.string.april, R.string.may, R.string.june, R.string.july, R.string.august, R.string.september, R.string.october, R.string.november, R.string.december};

    private LocalDate dateCreated;
    private LocalDate todayDate;
    private LocalDate lastVisitDate;

    public String noOfMonthDue;
    public String noOfDayDue;
    public String visitMonthName;
    private Context context;

    public WashCheckAlertRule(Context context, long lastVisitDateLong, long dateCreatedLong) {

        this.context = context;

        this.todayDate = new LocalDate();
        if (lastVisitDateLong > 0) {
            this.lastVisitDate = new LocalDate(lastVisitDateLong);
            noOfDayDue = dayDifference(lastVisitDate, todayDate) + " days";
        }

        if (dateCreatedLong > 0) {
            this.dateCreated = new LocalDate(dateCreatedLong);
        }
    }

    public String getButtonStatus() {
        return buttonStatus;
    }



    public boolean isExpiry(Integer calYr) {
        return false;
    }
    public boolean isVisitNotDone() {
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
        return "homeAlertRule";
    }
}
