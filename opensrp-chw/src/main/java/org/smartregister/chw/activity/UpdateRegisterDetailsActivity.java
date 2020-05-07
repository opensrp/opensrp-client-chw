package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;


import org.smartregister.chw.core.activity.BaseChwNotificationDetailsActivity;

import static org.smartregister.chw.core.utils.CoreConstants.DB_CONSTANTS.BASE_ENTITY_ID;
import static org.smartregister.chw.core.utils.CoreConstants.DB_CONSTANTS.NOTIFICATION_TYPE;

public class UpdateRegisterDetailsActivity extends BaseChwNotificationDetailsActivity {

    public static void startActivity(Activity launcherActivity, String baseEntityId, String notificationType) {
        Intent intent = new Intent(launcherActivity, UpdateRegisterDetailsActivity.class);
        intent.putExtra(BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(NOTIFICATION_TYPE, notificationType);
        launcherActivity.startActivity(intent);
    }
}
