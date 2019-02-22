package org.smartgresiter.wcaro.rule;

import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
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

    private LocalDate dateCreated;
    private LocalDate todayDate;
    private LocalDate lastVisitDate;
    private LocalDate visitNotDoneDate;
    public String noOfMonthDue;
    public String noOfDayDue;
    public String visitMonthName;
    private Integer yearOfBirth;

    public HomeAlertRule(String yearOfBirthString, long lastVisitDateLong, long visitNotDoneValue, long dateCreatedLong) {
        yearOfBirth = dobStringToYear(yearOfBirthString);
        
        this.todayDate = new LocalDate();
        if (lastVisitDateLong > 0) {
            this.lastVisitDate = new LocalDate(lastVisitDateLong);
            noOfDayDue = dayDifference(lastVisitDate, todayDate) + " days";
        }
        
        if (visitNotDoneValue > 0) {
            this.visitNotDoneDate = new LocalDate(visitNotDoneValue);
        }

        if (dateCreatedLong > 0) {
            this.dateCreated = new LocalDate(dateCreatedLong);
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
        if (yearOfBirth != null && yearOfBirth > calYr) {
            return true;
        }
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
            return isVisitThisMonth(dateCreated, todayDate);
        }

        return !isVisitThisMonth(lastVisitDate, todayDate);

    }

    public boolean isVisitWithinTwentyFour() {
        visitMonthName = theMonth(todayDate.getMonthOfYear()-1);
        noOfDayDue = "less than 24 hrs";
        if (lastVisitDate == null) return false;
        return !(lastVisitDate.isBefore(todayDate.minusDays(1)) && lastVisitDate.isBefore(todayDate));

    }

    public boolean isVisitWithinThisMonth() {
        if (lastVisitDate == null) return false;
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

    private Integer dobStringToYear(String yearOfBirthString) {
        if (!TextUtils.isEmpty(yearOfBirthString)) {
            try {
                String year = yearOfBirthString.contains("y") ? yearOfBirthString.substring(0, yearOfBirthString.indexOf("y")) : "";
                if (StringUtils.isNotBlank(year)) {
                    return Integer.valueOf(year);
                }
            } catch (Exception e) {
                Log.e(getClass().getCanonicalName(), e.toString(), e);
            }
        }

        return null;
    }

}
