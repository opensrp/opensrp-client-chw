package org.smartregister.chw.interactor;

import org.smartregister.chw.contract.FindReportContract;
import org.smartregister.chw.dao.ReportDao;
import org.smartregister.family.util.AppExecutors;

import java.util.Map;

import timber.log.Timber;

/**
 * @author rkodev
 */

public class FindReportInteractor implements FindReportContract.Interactor {

    protected AppExecutors appExecutors;

    public FindReportInteractor() {
        appExecutors = new AppExecutors();
    }

    @Override
    public void processAvailableLocations(Map<String, String> locations, FindReportContract.Presenter presenter) {
        Runnable runnable = () -> {
            try {
                Map<String, String> hierarchy = ReportDao.extractRecordedLocations();

                appExecutors.mainThread().execute(() -> presenter.onReportHierarchyLoaded(hierarchy));
            } catch (Exception e) {
                Timber.e(e);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }
}
