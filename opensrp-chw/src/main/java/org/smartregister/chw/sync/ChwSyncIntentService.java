package org.smartregister.chw.sync;

import org.smartregister.chw.dao.EventDao;
import org.smartregister.sync.intent.SyncIntentService;

public class ChwSyncIntentService extends SyncIntentService {

    @Override
    protected void handleSync() {
        // fetch the last downloaded serverVersion before any unsyced data
        Long serverVersion = EventDao.getMinimumVerifiedServerVersion();
        if (serverVersion != null)
            org.smartregister.util.Utils.getAllSharedPreferences().saveLastSyncDate(serverVersion);

        // flag all contentious events as unsynced
        EventDao.markEventsForReUpload();
        super.handleSync();
    }


    @Override
    public int getEventPullLimit() {
        return 1000;
    }


    @Override
    protected Integer getEventBatchSize(){
        return 250;
    } // Should this be configurable?
}
