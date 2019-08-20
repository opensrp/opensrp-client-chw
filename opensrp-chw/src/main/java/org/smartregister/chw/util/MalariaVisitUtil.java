package org.smartregister.chw.util;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.rule.MalariaFollowUpRule;

import java.util.Date;

public class MalariaVisitUtil {

    public static MalariaFollowUpRule getMalariaStatus(Date malariaTestDate) {
        MalariaFollowUpRule malariaFollowUpRule = new MalariaFollowUpRule(malariaTestDate);
        ChwApplication.getInstance().getRulesEngineHelper().getMalariaRule(malariaFollowUpRule, Constants.RULE_FILE.MALARIA_FOLLOW_UP_VISIT);
        return malariaFollowUpRule;
    }
}
