package org.smartregister.chw.reporting;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import org.smartregister.chw.reporting.models.IndicatorModel;
import org.smartregister.chw.reporting.models.PieChartViewModel;
import org.smartregister.chw.reporting.views.IndicatorView;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.domain.ReportingIndicatorVisualization;
import org.smartregister.reporting.util.AggregationUtil;
import org.smartregister.reporting.view.IndicatorVisualisationFactory;

import java.util.List;
import java.util.Map;

public class ReportingUtil {


    public static long getTotalStaticCount(List<Map<String, IndicatorTally>> indicatorTallies, String indicatorKey) {
        return AggregationUtil.getStaticIndicatorCount(indicatorTallies, indicatorKey);
    }

    public static long getLatestCountBasedOnDate(List<Map<String, IndicatorTally>> indicatorTallies, String indicatorKey) {
        return AggregationUtil.getLatestIndicatorCount(indicatorTallies, indicatorKey);
    }


    public static View getIndicatorView(ReportingIndicatorVisualization reportingIndicatorVisualization,
                                        IndicatorVisualisationFactory visualisationFactory, Context context) {
        return visualisationFactory.getIndicatorView(reportingIndicatorVisualization, context);
    }

    public static IndicatorModel getIndicatorModel(IndicatorView.CountType countType, String indicatorCode,
                                                   int labelResource, List<Map<String, IndicatorTally>> indicatorTallies) {
        long count = 0;
        if (countType == IndicatorView.CountType.STATIC_COUNT) {
            count = getTotalStaticCount(indicatorTallies, indicatorCode);
        } else if ((countType == IndicatorView.CountType.LATEST_COUNT)) {
            count = getLatestCountBasedOnDate(indicatorTallies, indicatorCode);
        }
        return new IndicatorModel(countType, indicatorCode, labelResource, count);
    }

    public static PieChartViewModel getPieChartViewModel(IndicatorModel yesPart, IndicatorModel noPart,
                                                         @Nullable String indicatorLabel, @Nullable String indicatorNote) {
        return new PieChartViewModel(yesPart, noPart, indicatorLabel, indicatorNote);
    }
}
