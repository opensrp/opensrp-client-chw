package org.smartregister.chw.activity;

import org.json.JSONException;
import org.smartregister.chw.hiv.activity.BaseHivCommunityFollowupDetailsActivity;

import java.util.Objects;

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
 