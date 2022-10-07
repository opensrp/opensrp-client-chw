package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.agyw.activity.BaseAGYWProfileActivity;
import org.smartregister.chw.agyw.util.Constants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.util.Utils;
import org.smartregister.common.Gender;

import java.util.ArrayList;
import java.util.List;

public class AgywProfileActivity extends BaseAGYWProfileActivity {

    public static void startProfile(Activity activity, String baseEntityId) {
        Intent intent = new Intent(activity, AgywProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        activity.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO: update options menu with required details
        return false;
    }

    @Override
    protected void startAGYWServices() {
        AGYWServicesActivity.startMe(this, memberObject.getBaseEntityId());
    }

    @Override
    public void startReferralForm() {
        if(BuildConfig.USE_UNIFIED_REFERRAL_APPROACH){
            List<ReferralTypeModel> referralTypeModels = new ArrayList<>();
            referralTypeModels.add(new ReferralTypeModel(getString(R.string.sti_referral),
                    org.smartregister.chw.util.Constants.JSON_FORM.getSTIServicesReferralForm(), CoreConstants.TASKS_FOCUS.STI_REFERRAL));
            referralTypeModels.addAll(Utils.getCommonReferralTypes(this, memberObject.getBaseEntityId()));
            referralTypeModels.add(new ReferralTypeModel(getString(R.string.kvp_friendly_services),
                    CoreConstants.JSON_FORM.getKvpFriendlyServicesReferralForm(), CoreConstants.TASKS_FOCUS.KVP_FRIENDLY_SERVICES));
            referralTypeModels.add(new ReferralTypeModel(getString(R.string.family_planning_referral),
                    org.smartregister.chw.util.Constants.JSON_FORM.getFamilyPlanningUnifiedReferralForm(Gender.FEMALE.toString()), CoreConstants.TASKS_FOCUS.FP_SIDE_EFFECTS));
            referralTypeModels.add(new ReferralTypeModel(getString(R.string.tb_referral),
                    CoreConstants.JSON_FORM.getTbReferralForm(), CoreConstants.TASKS_FOCUS.SUSPECTED_TB));
            Utils.launchClientReferralActivity(this, referralTypeModels, memberObject.getBaseEntityId());
        }
    }
}
