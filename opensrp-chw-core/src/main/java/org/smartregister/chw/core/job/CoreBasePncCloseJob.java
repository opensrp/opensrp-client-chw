package org.smartregister.chw.core.job;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.smartregister.chw.core.intent.CoreChwPncCloseDateIntent;
import org.smartregister.family.util.Constants;
import org.smartregister.job.BaseJob;

import timber.log.Timber;

public class CoreBasePncCloseJob extends BaseJob {
    public static final String TAG = "CoreBasePncCloseJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Timber.v("%s started", TAG);
        getApplicationContext().startService(new Intent(getApplicationContext(), CoreChwPncCloseDateIntent.class));
        return params.getExtras().getBoolean(Constants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
