package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.core.activity.BaseReferralNotificationDetailsActivity;

import static org.smartregister.chw.core.utils.CoreConstants.DB_CONSTANTS.NOTIFICATION_TYPE;
import static org.smartregister.chw.core.utils.CoreConstants.DB_CONSTANTS.REFERRAL_TASK_ID;

public class UpdateRegisterDetailsActivity extends BaseReferralNotificationDetailsActivity {

    public static void startActivity(Activity launcherActivity, String referralTaskId, String notificationType) {
        Intent intent = new Intent(launcherActivity, UpdateRegisterDetailsActivity.class);
        intent.putExtra(REFERRAL_TASK_ID, referralTaskId);
        intent.putExtra(NOTIFICATION_TYPE, notificationType);
        launcherActivity.startActivity(intent);
    }
}
