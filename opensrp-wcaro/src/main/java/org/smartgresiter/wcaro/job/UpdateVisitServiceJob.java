package org.smartgresiter.wcaro.job;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;

import org.smartgresiter.wcaro.service.UpdateVisitStatusIntentService;
import org.smartregister.family.util.Constants;
import org.smartregister.job.BaseJob;


public class UpdateVisitServiceJob extends BaseJob {

    public static final String TAG = "UpdateVisitServiceJob";

    @NonNull
    @Override
    protected Job.Result onRunJob(@NonNull Job.Params params) {
        Intent intent = new Intent(getApplicationContext(), UpdateVisitStatusIntentService.class);
        getApplicationContext().startService(intent);
        return params.getExtras().getBoolean(Constants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
