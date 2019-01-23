package org.smartgresiter.wcaro.rule;

import android.text.TextUtils;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.smartgresiter.wcaro.interactor.ChildProfileInteractor;
import org.smartgresiter.wcaro.util.ChildUtils;

//All date formats ISO 8601 yyyy-mm-dd

/**
 * Created by ndegwamartin on 09/11/2018.
 */
public class HomeAlertRule {

    public static final String RULE_KEY = "homeAlertRule";
    public String buttonStatus = ChildProfileInteractor.VisitType.DUE.name();
    private final String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    private LocalDate todayDate;
    private LocalDate lastVisitDate;
    private LocalDate visitNotDoneDate;
    public String noOfMonthDue;
    public String noOfDayDue;
    public String visitMonthName;
    private String dob;

    public HomeAlertRule(String dateOfBirthString, long lastVisitDateLong, long visitNotDoneValue) {
        dob = dateOfBirthString.contains("y") ? dateOfBirthString.substring(0, dateOfBirthString.indexOf("y")) : "";
        String lastVisit = (lastVisitDateLong == 0) ? "" : ChildUtils.covertLongDateToDisplayDate(lastVisitDateLong);
        String visitNotDone = (visitNotDoneValue == 0) ? "" : ChildUtils.covertLongDateToDisplayDate(visitNotDoneValue);
        ;
        this.todayDate = new LocalDate();
        if (!TextUtils.isEmpty(lastVisit)) {
            this.lastVisitDate = new LocalDate(lastVisit);
            noOfDayDue = dayDifference(lastVisitDate, todayDate) + " days";
        }
        if (!TextUtils.isEmpty(visitNotDone)) {
            this.visitNotDoneDate = new LocalDate(visitNotDone);
        }
    }

    public String getButtonStatus() {
        return buttonStatus;
    }

    public boolean isVisitNotDone() {
        if (visitNotDoneDate != null && visitNotDoneDate.getMonthOfYear() == todayDate.getMonthOfYear()) {
            return true;
        }

        return false;

    }

    public boolean isExpiry(Integer calYr) {
        if (!TextUtils.isEmpty(dob)) {
            if (Integer.parseInt(dob) > calYr) {
                return true;
            }
        }
        return false;

    }

    public boolean isOverdueWithinMonth(Integer value) {
        int diff = getMonthsDifference(lastVisitDate, todayDate);
        if (visitNotDoneDate == null) {
            if (diff >= value) {
                noOfMonthDue = diff + "M";
                return true;
            }

        } else {
            if (diff >= value && (visitNotDoneDate.getMonthOfYear() != (todayDate.getMonthOfYear() - 1))) {
                noOfMonthDue = diff + "M";
                return true;
            }
        }

        return false;
    }

    public boolean isDueWithinMonth() {
        if (todayDate.getDayOfMonth() == 1) {
            return true;
        }
        if (lastVisitDate == null) return true;
        return !isVisitThisMonth(lastVisitDate, todayDate);

    }

    public boolean isVisitWithinTwentyFour() {
        visitMonthName = theMonth(todayDate.getMonthOfYear());
        noOfDayDue = "less than 24 hrs";
        return !(lastVisitDate.isBefore(todayDate.minusDays(1)) && lastVisitDate.isBefore(todayDate));

    }

    public boolean isVisitWithinThisMonth() {
        return isVisitThisMonth(lastVisitDate, todayDate);
    }

    private boolean isVisitThisMonth(LocalDate lastVisit, LocalDate todayDate) {
        return (todayDate.getMonthOfYear() == lastVisit.getMonthOfYear() && todayDate.getYear() == lastVisit.getYear());

    }

    private int dayDifference(LocalDate date1, LocalDate date2) {
        return Days.daysBetween(date1, date2).getDays();
    }

    private String theMonth(int month) {
        return monthNames[month];
    }

    private int getMonthsDifference(LocalDate date1, LocalDate date2) {
        int m1 = date1.getYear() * 12 + date1.getMonthOfYear();
        int m2 = date2.getYear() * 12 + date2.getMonthOfYear();
        return m2 - m1;
    }
}
