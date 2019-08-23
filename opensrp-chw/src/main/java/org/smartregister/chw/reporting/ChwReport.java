package org.smartregister.chw.reporting;

import android.view.ViewGroup;

import org.smartregister.chw.R;
import org.smartregister.chw.util.ReportingConstants;
import org.smartregister.reporting.contract.ReportContract;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.domain.PieChartSlice;
import org.smartregister.reporting.model.NumericDisplayModel;
import org.smartregister.reporting.util.ReportingUtil;
import org.smartregister.reporting.view.NumericIndicatorView;
import org.smartregister.reporting.view.PieChartIndicatorView;

import java.util.List;
import java.util.Map;

public class ChwReport {


    public static void createAncReportViews(ViewGroup mainLayout, List<Map<String, IndicatorTally>> indicatorTallies) {
        //Disclaimer: Pie charts have binary slices yes and no with different tallying done separately ;)
        NumericDisplayModel anc_indicator1 = ReportingUtil.getIndicatorDisplayModel(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WRA, R.string.anc_indicator1, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(mainLayout.getContext(), anc_indicator1).createView());

        NumericDisplayModel anc_indicator2 = ReportingUtil.getIndicatorDisplayModel(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_PREGNANT_WOMEN, R.string.anc_indicator2, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(mainLayout.getContext(), anc_indicator2).createView());

        PieChartSlice anc_indicator3_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_DUE_HOME_VISIT, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes), indicatorTallies);
        PieChartSlice anc_indicator3_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_OVERDUE_HOME_VISIT, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(anc_indicator3_1, anc_indicator3_2), R.string.anc_indicator3, null)).createView());

        PieChartSlice anc_indicator4_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_DUE_HEALTH_FACILITY_VISIT, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes), indicatorTallies);
        PieChartSlice anc_indicator4_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_OVERDUE_HEALTH_FACILITY_VISIT, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(anc_indicator4_1, anc_indicator4_2), R.string.anc_indicator4, null)).createView());

        PieChartSlice anc_indicator5_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_TESTED_HIV, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes), indicatorTallies);
        PieChartSlice anc_indicator5_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_NOT_TESTED_HIV, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(anc_indicator5_1, anc_indicator5_2), R.string.anc_indicator5, null)).createView());

        PieChartSlice anc_indicator6_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_TESTED_SYPHILIS, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes), indicatorTallies);
        PieChartSlice anc_indicator6_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_NOT_TESTED_SYPHILIS, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(anc_indicator6_1, anc_indicator6_2), R.string.anc_indicator6, null)).createView());

        PieChartSlice anc_indicator7_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_DUE_TT_IMMUNIZATION, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes), indicatorTallies);
        PieChartSlice anc_indicator7_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_OVERDUE_TT_IMMUNIZATION, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(anc_indicator7_1, anc_indicator7_2), R.string.anc_indicator7, null)).createView());

        PieChartSlice anc_indicator8_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_DUE_IPTPSP, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes), indicatorTallies);
        PieChartSlice anc_indicator8_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_OVERDUE_IPTPSP, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(anc_indicator8_1, anc_indicator8_2), R.string.anc_indicator8, null)).createView());
    }

    public static void createChildReportViews(ViewGroup mainLayout, List<Map<String, IndicatorTally>> indicatorTallies) {
        //Disclaimer: Pie charts have binary slices yes and no with different tallying done separately ;)
        NumericDisplayModel indicator1 = ReportingUtil.getIndicatorDisplayModel(ReportContract.IndicatorView.CountType.TOTAL_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildrenUnder5, R.string.total_under_5_children_label, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(mainLayout.getContext(), indicator1).createView());

        PieChartSlice indicator2_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren_0_24UptoDateVaccinations, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes), indicatorTallies);
        PieChartSlice indicator2_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren_0_24OverdueVaccinations, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(indicator2_1, indicator2_2), R.string.children_0_24_months_upto_date_vaccinations, R.string.opv_0_not_included)).createView());

        PieChartSlice indicator3_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren0_5ExclusivelyBreastfeeding, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes), indicatorTallies);
        PieChartSlice indicator3_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren0_5NotExclusivelyBreastfeeding, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(indicator3_1, indicator3_2), R.string.children_0_5_months_exclusively_breastfeeding, null)).createView());

        PieChartSlice indicator4_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.TOTAL_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren6_59VitaminReceivedA, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes), indicatorTallies);
        PieChartSlice indicator4_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.TOTAL_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren6_59VitaminNotReceivedA, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(indicator4_1, indicator4_2), R.string.children_6_59_months_received_vitamin_A, null)).createView());

        PieChartSlice indicator5_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.TOTAL_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren12_59Dewormed, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes), indicatorTallies);
        PieChartSlice indicator5_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.TOTAL_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren12_59NotDewormed, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(indicator5_1, indicator5_2), R.string.children_12_59_months_dewormed, null)).createView());

        PieChartSlice indicator6_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren_6_23UptoDateMNP, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes), indicatorTallies);
        PieChartSlice indicator6_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren_6_23OverdueMNP, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(indicator6_1, indicator6_2), R.string.children_6_23_months_upto_date_mnp, null)).createView());

        PieChartSlice indicator7_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.TOTAL_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren0_59WithBirthCert, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes), indicatorTallies);
        PieChartSlice indicator7_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.TOTAL_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren0_59WithNoBirthCert, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(indicator7_1, indicator7_2), R.string.children_0_59_months_with_birth_certificate, null)).createView());

        NumericDisplayModel indicator8 = ReportingUtil.getIndicatorDisplayModel(ReportContract.IndicatorView.CountType.TOTAL_COUNT, ReportingConstants.ChildIndicatorKeys.deceasedChildren0_11Months, R.string.deceased_children_0_11_months, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(mainLayout.getContext(), indicator8).createView());

        NumericDisplayModel indicator9 = ReportingUtil.getIndicatorDisplayModel(ReportContract.IndicatorView.CountType.TOTAL_COUNT, ReportingConstants.ChildIndicatorKeys.deceasedChildren12_59Months, R.string.deceased_children_12_59_months, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(mainLayout.getContext(), indicator9).createView());
    }
}
