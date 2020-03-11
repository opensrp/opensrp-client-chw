package org.smartregister.chw.rules;

import org.smartregister.chw.util.Utils;

public class RulesEngineHelper extends com.vijay.jsonwizard.rules.RulesEngineHelper {

    public double getZScore(String gender, String height, String weight) {
        return Utils.getZScore(gender, height, weight);
    }
}
