package org.smartregister.chw.activity;

import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONException;
import org.smartregister.chw.hiv.activity.BaseHivCommunityFollowupDetailsActivity;

import java.util.Objects;

import timber.log.Timber;

public class HivCommunityFollowupDetailsActivity extends BaseHivCommunityFollowupDetailsActivity {
    @Override
    protected void openFollowupForm() {
        try {
            HivProfileActivity.startHivCommunityFollowupFeedbackActivity(this, Objects.requireNonNull(getMemberObject()).getBaseEntityId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
 