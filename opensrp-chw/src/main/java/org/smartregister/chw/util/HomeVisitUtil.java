package org.smartregister.chw.util;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Rules;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.RegisterAlert;
import org.smartregister.chw.rule.AncVisitAlertRule;
import org.smartregister.chw.rule.PncVisitAlertRule;

import java.util.Date;

public class HomeVisitUtil {

    public static VisitSummary getAncVisitStatus(Context context, Rules rules, String lmpDate, String visitDate, String visitNotDate, LocalDate dateCreated) {
        AncVisitAlertRule ancVisitAlertRule = new AncVisitAlertRule(context, lmpDate, visitDate, visitNotDate, dateCreated);
        ChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(ancVisitAlertRule, rules);
        Date date = null;
        if (StringUtils.isNotBlank(visitDate)) {
            date = DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(visitDate).toDate();
        }
        return getAncVisitStatus(ancVisitAlertRule, date);
    }

    public static VisitSummary getAncVisitStatus(RegisterAlert registerAlert, Date visitDate) {
        VisitSummary visitSummary = new VisitSummary();
        visitSummary.setVisitStatus(registerAlert.getButtonStatus());
        visitSummary.setNoOfMonthDue(registerAlert.getNumberOfMonthsDue());
        visitSummary.setNoOfDaysDue(registerAlert.getNumberOfDaysDue());
        visitSummary.setLastVisitDays(registerAlert.getNumberOfDaysDue());
        visitSummary.setLastVisitMonthName(registerAlert.getVisitMonthName());
        if (visitDate != null) {
            visitSummary.setLastVisitTime(visitDate.getTime());
        }
        return visitSummary;
    }

    public static VisitSummary getPncVisitStatus(Rules rules, Date lastVisitDate, Date lastNotVisitDate, Date deliveryDate) {
        PncVisitAlertRule pncVisitAlertRule = new PncVisitAlertRule(lastVisitDate, lastNotVisitDate, deliveryDate);
        ChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(pncVisitAlertRule, rules);
        return getAncVisitStatus(pncVisitAlertRule, lastVisitDate);
    }
}
