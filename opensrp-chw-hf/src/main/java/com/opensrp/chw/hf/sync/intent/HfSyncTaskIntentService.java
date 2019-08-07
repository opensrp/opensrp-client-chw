package com.opensrp.chw.hf.sync.intent;

import android.content.Intent;

import com.opensrp.chw.hf.sync.helper.HfTaskServiceHelper;

import org.smartregister.sync.intent.SyncTaskIntentService;

public class HfSyncTaskIntentService extends SyncTaskIntentService {
    private static final String TAG = "SyncTaskIntentService";

    public HfSyncTaskIntentService() {
        super();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HfTaskServiceHelper taskServiceHelper = HfTaskServiceHelper.getInstance();
        taskServiceHelper.syncTasks();
    }
}
