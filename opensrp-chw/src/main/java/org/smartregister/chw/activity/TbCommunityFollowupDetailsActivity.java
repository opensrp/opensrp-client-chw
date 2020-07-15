package org.smartregister.chw.activity;


import com.vijay.jsonwizard.utils.FormUtils;

import org.smartregister.chw.tb.activity.BaseTbCommunityFollowupDetailsActivity;

import static org.smartregister.chw.core.utils.FormUtils.getFormUtils;

public class TbCommunityFollowupDetailsActivity extends BaseTbCommunityFollowupDetailsActivity {


    @Override
    public void openFollowupForm(){
        TbRegisterActivity.startTbFormActivity(this, getMemberObject().getBaseEntityId(),org.smartregister.chw.util.Constants.JSON_FORM.getHivCommunityFollowFeedback(),(new FormUtils()).getFormJsonFromRepositoryOrAssets(this, org.smartregister.chw.util.Constants.JSON_FORM.getHivCommunityFollowFeedback()).toString());
    }
}
 