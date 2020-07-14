package org.smartregister.chw.activity;

import com.vijay.jsonwizard.utils.FormUtils;

import org.smartregister.chw.hiv.activity.BaseHivCommunityFollowupDetailsActivity;

import static org.smartregister.chw.core.utils.FormUtils.getFormUtils;

public class HivCommunityFollowupDetailsActivity extends BaseHivCommunityFollowupDetailsActivity {
    @Override
    protected void openFollowupForm() {
        HivRegisterActivity.startHIVFormActivity(this, getMemberObject().getBaseEntityId(), org.smartregister.chw.util.Constants.JSON_FORM.getHivCommunityFollowFeedback(), (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, org.smartregister.chw.util.Constants.JSON_FORM.getHivCommunityFollowFeedback()).toString());
    }

}
 