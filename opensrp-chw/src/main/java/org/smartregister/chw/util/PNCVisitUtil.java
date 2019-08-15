package org.smartregister.chw.util;


import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.rule.PNCHealthFacilityVisitRule;

import java.util.Date;

public class PNCVisitUtil {

    public static PNCHealthFacilityVisitRule getNextPNCHealthFacilityVisit(Date deliveryDate, Date lastVisitDate) {

        PNCHealthFacilityVisitRule visitRule = new PNCHealthFacilityVisitRule(deliveryDate, lastVisitDate);
        visitRule = ChwApplication.getInstance().getRulesEngineHelper().getPNCHealthFacilityRule(visitRule, Constants.RULE_FILE.PNC_HEALTH_FACILITY_VISIT);

        return visitRule;
    }


}
