package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.core.activity.BaseChwNotificationDetailsActivity;

import static org.smartregister.chw.core.utils.CoreConstants.DB_CONSTANTS.NOTIFICATION_ID;
import static org.smartregister.chw.core.utils.CoreConstants.DB_CONSTANTS.NOTIFICATION_TYPE;

public class UpdateRegisterDetailsActivity extends BaseChwNotificationDetailsActivity {

    public static void startActivity(Activity launcherActivity, String notificationId, String notificationType) {
        Intent intent = new Intent(launcherActivity, UpdateRegisterDetailsActivity.class);
        intent.putExtra(NOTIFICATION_ID, notificationId);
        intent.putExtra(NOTIFICATION_TYPE, notificationType);
        launcherActivity.startActivity(intent);
    }

    @Override
    public void goToMemberProfile() {
        //TODO implement
    }
}
