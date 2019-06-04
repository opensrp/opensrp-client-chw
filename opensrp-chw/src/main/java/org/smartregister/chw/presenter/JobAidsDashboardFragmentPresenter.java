package org.smartregister.chw.presenter;

import org.smartregister.reporting.contract.ReportContract;
import org.smartregister.reporting.domain.BaseReportIndicatorsModel;
import org.smartregister.reporting.domain.IndicatorQuery;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.domain.ReportIndicator;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

public class JobAidsDashboardFragmentPresenter implements ReportContract.Presenter {

    private WeakReference<ReportContract.View> viewWeakReference;
    private ReportContract.Model model;

    public JobAidsDashboardFragmentPresenter(ReportContract.View view) {
        this.viewWeakReference = new WeakReference<>(view);
        this.model = new BaseReportIndicatorsModel();
    }

    @Override
    public void onResume() {
        getView().refreshUI();
    }

    @Override
    public List<Map<String, IndicatorTally>> fetchIndicatorsDailytallies() {
        return model.getIndicatorsDailyTallies();
    }

    @Override
    public void addIndicators(List<ReportIndicator> indicatorList) {
        for (ReportIndicator indicator : indicatorList) {
            model.addIndicator(indicator);
        }
    }

    @Override
    public void addIndicatorQueries(List<IndicatorQuery> indicatorQueryList) {
        for (IndicatorQuery indicatorQuery : indicatorQueryList) {
            model.addIndicatorQuery(indicatorQuery);
        }
    }

    @Override
    public void scheduleRecurringTallyJob() {
        // Handled LoginInteractor#scheduleJobsPeriodically that schedules all CHW related jobs
    }

    public ReportContract.View getView() {
        if (viewWeakReference != null) {
            return viewWeakReference.get();
        }
        return null;
    }
}
