package org.smartgresiter.wcaro.sync;

import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.intent.SyncIntentService;

public class WCAROSyncIntentService extends SyncIntentService {

    @Override
    protected ClientProcessorForJava getClientProcessor() {
        return WcaroApplication.getClientProcessor(WcaroApplication.getInstance().getApplicationContext());
    }

}
