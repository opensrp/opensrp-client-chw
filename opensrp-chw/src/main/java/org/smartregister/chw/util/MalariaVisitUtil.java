package org.smartregister.chw.util;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.rule.MalariaFollowUpRule;

import java.util.Date;

import timber.log.Timber;

public class MalariaVisitUtil {

    public static MalariaFollowUpRule getMalariaStatus(Date malariaTestDate) {
        MalariaFollowUpRule malariaFollowUpRule = new MalariaFollowUpRule(malariaTestDate);
        try {
            ChwApplication.getInstance().getRulesEngineHelper().getMalariaRule(malariaFollowUpRule, Constants.RULE_FILE.MALARIA_FOLLOW_UP_VISIT);
        }catch (Exception e){
            Timber.e(e);
        }
        return malariaFollowUpRule;
    }
}
