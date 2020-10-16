package org.smartregister.chw.interactor;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.FindReportContract;
import org.smartregister.chw.dao.ReportDao;
import org.smartregister.family.util.AppExecutors;

import java.util.LinkedHashMap;
import java.util.List;
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
    public void processAvailableLocations(LinkedHashMap<String, String> locations, FindReportContract.Presenter presenter) {
        Runnable runnable = () -> {
            try {
                LinkedHashMap<String, String> hierarchy = new LinkedHashMap<>();

                if (ChwApplication.getApplicationFlavor().useCHWInReportingView())
                    hierarchy.putAll(ReportDao.extractRecordedProviders());
                else {

                    List<String> extracted = ReportDao.extractRecordedLocations();
                    for (Map.Entry<String, String> entry : locations.entrySet()) {
                        if (extracted.contains(entry.getKey()))
                            hierarchy.put(entry.getKey(), entry.getValue());
                    }
                }
                appExecutors.mainThread().execute(() -> presenter.onReportHierarchyLoaded(hierarchy));
            } catch (Exception e) {
                Timber.e(e);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }
}
