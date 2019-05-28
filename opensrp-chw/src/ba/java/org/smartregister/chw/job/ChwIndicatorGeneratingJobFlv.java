package org.smartregister.chw.job;

import timber.log.Timber;

public class ChwIndicatorGeneratingJobFlv implements ChwIndicatorGeneratingJob.Flavor {

    @Override
    public void processHomeVisitDetails() {
        Timber.v("processHomeVisitDetails");
    }
}
