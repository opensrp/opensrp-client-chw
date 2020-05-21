package org.smartregister.chw.util;

import android.app.Activity;
import android.util.Pair;
import android.view.View;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.UpdateRegisterDetailsActivity;
import org.smartregister.chw.core.adapter.NotificationListAdapter;

import java.util.List;

public class NotificationsUtil {
    public static void handleNotificationRowClick(Activity launcherActivity, View view,
                                                  NotificationListAdapter notificationListAdapter) {
        if (view.getTag() instanceof NotificationListAdapter.NotificationRowViewHolder
                && notificationListAdapter.canOpen) {
            notificationListAdapter.canOpen = false;
            NotificationListAdapter.NotificationRowViewHolder notificationRowViewHolder =
                    (NotificationListAdapter.NotificationRowViewHolder) view.getTag();
            Pair<String, String> notificationRecord = notificationListAdapter.getNotificationRecords()
                    .get(notificationRowViewHolder.getAdapterPosition());
            UpdateRegisterDetailsActivity.startActivity(launcherActivity, notificationRecord.first, notificationRecord.second);
        }
    }

    public static void handleReceivedNotifications(Activity activity, List<Pair<String, String>> notifications,
                                                   NotificationListAdapter notificationListAdapter) {
        notificationListAdapter.getNotificationRecords().clear();
        notificationListAdapter.getNotificationRecords().addAll(notifications);
        notificationListAdapter.notifyDataSetChanged();
        activity.findViewById(R.id.notification_and_referral_row).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.view_notification_and_referral_row).setVisibility(View.VISIBLE);
    }
}
