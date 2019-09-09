package org.smartregister.brac.hnpp.job;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;

import org.smartregister.brac.hnpp.sync.intent.PullHouseholdIdIntentService;
import org.smartregister.family.util.Constants;
import org.smartregister.job.BaseJob;

public class PullHouseholdIdsServiceJob extends BaseJob {

    public static final String TAG = "PullHouseholdIdsServiceJob";

    @NonNull
    @Override
    protected Job.Result onRunJob(@NonNull Job.Params params) {
        Intent intent = new Intent(getApplicationContext(), PullHouseholdIdIntentService.class);
        getApplicationContext().startService(intent);
        return params != null && params.getExtras().getBoolean(Constants.INTENT_KEY.TO_RESCHEDULE, false) ? Job.Result.RESCHEDULE : Job.Result.SUCCESS;
    }
}
