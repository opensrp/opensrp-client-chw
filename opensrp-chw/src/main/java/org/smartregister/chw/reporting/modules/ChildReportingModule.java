package org.smartregister.chw.reporting.modules;

import android.view.ViewGroup;

import org.smartregister.chw.R;
import org.smartregister.chw.reporting.models.NumericViewModel;
import org.smartregister.chw.reporting.views.IndicatorView.CountType;
import org.smartregister.chw.reporting.views.NumericIndicatorView;
import org.smartregister.chw.util.DashboardUtil;
import org.smartregister.reporting.domain.IndicatorTally;

import java.util.List;
import java.util.Map;

import static org.smartregister.chw.reporting.ReportingUtil.getLatestCountBasedOnDate;
import static org.smartregister.chw.reporting.ReportingUtil.getTotalStaticCount;
import static org.smartregister.chw.reporting.views.IndicatorViewFactory.createView;

public class ChildReportingModule implements ReportingModule {

    private List<Map<String, IndicatorTally>> indicatorTallies;

    @Override
    public void createReport(ViewGroup mainLayout) {
        mainLayout.removeAllViews();
        NumericViewModel numericViewModel = getNumericViewModel(CountType.STATIC_COUNT, DashboardUtil.countOfChildrenUnder5, R.string.total_under_5_children_label);
        mainLayout.addView(createView(new NumericIndicatorView(mainLayout.getContext(), numericViewModel)));
        NumericViewModel numericViewModel2 = getNumericViewModel(CountType.STATIC_COUNT, DashboardUtil.deceasedChildren0_11Months, R.string.deceased_children_0_11_months);
        mainLayout.addView(createView(new NumericIndicatorView(mainLayout.getContext(), numericViewModel2)));
        NumericViewModel numericViewModel3 = getNumericViewModel(CountType.STATIC_COUNT, DashboardUtil.deceasedChildren12_59Months, R.string.deceased_children_12_59_months);
        mainLayout.addView(createView(new NumericIndicatorView(mainLayout.getContext(), numericViewModel3)));

    }

    private NumericViewModel getNumericViewModel(CountType countType, String indicatorCode, int labelResource) {
        long count = 0;
        if (countType == CountType.STATIC_COUNT) {
            count = getTotalStaticCount(indicatorTallies, indicatorCode);
        } else if ((countType == CountType.LATEST_COUNT)) {
            count = getLatestCountBasedOnDate(indicatorTallies, indicatorCode);
        }
        return new NumericViewModel(countType, indicatorCode, labelResource, count);
    }

    public List<Map<String, IndicatorTally>> getIndicatorTallies() {
        return indicatorTallies;
    }

    public void setIndicatorTallies(List<Map<String, IndicatorTally>> indicatorTallies) {
        this.indicatorTallies = indicatorTallies;
    }

}
