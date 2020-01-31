package org.smartregister.chw.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.smartregister.chw.activity.ClientReferralActivity;
import org.smartregister.chw.model.ReferralTypeModel;

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
}
