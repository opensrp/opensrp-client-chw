package org.smartregister.chw.core.utils;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Rules;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.RegisterAlert;
import org.smartregister.chw.core.rule.AncVisitAlertRule;
import org.smartregister.chw.core.rule.PncVisitAlertRule;

import java.util.Date;

public class HomeVisitUtil {

    public static VisitSummary getAncVisitStatus(Context context, Rules rules, String lmpDate, String visitDate, String visitNotDate, LocalDate dateCreated) {
        AncVisitAlertRule ancVisitAlertRule = new AncVisitAlertRule(context, lmpDate, visitDate, visitNotDate, dateCreated);
        CoreChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(ancVisitAlertRule, rules);
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

    public static PncVisitAlertRule getPncVisitStatus(Rules rules, Date lastVisitDate, Date deliveryDate) {
        PncVisitAlertRule pncVisitAlertRule = new PncVisitAlertRule(lastVisitDate, deliveryDate);
        CoreChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(pncVisitAlertRule, rules);
        return pncVisitAlertRule;
    }
}
