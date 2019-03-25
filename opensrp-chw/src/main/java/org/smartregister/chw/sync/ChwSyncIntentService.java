package org.smartregister.chw.sync;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.intent.SyncIntentService;

public class ChwSyncIntentService extends SyncIntentService {

    @Override
    protected ClientProcessorForJava getClientProcessor() {
        return ChwApplication.getClientProcessor(ChwApplication.getInstance().getApplicationContext());
    }

}
