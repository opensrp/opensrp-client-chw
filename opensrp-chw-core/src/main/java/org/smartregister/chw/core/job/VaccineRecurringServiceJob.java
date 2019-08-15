package org.smartregister.chw.core.job;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.smartregister.family.util.Constants;
import org.smartregister.immunization.service.intent.RecurringIntentService;
import org.smartregister.immunization.service.intent.VaccineIntentService;
import org.smartregister.job.BaseJob;


public class VaccineRecurringServiceJob extends BaseJob {

    public static final String TAG = "VaccineRecurringServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getApplicationContext(), VaccineIntentService.class);
        getApplicationContext().startService(intent);
        Intent intent2 = new Intent(getApplicationContext(), RecurringIntentService.class);
        getApplicationContext().startService(intent2);
        return params.getExtras().getBoolean(Constants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
