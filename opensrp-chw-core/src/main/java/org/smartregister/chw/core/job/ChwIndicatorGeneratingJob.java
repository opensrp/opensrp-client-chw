package org.smartregister.chw.core.job;

import android.support.annotation.NonNull;

import org.smartregister.reporting.job.RecurringIndicatorGeneratingJob;

public class ChwIndicatorGeneratingJob extends RecurringIndicatorGeneratingJob {
    @NonNull
    protected Result onRunJob(@NonNull Params params) {
        return super.onRunJob(params);
    }
}
