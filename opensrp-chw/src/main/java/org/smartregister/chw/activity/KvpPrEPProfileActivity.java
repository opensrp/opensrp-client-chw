package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.core.activity.CoreKvpProfileActivity;
import org.smartregister.chw.kvp.util.Constants;
import org.smartregister.chw.util.KvpVisitUtils;

import timber.log.Timber;

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
    protected void setupViews() {
        try {
            KvpVisitUtils.processVisits(this);
        } catch (Exception e) {
            Timber.e(e);
        }
        super.setupViews();
    }
}
