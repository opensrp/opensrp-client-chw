package org.smartregister.chw.util;

import org.jeasy.rules.api.Rules;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.rule.PncVisitAlertRule;

import java.util.Date;

public class HomeVisitUtilTest extends BaseUnitTest {

    @Test
    public void testGetAncVisitStatus(){
        Rules rules = ChwApplication.getInstance().getRulesEngineHelper().rules(Constants.RULE_FILE.PNC_HOME_VISIT);
        Date lastVisitDate = null;
        Date lastNotVisitDate = null;
        Date deliveryDate = LocalDate.now().plusDays(-3).toDate();

        PncVisitAlertRule pncVisitAlertRule = new PncVisitAlertRule(lastVisitDate, lastNotVisitDate, deliveryDate);
        ChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(pncVisitAlertRule, rules);
        VisitSummary visitSummary = HomeVisitUtil.getPncVisitStatus(rules, lastVisitDate, lastNotVisitDate, deliveryDate);
    }
}
