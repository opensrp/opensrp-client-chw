package org.smartregister.chw.model;

import org.smartregister.chw.contract.FindReportContract;

import timber.log.Timber;

public class FilterReportFragmentModel implements FindReportContract.Model {
    @Override
    public void getLocationFilter() {
        Timber.v("getLocation");
    }
}
