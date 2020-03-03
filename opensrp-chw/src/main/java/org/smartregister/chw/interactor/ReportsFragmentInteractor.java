package org.smartregister.chw.interactor;

import org.smartregister.chw.contract.ListContract;
import org.smartregister.chw.domain.ReportType;
import org.smartregister.family.util.AppExecutors;

import java.util.List;
import java.util.concurrent.Callable;

import timber.log.Timber;

public class ReportsFragmentInteractor implements ListContract.Interactor<ReportType> {

    protected AppExecutors appExecutors;

    public ReportsFragmentInteractor() {
        appExecutors = new AppExecutors();
    }

    @Override
    public void runRequest(Callable<List<ReportType>> callable, ListContract.Presenter<ReportType> presenter) {

        Runnable runnable = () -> {
            try {
                List<ReportType> reportTypes = callable.call();
                appExecutors.mainThread().execute(() -> presenter.onItemsFetched(reportTypes));
            } catch (Exception e) {
                Timber.e(e);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }
}
