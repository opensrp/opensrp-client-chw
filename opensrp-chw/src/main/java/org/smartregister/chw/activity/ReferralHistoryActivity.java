package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.referral.activity.BaseReferralHistoryActivity;
import org.smartregister.chw.referral.domain.MemberObject;
import org.smartregister.chw.referral.util.Constants;

public class ReferralHistoryActivity extends BaseReferralHistoryActivity {
    public static void startReferralHistoryActivity(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, ReferralHistoryActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.MEMBER_OBJECT, memberObject);
        activity.startActivity(intent);
    }

}
