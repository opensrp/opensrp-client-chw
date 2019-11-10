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

    /**
     * Create and add indicator chart and numeric visualisations in dashboard
     * @param mainLayout view holding the visualisations being displayed
     * @param indicatorTallies list containing indicator counts for display
     */
    public static void showIndicatorVisualisations(ViewGroup mainLayout, List<Map<String, IndicatorTally>> indicatorTallies) {
        // Display order as determined in https://docs.google.com/spreadsheets/d/1q9YiWqjLiToTd0--Q8CbwhBwwcNDspbDxVrUfDm8VGU/edit#gid=315573423

        NumericDisplayModel indicator1 = ReportingUtil.getIndicatorDisplayModel(ReportContract.IndicatorView.CountType.TOTAL_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildrenUnder5, R.string.total_under_5_children_label, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(mainLayout.getContext(), indicator1).createView());
        createAncReportViews(mainLayout, indicatorTallies);
        createPncReportViews(mainLayout, indicatorTallies);

        PieChartSlice indicator2_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren_0_24UptoDateVaccinations, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice indicator2_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren_0_24OverdueVaccinations, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(indicator2_1, indicator2_2), R.string.children_0_24_months_upto_date_vaccinations, R.string.opv_0_not_included)).createView());

        PieChartSlice pnc_indicator_4_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_BABIES_RECEIVED_BGC_ON_TIME, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice pnc_indicator_4_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_BABIES_DID_NOT_RECEIVE_BGC_ON_TIME, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(pnc_indicator_4_1, pnc_indicator_4_2), R.string.pnc_indicator_4, R.string.pnc_indicator_4_note)).createView());

        createChildReportViews(mainLayout, indicatorTallies);

        NumericDisplayModel pnc_indicator_9 = ReportingUtil.getIndicatorDisplayModel(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_DECEASED_NEWBORNS_0_28, R.string.pnc_indicator_9, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(mainLayout.getContext(), pnc_indicator_9).createView());

        NumericDisplayModel indicator8 = ReportingUtil.getIndicatorDisplayModel(ReportContract.IndicatorView.CountType.TOTAL_COUNT, ReportingConstants.ChildIndicatorKeys.deceasedChildren0_11Months, R.string.deceased_children_0_11_months, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(mainLayout.getContext(), indicator8).createView());

        NumericDisplayModel indicator9 = ReportingUtil.getIndicatorDisplayModel(ReportContract.IndicatorView.CountType.TOTAL_COUNT, ReportingConstants.ChildIndicatorKeys.deceasedChildren12_59Months, R.string.deceased_children_12_59_months, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(mainLayout.getContext(), indicator9).createView());

        PieChartSlice pnc_indicator_5_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_OTHER_WRA_DEATHS, mainLayout.getContext().getResources().getString(R.string.other_wra_deaths_slice_label), mainLayout.getContext().getResources().getColor(R.color.black), indicatorTallies);
        PieChartSlice pnc_indicator_5_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_MATERNAL_DEATHS, mainLayout.getContext().getResources().getString(R.string.maternal_deaths_slice_label), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(pnc_indicator_5_1, pnc_indicator_5_2), R.string.pnc_indicator_5, R.string.pnc_indicator_5_note)).createView());

    }

    public static void createPncReportViews(ViewGroup mainLayout, List<Map<String, IndicatorTally>> indicatorTallies) {
        PieChartSlice pnc_indicator_1_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_WOMEN_DELIVERED_IN_HF, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice pnc_indicator_1_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_WOMEN_DELIVERED_ELSEWHERE, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(pnc_indicator_1_1, pnc_indicator_1_2), R.string.pnc_indicator_1, null)).createView());

        PieChartSlice pnc_indicator_2_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_BABIES_NORMAL_BIRTHWEIGHT, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice pnc_indicator_2_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_BABIES_LOW_BIRTHWEIGHT, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(pnc_indicator_2_1, pnc_indicator_2_2), R.string.pnc_indicator_2, null)).createView());

        PieChartSlice pnc_indicator_3_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_BABIES_INITIATED_EARLY_BREASTFEEDING, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice pnc_indicator_3_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_BABIES_NOT_INITIATED_EARLY_BREASTFEEDING, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(pnc_indicator_3_1, pnc_indicator_3_2), R.string.pnc_indicator_3, null)).createView());

        PieChartSlice pnc_indicator_6_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_NEWBORNS_WITH_NO_DANGER_SIGNS, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice pnc_indicator_6_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_NEWBORNS_WITH_DANGER_SIGNS, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(pnc_indicator_6_1, pnc_indicator_6_2), R.string.pnc_indicator_7, null)).createView());

        PieChartSlice pnc_indicator_7_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_WOMEN_WITH_NO_DANGER_SIGNS, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice pnc_indicator_7_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_WOMEN_WITH_DANGER_SIGNS, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(pnc_indicator_7_1, pnc_indicator_7_2), R.string.pnc_indicator_6, null)).createView());

        PieChartSlice pnc_indicator_8_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_POSTPARTUM_FP_METHOD_NONE, mainLayout.getContext().getResources().getString(R.string.none), mainLayout.getContext().getResources().getColor(R.color.pie_chart_red), indicatorTallies);
        PieChartSlice pnc_indicator_8_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_POSTPARTUM_FP_METHOD_ABSTINENCE, mainLayout.getContext().getResources().getString(R.string.abstinence), mainLayout.getContext().getResources().getColor(R.color.pie_chart_blue), indicatorTallies);
        PieChartSlice pnc_indicator_8_3 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_POSTPARTUM_FP_METHOD_CONDOM, mainLayout.getContext().getResources().getString(R.string.condom), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yellow), indicatorTallies);
        PieChartSlice pnc_indicator_8_4 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_POSTPARTUM_FP_METHOD_TABLETS, mainLayout.getContext().getResources().getString(R.string.tablets), mainLayout.getContext().getResources().getColor(R.color.pie_chart_green), indicatorTallies);
        PieChartSlice pnc_indicator_8_5 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_POSTPARTUM_FP_METHOD_INJECTABLE, mainLayout.getContext().getResources().getString(R.string.injectable), mainLayout.getContext().getResources().getColor(R.color.pie_chart_purple), indicatorTallies);
        PieChartSlice pnc_indicator_8_6 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_POSTPARTUM_FP_METHOD_IUD, mainLayout.getContext().getResources().getString(R.string.iud), mainLayout.getContext().getResources().getColor(R.color.pie_chart_orange), indicatorTallies);
        PieChartSlice pnc_indicator_8_7 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_POSTPARTUM_FP_METHOD_IMPLANT, mainLayout.getContext().getResources().getString(R.string.implant), mainLayout.getContext().getResources().getColor(R.color.pie_chart_brown), indicatorTallies);
        PieChartSlice pnc_indicator_8_8 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.PncIndicatorKeysHelper.COUNT_POSTPARTUM_FP_METHOD_OTHER, mainLayout.getContext().getResources().getString(R.string.other), mainLayout.getContext().getResources().getColor(R.color.black), indicatorTallies);
        List<PieChartSlice> slices = ReportingUtil.addPieChartSlices(pnc_indicator_8_1, pnc_indicator_8_2, pnc_indicator_8_3, pnc_indicator_8_4, pnc_indicator_8_5, pnc_indicator_8_6, pnc_indicator_8_7, pnc_indicator_8_8);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(slices, R.string.pnc_indicator_8, null)).createView());
    }


    public static void createAncReportViews(ViewGroup mainLayout, List<Map<String, IndicatorTally>> indicatorTallies) {
        //Disclaimer: Pie charts have binary slices yes and no with different tallying done separately ;)
        NumericDisplayModel anc_indicator1 = ReportingUtil.getIndicatorDisplayModel(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WRA, R.string.anc_indicator1, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(mainLayout.getContext(), anc_indicator1).createView());

        NumericDisplayModel anc_indicator2 = ReportingUtil.getIndicatorDisplayModel(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_PREGNANT_WOMEN, R.string.anc_indicator2, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(mainLayout.getContext(), anc_indicator2).createView());

        PieChartSlice anc_indicator3_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_DUE_HOME_VISIT, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice anc_indicator3_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_OVERDUE_HOME_VISIT, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(anc_indicator3_1, anc_indicator3_2), R.string.anc_indicator3, null)).createView());

        PieChartSlice anc_indicator4_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_DUE_HEALTH_FACILITY_VISIT, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice anc_indicator4_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_OVERDUE_HEALTH_FACILITY_VISIT, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(anc_indicator4_1, anc_indicator4_2), R.string.anc_indicator4, null)).createView());

        PieChartSlice anc_indicator5_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_TESTED_HIV, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice anc_indicator5_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_NOT_TESTED_HIV, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(anc_indicator5_1, anc_indicator5_2), R.string.anc_indicator5, null)).createView());

        PieChartSlice anc_indicator6_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_TESTED_SYPHILIS, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice anc_indicator6_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_NOT_TESTED_SYPHILIS, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(anc_indicator6_1, anc_indicator6_2), R.string.anc_indicator6, null)).createView());

        PieChartSlice anc_indicator7_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_DUE_TT_IMMUNIZATION, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice anc_indicator7_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_OVERDUE_TT_IMMUNIZATION, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(anc_indicator7_1, anc_indicator7_2), R.string.anc_indicator7, null)).createView());

        PieChartSlice anc_indicator8_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_DUE_IPTPSP, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice anc_indicator8_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_OVERDUE_IPTPSP, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(anc_indicator8_1, anc_indicator8_2), R.string.anc_indicator8, null)).createView());
    }

    public static void createChildReportViews(ViewGroup mainLayout, List<Map<String, IndicatorTally>> indicatorTallies) {
        //Disclaimer: Pie charts have binary slices yes and no with different tallying done separately ;)

        PieChartSlice indicator7_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.TOTAL_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren0_59WithBirthCert, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice indicator7_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.TOTAL_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren0_59WithNoBirthCert, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(indicator7_1, indicator7_2), R.string.children_0_59_months_with_birth_certificate, null)).createView());

        PieChartSlice indicator3_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren0_5ExclusivelyBreastfeeding, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice indicator3_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren0_5NotExclusivelyBreastfeeding, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(indicator3_1, indicator3_2), R.string.children_0_5_months_exclusively_breastfeeding, null)).createView());

        PieChartSlice indicator4_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren6_59VitaminReceivedA, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice indicator4_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren6_59VitaminNotReceivedA, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(indicator4_1, indicator4_2), R.string.children_6_59_months_received_vitamin_A, null)).createView());

        PieChartSlice indicator5_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren12_59Dewormed, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice indicator5_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren12_59NotDewormed, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(indicator5_1, indicator5_2), R.string.children_12_59_months_dewormed, null)).createView());

        PieChartSlice indicator6_1 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren_6_23UptoDateMNP, mainLayout.getContext().getResources().getString(R.string.yes), mainLayout.getContext().getResources().getColor(R.color.pie_chart_yes_green), indicatorTallies);
        PieChartSlice indicator6_2 = ReportingUtil.getPieChartSlice(ReportContract.IndicatorView.CountType.LATEST_COUNT, ReportingConstants.ChildIndicatorKeys.countOfChildren_6_23OverdueMNP, mainLayout.getContext().getResources().getString(R.string.no), mainLayout.getContext().getResources().getColor(R.color.pie_chart_no_red), indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), ReportingUtil.getPieChartDisplayModel(ReportingUtil.addPieChartSlices(indicator6_1, indicator6_2), R.string.children_6_23_months_upto_date_mnp, null)).createView());
    }
}
