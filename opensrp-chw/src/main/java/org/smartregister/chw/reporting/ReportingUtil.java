package org.smartregister.chw.reporting;

import android.content.Context;
import android.view.View;

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
}
