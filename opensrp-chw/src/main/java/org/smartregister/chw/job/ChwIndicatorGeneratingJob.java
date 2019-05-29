package org.smartregister.chw.job;

import android.support.annotation.NonNull;

import org.smartregister.reporting.job.RecurringIndicatorGeneratingJob;

/**
 * Specialised CHW RecurringIndicatorGeneratingJob that processes
 * home visit details before starting the tally generating service
 *
 * @author Allan
 */
public class ChwIndicatorGeneratingJob extends RecurringIndicatorGeneratingJob {

    private Flavor processorFlv = new ChwIndicatorGeneratingJobFlv();

    @NonNull
    protected Result onRunJob(@NonNull Params params) {
        processorFlv.processHomeVisitDetails();
        return super.onRunJob(params);
    }

    public interface Flavor {
        void processHomeVisitDetails();
    }
}
