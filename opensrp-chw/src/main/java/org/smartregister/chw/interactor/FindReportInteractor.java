package org.smartregister.chw.interactor;

import org.smartregister.chw.contract.FindReportContract;
import org.smartregister.chw.dao.ReportDao;
import org.smartregister.family.util.AppExecutors;

import java.util.HashMap;
import java.util.LinkedHashMap;

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
    public void processAvailableLocations(LinkedHashMap<String, String> locations, FindReportContract.Presenter presenter) {
        Runnable runnable = () -> {
            try {
                HashMap<String, String> extracted = ReportDao.extractRecordedLocations();

                appExecutors.mainThread().execute(() -> presenter.onReportHierarchyLoaded(extracted));
            } catch (Exception e) {
                Timber.e(e);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }
}
