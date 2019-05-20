package org.smartregister.chw.job;

import android.content.Context;
import android.support.annotation.NonNull;

import org.smartregister.reporting.job.RecurringIndicatorGeneratingJob;

public class ChwIndicatorGeneratingJob extends RecurringIndicatorGeneratingJob {

    private HomeVisitInfoProcessor processorFlv = new ChwIndicatorGeneratingJobFlv();

    @NonNull
    protected Result onRunJob(@NonNull Params params) {
        super.onRunJob(params);
        processorFlv.processHomeVisitDetails(getContext());
        return params != null && params.getExtras().getBoolean("to_reschedule", false) ? Result.RESCHEDULE : Result.SUCCESS;
    }

    public interface HomeVisitInfoProcessor {
        void processHomeVisitDetails(Context context);
    }
}
