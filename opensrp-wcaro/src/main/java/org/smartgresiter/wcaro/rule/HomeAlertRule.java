package org.smartgresiter.wcaro.rule;

import android.text.TextUtils;
import android.util.Log;

import org.joda.time.LocalDate;
import org.smartgresiter.wcaro.interactor.ChildProfileInteractor;
import org.smartgresiter.wcaro.util.ChildUtils;

//All date formats ISO 8601 yyyy-mm-dd

/**
 * Created by ndegwamartin on 09/11/2018.
 */
public class HomeAlertRule {

    public static final String RULE_KEY = "homeAlertRule";
    public String buttonStatus=ChildProfileInteractor.VisitType.DUE.name();

    public  String lastVisitDate;
    public  String visitNotDoneValue;
    private LocalDate todayDate;

    public HomeAlertRule(long lastVisitDateLong, long visitNotDoneValue) {
        this.lastVisitDate=(lastVisitDateLong==0)?"":ChildUtils.covertLongDateToDisplayDate(lastVisitDateLong);

        this.visitNotDoneValue = (visitNotDoneValue==0)?"":ChildUtils.covertLongDateToDisplayDate(visitNotDoneValue);;

        this.todayDate = new LocalDate();
    }

    public String getButtonStatus() {
        return buttonStatus;
    }
    public boolean isOverdueWithinMonth() {

        LocalDate lastVisit = new LocalDate(lastVisitDate);
        LocalDate visitNotDone = new LocalDate(visitNotDoneValue);
        int diff=getMonthsDifference(lastVisit,todayDate);
        if(diff>=2 && (visitNotDone.getMonthOfYear()==(todayDate.getMonthOfYear()-1))){
            return true;
        }
        return false;
    }

    public boolean isDueWithinMonth() {

        LocalDate lastVisit = new LocalDate(lastVisitDate);
        if(todayDate.getDayOfMonth()==1){
            return true;
        }
        if(TextUtils.isEmpty(lastVisitDate)) return true;
        return !isVisitThisMonth(lastVisit,todayDate);

    }

   public boolean isVisitWithinTwentyFour(){
       LocalDate lastVisit = new LocalDate(lastVisitDate);
       return ! (lastVisit.isBefore( todayDate.minusDays(1) )  &&  lastVisit.isBefore( todayDate));

   }
    public boolean isVisitWithinThisMonth(){
        LocalDate lastVisit = new LocalDate(lastVisitDate);
        return isVisitThisMonth(lastVisit,todayDate);
    }
    private static boolean isVisitThisMonth(LocalDate lastVisit,LocalDate todayDate){
       return (todayDate.getMonthOfYear()==lastVisit.getMonthOfYear() && todayDate.getYear() == lastVisit.getYear());

    }

    private static int getMonthsDifference(LocalDate date1, LocalDate date2) {
        int m1 = date1.getYear() * 12 + date1.getMonthOfYear();
        int m2 = date2.getYear() * 12 + date2.getMonthOfYear();
        return m2 - m1 + 1;
    }
}
