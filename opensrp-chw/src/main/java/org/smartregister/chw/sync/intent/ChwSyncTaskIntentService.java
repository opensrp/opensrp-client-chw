package org.smartregister.chw.sync.intent;

import android.content.Intent;

import org.smartregister.chw.sync.helper.ChwTaskServiceHelper;
import org.smartregister.sync.intent.SyncTaskIntentService;

public class ChwSyncTaskIntentService extends SyncTaskIntentService {
    @Override
    protected void onHandleIntent(Intent intent) {
        ChwTaskServiceHelper taskServiceHelper = ChwTaskServiceHelper.getInstance();
        taskServiceHelper.syncTasks();
    }
}
