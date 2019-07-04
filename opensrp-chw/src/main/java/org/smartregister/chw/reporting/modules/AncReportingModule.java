package org.smartregister.chw.reporting.modules;

import android.view.ViewGroup;

import org.smartregister.reporting.domain.IndicatorTally;

import java.util.List;
import java.util.Map;

public class AncReportingModule implements ReportingModule {

    private List<Map<String, IndicatorTally>> indicatorTallies;

    @Override
    public void createReport(ViewGroup mainLayout) {

    }

    public List<Map<String, IndicatorTally>> getIndicatorTallies() {
        return indicatorTallies;
    }

    public void setIndicatorTallies(List<Map<String, IndicatorTally>> indicatorTallies) {
        this.indicatorTallies = indicatorTallies;
    }
}
