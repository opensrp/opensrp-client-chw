package org.smartregister.chw.job;

        import android.content.Intent;
        import android.support.annotation.NonNull;

        import org.smartregister.chw.anc.intent.HomeVisitIntent;
        import org.smartregister.family.util.Constants;
        import org.smartregister.job.BaseJob;

        import timber.log.Timber;

public class HomeVisitServiceJob extends BaseJob {
    public static final String TAG = "HomeVisitServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Timber.v("%s started", TAG);
        getApplicationContext().startService(new Intent(getApplicationContext(), HomeVisitIntent.class));
        return params.getExtras().getBoolean(Constants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
