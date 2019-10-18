package org.smartregister.chw.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Recompute all schedules to adjust the new dates
 */
public class SchedulesIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * <p>
     * Used to name the worker thread, important only for debugging.
     */
    public SchedulesIntentService() {
        super("SchedulesIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // execute all children schedules

        // execute all anc schedules

        // execute all pnc schedules

        // execute all malaria schedules

    }
}
