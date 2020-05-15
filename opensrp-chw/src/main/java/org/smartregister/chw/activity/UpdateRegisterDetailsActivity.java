package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;


import org.smartregister.chw.core.activity.BaseChwNotificationDetailsActivity;
import org.smartregister.chw.presenter.ChwNotificationDetailsPresenter;

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
    public void initPresenter() {
        presenter = new ChwNotificationDetailsPresenter(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            notificationId = getIntent().getExtras().getString(NOTIFICATION_ID);

            notificationType = getIntent().getExtras().getString(NOTIFICATION_TYPE);
            presenter.getNotificationDetails(notificationId, notificationType);
        }
    }
}
