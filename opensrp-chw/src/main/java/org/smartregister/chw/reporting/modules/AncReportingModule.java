package org.smartregister.chw.reporting.modules;

import android.view.ViewGroup;

import org.smartregister.chw.R;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.impl.ReportingModule;
import org.smartregister.reporting.impl.models.IndicatorModel;
import org.smartregister.reporting.impl.views.NumericIndicatorView;
import org.smartregister.reporting.impl.views.PieChartIndicatorView;

import java.util.List;
import java.util.Map;

import static org.smartregister.reporting.impl.ReportingUtil.getIndicatorModel;
import static org.smartregister.reporting.impl.ReportingUtil.getPieChartViewModel;
import static org.smartregister.reporting.impl.views.IndicatorView.CountType.LATEST_COUNT;
import static org.smartregister.reporting.impl.views.IndicatorViewFactory.createView;

public class AncReportingModule implements ReportingModule {

    private static final String COUNT_WRA = "anc_report_indicator_1";
    private static final String COUNT_PREGNANT_WOMEN = "anc_report_indicator_2";
    private static final String COUNT_WOMEN_DUE_HOME_VISIT = "anc_report_indicator_3_1";
    private static final String COUNT_WOMEN_OVERDUE_HOME_VISIT = "anc_report_indicator_3_2";
    private static final String COUNT_WOMEN_DUE_HEALTH_FACILITY_VISIT = "anc_report_indicator_4_1";
    private static final String COUNT_WOMEN_OVERDUE_HEALTH_FACILITY_VISIT = "anc_report_indicator_4_2";
    private static final String COUNT_WOMEN_TESTED_HIV = "anc_report_indicator_5_1";
    private static final String COUNT_WOMEN_NOT_TESTED_HIV = "anc_report_indicator_5_2";
    private static final String COUNT_WOMEN_TESTED_SYPHILIS = "anc_report_indicator_6_1";
    private static final String COUNT_WOMEN_NOT_TESTED_SYPHILIS = "anc_report_indicator_6_2";
    private static final String COUNT_WOMEN_DUE_TT_IMMUNIZATION = "anc_report_indicator_7_1";
    private static final String COUNT_WOMEN_OVERDUE_TT_IMMUNIZATION = "anc_report_indicator_7_2";
    private static final String COUNT_WOMEN_DUE_IPTPSP = "anc_report_indicator_8_1";
    private static final String COUNT_WOMEN_OVERDUE_IPTPSP = "anc_report_indicator_8_2";

    private List<Map<String, IndicatorTally>> indicatorTallies;

    @Override
    public void generateReport(ViewGroup mainLayout) {
        //Disclaimer: Pie charts have binary slices yes and no with different tallying done separately ;)
        IndicatorModel anc_indicator1 = getIndicatorModel(LATEST_COUNT, COUNT_WRA, R.string.anc_indicator1, indicatorTallies);
        mainLayout.addView(createView(new NumericIndicatorView(mainLayout.getContext(), anc_indicator1)));

        IndicatorModel anc_indicator2 = getIndicatorModel(LATEST_COUNT, COUNT_PREGNANT_WOMEN, R.string.anc_indicator2, indicatorTallies);
        mainLayout.addView(createView(new NumericIndicatorView(mainLayout.getContext(), anc_indicator2)));

        IndicatorModel anc_indicator3_1 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_DUE_HOME_VISIT, R.string.anc_indicator3, indicatorTallies);
        IndicatorModel anc_indicator3_2 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_OVERDUE_HOME_VISIT, R.string.anc_indicator3, indicatorTallies);
        mainLayout.addView(createView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(anc_indicator3_1, anc_indicator3_2, null, null))));

        IndicatorModel anc_indicator4_1 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_DUE_HEALTH_FACILITY_VISIT, R.string.anc_indicator4, indicatorTallies);
        IndicatorModel anc_indicator4_2 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_OVERDUE_HEALTH_FACILITY_VISIT, R.string.anc_indicator4, indicatorTallies);
        mainLayout.addView(createView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(anc_indicator4_1, anc_indicator4_2, null, null))));

        IndicatorModel anc_indicator5_1 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_TESTED_HIV, R.string.anc_indicator5, indicatorTallies);
        IndicatorModel anc_indicator5_2 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_NOT_TESTED_HIV, R.string.anc_indicator5, indicatorTallies);
        mainLayout.addView(createView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(anc_indicator5_1, anc_indicator5_2, null, null))));

        IndicatorModel anc_indicator6_1 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_TESTED_SYPHILIS, R.string.anc_indicator6, indicatorTallies);
        IndicatorModel anc_indicator6_2 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_NOT_TESTED_SYPHILIS, R.string.anc_indicator6, indicatorTallies);
        mainLayout.addView(createView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(anc_indicator6_1, anc_indicator6_2, null, null))));

        IndicatorModel anc_indicator7_1 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_DUE_TT_IMMUNIZATION, R.string.anc_indicator7, indicatorTallies);
        IndicatorModel anc_indicator7_2 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_OVERDUE_TT_IMMUNIZATION, R.string.anc_indicator7, indicatorTallies);
        mainLayout.addView(createView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(anc_indicator7_1, anc_indicator7_2, null, null))));

        IndicatorModel anc_indicator8_1 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_DUE_IPTPSP, R.string.anc_indicator8, indicatorTallies);
        IndicatorModel anc_indicator8_2 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_OVERDUE_IPTPSP, R.string.anc_indicator8, indicatorTallies);
        mainLayout.addView(createView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(anc_indicator8_1, anc_indicator8_2, null, null))));

    }

    public List<Map<String, IndicatorTally>> getIndicatorTallies() {
        return indicatorTallies;
    }

    public void setIndicatorTallies(List<Map<String, IndicatorTally>> indicatorTallies) {
        this.indicatorTallies = indicatorTallies;
    }
}
