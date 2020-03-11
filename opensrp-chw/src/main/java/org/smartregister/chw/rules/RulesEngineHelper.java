package org.smartregister.chw.helper;

import org.smartregister.chw.util.Utils;
import org.smartregister.growthmonitoring.domain.WeightForHeightZscore;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.growthmonitoring.repository.WeightForHeightRepository;

import java.util.List;

public class RulesEngineHelper extends com.vijay.jsonwizard.rules.RulesEngineHelper {

    public double getZScore(String gender, String height, String weight) {
        return Utils.getZScore(gender, height, weight);
    }
}
