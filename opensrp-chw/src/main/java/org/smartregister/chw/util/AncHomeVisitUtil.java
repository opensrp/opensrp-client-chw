package org.smartregister.chw.util;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Rules;
import org.joda.time.format.DateTimeFormat;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.rule.AncVisitAlertRule;

public class AncHomeVisitUtil {

    public static AncVisit getVisitStatus(Context context, Rules rules, String lmpDate, String visitDate, String visitNotDate, Long dateCreatedLong) {
        AncVisitAlertRule ancVisitAlertRule = new AncVisitAlertRule(context, lmpDate, visitDate, visitNotDate, dateCreatedLong);
        ChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(ancVisitAlertRule, rules);
        return getVisitStatus(ancVisitAlertRule, visitDate);
    }

    public static AncVisit getVisitStatus(AncVisitAlertRule homeAlertRule, String visitDate) {
        AncVisit ancVisit = new AncVisit();
        ancVisit.setVisitStatus(homeAlertRule.buttonStatus);
        ancVisit.setNoOfMonthDue(homeAlertRule.noOfMonthDue);
        ancVisit.setLastVisitDays(homeAlertRule.noOfDayDue);
        ancVisit.setLastVisitMonthName(homeAlertRule.visitMonthName);
        if (StringUtils.isNotBlank(visitDate)) {
            ancVisit.setLastVisitTime(DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(visitDate).getMillis());
        }
        return ancVisit;
    }
}
