package org.smartregister.chw.reporting.modules;

import android.view.ViewGroup;

import org.smartregister.reporting.domain.IndicatorTally;

import java.util.List;
import java.util.Map;

public interface ReportingModule {
    void generateReport(ViewGroup mainLayout);
    void setIndicatorTallies(List<Map<String, IndicatorTally>> indicatorTallies);
    List<Map<String, IndicatorTally>> getIndicatorTallies();
}
