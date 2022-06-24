package org.smartregister.chw.util;

import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.HomeVisitUtil;
import org.smartregister.chw.rule.CbhsFollowupRule;

import java.util.Date;

public class ChwHomeVisitUtil extends HomeVisitUtil {

    public static CbhsFollowupRule getCBHSVisitStatus(Date lastVisitDate, Date hivDate) {
        CbhsFollowupRule cbhsFollowupRule = new CbhsFollowupRule(hivDate, lastVisitDate);
        CoreChwApplication.getInstance().getRulesEngineHelper().getHivRule(cbhsFollowupRule, CoreConstants.RULE_FILE.HIV_FOLLOW_UP_VISIT);
        return cbhsFollowupRule;
    }
}
