package org.smartregister.chw.job;

import android.content.Context;

import timber.log.Timber;

public class ChwIndicatorGeneratingJobFlv implements ChwIndicatorGeneratingJob.Flavor {
    @Override
    public void preExecute(Context context) {
        Timber.v("BA Reporting pre-execute");
    }
}
