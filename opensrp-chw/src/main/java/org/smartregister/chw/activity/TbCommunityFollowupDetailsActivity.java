package org.smartregister.chw.activity;


import org.smartregister.chw.tb.activity.BaseTbCommunityFollowupDetailsActivity;

import static org.smartregister.chw.core.utils.FormUtils.getFormUtils;

public class TbCommunityFollowupDetailsActivity extends BaseTbCommunityFollowupDetailsActivity {


    @Override
    public void openFollowupForm(){
        TbRegisterActivity.startTbFormActivity(this, getMemberObject().getBaseEntityId(),org.smartregister.chw.util.Constants.JSON_FORM.getHivCommunityFollowFeedback(),getFormUtils().getFormJsonFromRepositoryOrAssets(org.smartregister.chw.util.Constants.JSON_FORM.getHivCommunityFollowFeedback()).toString());
    }
}
 