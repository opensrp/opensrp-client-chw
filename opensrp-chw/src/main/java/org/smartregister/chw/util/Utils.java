package org.smartregister.chw.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.smartregister.chw.activity.ClientReferralActivity;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.growthmonitoring.repository.WeightForHeightRepository;

import java.util.ArrayList;
import java.util.List;

public class Utils extends org.smartregister.chw.core.utils.Utils {

    public static void launchClientReferralActivity(Activity activity, List<ReferralTypeModel> referralTypeModels, String baseEntityId) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ENTITY_ID, baseEntityId);
        bundle.setClassLoader(ReferralTypeModel.class.getClassLoader());
        bundle.putParcelableArrayList(Constants.REFERRAL_TYPES, (ArrayList<ReferralTypeModel>) referralTypeModels);
        activity.startActivity(new Intent(activity, ClientReferralActivity.class).putExtras(bundle));
    }

    public static String toCSV(String[] list) {
        String result = "";
        if (list.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : list) {
                sb.append(s).append(", ");
            }
            result = sb.deleteCharAt(sb.length() - 2).toString();
        }
        return result;
    }

    public static double getWFHZScore(String gender, String height, String weight) {
        double zScore = 0.0;
        List<ZScore> zScoreValues = new WeightForHeightRepository().findZScoreVariables(gender, Double.parseDouble(height));
        if (zScoreValues.size() > 0) {
            zScore = zScoreValues.get(0).getZ(Double.parseDouble(weight));
        }
        return zScore;
    }
}
