package org.smartregister.chw.presenter;

import org.smartregister.reporting.contract.ReportContract;
import org.smartregister.reporting.domain.BaseReportIndicatorsModel;
import org.smartregister.reporting.domain.IndicatorTally;

import java.util.List;
import java.util.Map;


public class SuperVisorDashboardFragmentPresenter {
    private ReportContract.Model model;

    public SuperVisorDashboardFragmentPresenter() {
        this.model = new BaseReportIndicatorsModel();
    }

    public List<Map<String, IndicatorTally>> getLatestIndicatorTallies() {
        return model.getLatestIndicatorTallies();
    }

}
