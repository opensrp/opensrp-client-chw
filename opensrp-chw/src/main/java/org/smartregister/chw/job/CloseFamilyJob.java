package org.smartregister.chw.job;

import android.content.Intent;

import androidx.annotation.NonNull;

import org.smartregister.chw.service.CloseFamilyService;
import org.smartregister.family.util.Constants;
import org.smartregister.job.BaseJob;

import timber.log.Timber;

public class CloseFamilyJob extends BaseJob {
    public static final String TAG = "CloseFamilyJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Timber.v("%s started", TAG);
        getApplicationContext().startService(new Intent(getApplicationContext(), CloseFamilyService.class));
        return params.getExtras().getBoolean(Constants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
