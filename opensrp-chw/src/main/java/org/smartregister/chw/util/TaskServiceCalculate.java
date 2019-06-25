package org.smartregister.chw.util;

import org.joda.time.LocalDate;
import org.smartregister.chw.rule.ICommonRule;
import org.smartregister.chw.util.Utils;
import org.smartregister.util.DateUtil;

//All date formats ISO 8601 yyyy-mm-dd

public class TaskServiceCalculate {

    public String buttonStatus= "";
    private LocalDate todayDate;
    private LocalDate birthDay;

    public TaskServiceCalculate(String dateOfBirth){

        todayDate = new LocalDate();
        birthDay= new LocalDate(Utils.dobStringToDate(dateOfBirth));

    }
    public boolean isOverdue(Integer month) {


        return todayDate.isAfter(birthDay.plusMonths(month));

    }

    public boolean isDue(Integer month) {


        return todayDate.isAfter(birthDay.plusMonths(month));

    }
    public boolean isExpire(Integer month) {


        return todayDate.isAfter(birthDay.plusMonths(month));

    }
    public enum TASK_TYPE {Minimum_dietary, MUAC, LLITN,ECD}
}
