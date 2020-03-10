package org.smartregister.chw.helper;

import org.smartregister.growthmonitoring.domain.WeightForHeightZscore;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.growthmonitoring.repository.WeightForHeightRepository;

import java.util.List;

public class RulesEngineHelper extends com.vijay.jsonwizard.rules.RulesEngineHelper {

    public double getZScore(String gender, String height, String weight) {
        double zScore = 0.0;
        List<ZScore> zscoreValues = new WeightForHeightRepository().findZScoreVariables(gender, Double.parseDouble(height));
        if (zscoreValues.size() > 0) {
            WeightForHeightZscore weightForHeightZscoreParams = ((WeightForHeightZscore) zscoreValues.get(0));
            zScore = weightForHeightZscoreParams.getZ(Double.parseDouble(weight));
        }
        return zScore;
    }
}
