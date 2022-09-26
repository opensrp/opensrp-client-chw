package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.core.activity.CoreKvpProfileActivity;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.kvp.util.Constants;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.util.KvpVisitUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.chw.util.Utils.getCommonReferralTypes;
import static org.smartregister.chw.util.Utils.launchClientReferralActivity;

public class KvpPrEPProfileActivity extends CoreKvpProfileActivity {
    public static void startProfileActivity(Activity activity, String baseEntityId) {
        Intent intent = new Intent(activity, KvpPrEPProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.PROFILE_TYPE, Constants.PROFILE_TYPES.KVP_PrEP_PROFILE);
        activity.startActivity(intent);
    }

    @Override
    public void openFollowupVisit() {
        KvpPrEPVisitActivity.startKvpPrEPVisitActivity(this, memberObject.getBaseEntityId(), false);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        setupViews();
    }

    @Override
    protected boolean showReferralView() {
        return true;
    }

    @Override
    public void startReferralForm() {
        if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH) {
            List<ReferralTypeModel> referralTypeModels = new ArrayList<>();
            referralTypeModels.add(new ReferralTypeModel(getString(R.string.kvp_friendly_services),
                    CoreConstants.JSON_FORM.getKvpFriendlyServicesReferralForm(), CoreConstants.TASKS_FOCUS.KVP_FRIENDLY_SERVICES));
            referralTypeModels.addAll(getCommonReferralTypes(this, memberObject.getBaseEntityId()));

            launchClientReferralActivity(this, referralTypeModels, memberObject.getBaseEntityId());
        } else {
            Toast.makeText(this, "Refer to facility", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void setupViews() {
        try {
            KvpVisitUtils.processVisits(this);
        } catch (Exception e) {
            Timber.e(e);
        }
        super.setupViews();
    }
}
