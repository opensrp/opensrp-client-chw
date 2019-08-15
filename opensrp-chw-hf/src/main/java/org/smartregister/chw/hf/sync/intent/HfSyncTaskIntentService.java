package org.smartregister.chw.hf.sync.intent;

import android.content.Intent;

import org.smartregister.chw.hf.sync.helper.HfTaskServiceHelper;
import org.smartregister.sync.intent.SyncTaskIntentService;

public class HfSyncTaskIntentService extends SyncTaskIntentService {
    @Override
    protected void onHandleIntent(Intent intent) {
        HfTaskServiceHelper taskServiceHelper = HfTaskServiceHelper.getInstance();
        taskServiceHelper.syncTasks();
    }
}
