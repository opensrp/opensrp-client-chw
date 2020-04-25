package org.smartregister.chw.util;

import org.jeasy.rules.api.Rules;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fp_pathfinder.domain.FpMemberObject;
import org.smartregister.chw.fp_pathfinder.util.FamilyPlanningConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class PathfinderFamilyPlanningUtil extends org.smartregister.chw.fp.util.FpUtil {

    public static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    public static Rules getFpRules(String fpMethod) {
        Rules fpRule = new Rules();

        switch (fpMethod) {
            case FamilyPlanningConstants.DBConstants.FP_POP:
            case FamilyPlanningConstants.DBConstants.FP_COC:
                fpRule = CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.FP_COC_POP_REFILL);
                break;
            case FamilyPlanningConstants.DBConstants.FP_IUCD:
                fpRule = CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.FP_IUCD);
                break;
            case FamilyPlanningConstants.DBConstants.FP_FEMALE_CONDOM:
            case FamilyPlanningConstants.DBConstants.FP_MALE_CONDOM:
                fpRule = CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.FP_CONDOM_REFILL);
                break;
            case FamilyPlanningConstants.DBConstants.FP_INJECTABLE:
                fpRule = CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.FP_INJECTION_DUE);
                break;
            case FamilyPlanningConstants.DBConstants.FP_FEMALE_STERLIZATION:
                fpRule = CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.FP_FEMALE_STERILIZATION);
                break;
            default:
                break;
        }
        return fpRule;
    }

    public static Date parseFpStartDate(String startDate) {
        try {
            return sdf.parse(startDate);
        } catch (ParseException e) {
            Timber.e(e);
        }

        return null;
    }

    public static MemberObject toMember(FpMemberObject memberObject) {
        MemberObject res = new MemberObject();
        res.setBaseEntityId(memberObject.getBaseEntityId());
        res.setFirstName(memberObject.getFirstName());
        res.setLastName(memberObject.getLastName());
        res.setMiddleName(memberObject.getMiddleName());
        res.setDob(memberObject.getAge());
        return res;
    }
}
