package org.smartregister.chw.util;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Rules;
import org.joda.time.format.DateTimeFormat;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.rule.AncVisitAlertRule;

public class AncHomeVisitUtil {

    public static AncVisit getVisitStatus(Context context, Rules rules, String lmpDate, String visitDate, String lastVisitDate, String visitNotDate) {
        AncVisitAlertRule ancVisitAlertRule = new AncVisitAlertRule(context, lmpDate, visitDate, lastVisitDate, visitNotDate);
        ChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(ancVisitAlertRule, rules);
        return getVisitStatus(ancVisitAlertRule, lastVisitDate);
    }

    public static AncVisit getVisitStatus(AncVisitAlertRule homeAlertRule, String lastVisitDate) {
        AncVisit ancVisit = new AncVisit();
        ancVisit.setVisitStatus(homeAlertRule.buttonStatus);
        ancVisit.setNoOfMonthDue(homeAlertRule.noOfMonthDue);
        ancVisit.setLastVisitDays(homeAlertRule.noOfDayDue);
        ancVisit.setLastVisitMonthName(homeAlertRule.visitMonthName);
        if (StringUtils.isNotBlank(lastVisitDate)) {
            ancVisit.setLastVisitTime(DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(lastVisitDate).getMillis());
        }
        return ancVisit;
    }
}
