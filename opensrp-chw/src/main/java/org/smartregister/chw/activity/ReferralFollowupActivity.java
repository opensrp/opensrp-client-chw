package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.referral.activity.BaseReferralFollowupActivity;
import org.smartregister.chw.referral.domain.MemberObject;
import org.smartregister.chw.referral.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import static org.smartregister.chw.core.utils.CoreConstants.JSON_FORM.getReferralFollowupForm;


public class ReferralFollowupActivity extends BaseReferralFollowupActivity {
    private static final String CLIENT = "client";

    public static void startReferralFollowupActivity(Activity activity, MemberObject memberObject, CommonPersonObjectClient client) {
        Intent intent = new Intent(activity, ReferralFollowupActivity.class);
        intent.putExtra(Constants.REFERRAL_MEMBER_OBJECT.MEMBER_OBJECT, memberObject);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.REFERRAL_FOLLOWUP_FORM_NAME, getReferralFollowupForm());
        intent.putExtra(CLIENT, client);
        activity.startActivity(intent);
    }
}
