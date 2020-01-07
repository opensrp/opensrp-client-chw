package org.smartregister.chw.job;

import android.content.Intent;

import androidx.annotation.NonNull;

import org.smartregister.chw.service.SchedulesIntentService;
import org.smartregister.family.util.Constants;
import org.smartregister.job.BaseJob;

import timber.log.Timber;

public class ScheduleJob extends BaseJob {
    public static final String TAG = "ScheduleJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Timber.v("%s started", TAG);
        getApplicationContext().startService(new Intent(getApplicationContext(), SchedulesIntentService.class));
        return params.getExtras().getBoolean(Constants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
