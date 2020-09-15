package org.smartregister.chw.rules;

import android.content.Context;

import org.joda.time.DateTime;
import org.smartregister.chw.core.rule.HomeAlertRule;

import java.util.Date;

public class LmhHomeAlertRule extends HomeAlertRule {

    public LmhHomeAlertRule(Context context, String yearOfBirthString, long lastVisitDateLong, long visitNotDoneValue, long dateCreatedLong) {
        super(context, yearOfBirthString, lastVisitDateLong, visitNotDoneValue, dateCreatedLong);
    }

    /**
     * visit is due the first day of the month
     *
     * @return
     */
    @Override
    public Date getDueDate() {
        return new DateTime(new Date()).withDayOfMonth(1).toDate();
    }

    @Override
    public Date getOverDueDate()  {
        return getLastDayOfMonth(new Date());
    }
}
