package org.smartregister.chw.reporting;

import android.view.ViewGroup;

import org.smartregister.chw.R;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.model.IndicatorDisplayModel;
import org.smartregister.reporting.view.NumericIndicatorView;
import org.smartregister.reporting.view.PieChartIndicatorView;

import java.util.List;
import java.util.Map;

import static org.smartregister.chw.util.ReportingConstants.AncIndicatorKeys.COUNT_PREGNANT_WOMEN;
import static org.smartregister.chw.util.ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_DUE_HEALTH_FACILITY_VISIT;
import static org.smartregister.chw.util.ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_DUE_HOME_VISIT;
import static org.smartregister.chw.util.ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_DUE_IPTPSP;
import static org.smartregister.chw.util.ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_DUE_TT_IMMUNIZATION;
import static org.smartregister.chw.util.ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_NOT_TESTED_HIV;
import static org.smartregister.chw.util.ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_NOT_TESTED_SYPHILIS;
import static org.smartregister.chw.util.ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_OVERDUE_HEALTH_FACILITY_VISIT;
import static org.smartregister.chw.util.ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_OVERDUE_HOME_VISIT;
import static org.smartregister.chw.util.ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_OVERDUE_IPTPSP;
import static org.smartregister.chw.util.ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_OVERDUE_TT_IMMUNIZATION;
import static org.smartregister.chw.util.ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_TESTED_HIV;
import static org.smartregister.chw.util.ReportingConstants.AncIndicatorKeys.COUNT_WOMEN_TESTED_SYPHILIS;
import static org.smartregister.chw.util.ReportingConstants.AncIndicatorKeys.COUNT_WRA;
import static org.smartregister.chw.util.ReportingConstants.ChildIndicatorKeys.countOfChildren0_59WithBirthCert;
import static org.smartregister.chw.util.ReportingConstants.ChildIndicatorKeys.countOfChildren0_59WithNoBirthCert;
import static org.smartregister.chw.util.ReportingConstants.ChildIndicatorKeys.countOfChildren0_5ExclusivelyBreastfeeding;
import static org.smartregister.chw.util.ReportingConstants.ChildIndicatorKeys.countOfChildren0_5NotExclusivelyBreastfeeding;
import static org.smartregister.chw.util.ReportingConstants.ChildIndicatorKeys.countOfChildren12_59Dewormed;
import static org.smartregister.chw.util.ReportingConstants.ChildIndicatorKeys.countOfChildren12_59NotDewormed;
import static org.smartregister.chw.util.ReportingConstants.ChildIndicatorKeys.countOfChildren6_59VitaminNotReceivedA;
import static org.smartregister.chw.util.ReportingConstants.ChildIndicatorKeys.countOfChildren6_59VitaminReceivedA;
import static org.smartregister.chw.util.ReportingConstants.ChildIndicatorKeys.countOfChildrenUnder5;
import static org.smartregister.chw.util.ReportingConstants.ChildIndicatorKeys.countOfChildren_0_24OverdueVaccinations;
import static org.smartregister.chw.util.ReportingConstants.ChildIndicatorKeys.countOfChildren_0_24UptoDateVaccinations;
import static org.smartregister.chw.util.ReportingConstants.ChildIndicatorKeys.countOfChildren_6_23OverdueMNP;
import static org.smartregister.chw.util.ReportingConstants.ChildIndicatorKeys.countOfChildren_6_23UptoDateMNP;
import static org.smartregister.chw.util.ReportingConstants.ChildIndicatorKeys.deceasedChildren0_11Months;
import static org.smartregister.chw.util.ReportingConstants.ChildIndicatorKeys.deceasedChildren12_59Months;
import static org.smartregister.reporting.contract.ReportContract.IndicatorView.CountType.LATEST_COUNT;
import static org.smartregister.reporting.contract.ReportContract.IndicatorView.CountType.STATIC_COUNT;
import static org.smartregister.reporting.util.ReportingUtil.getIndicatorModel;
import static org.smartregister.reporting.util.ReportingUtil.getPieChartViewModel;

public class ChwReport {


    public static void createAncReportViews(ViewGroup mainLayout, List<Map<String, IndicatorTally>> indicatorTallies) {
        //Disclaimer: Pie charts have binary slices yes and no with different tallying done separately ;)
        IndicatorDisplayModel anc_indicator1 = getIndicatorModel(LATEST_COUNT, COUNT_WRA, R.string.anc_indicator1, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(mainLayout.getContext(), anc_indicator1).createView());

        IndicatorDisplayModel anc_indicator2 = getIndicatorModel(LATEST_COUNT, COUNT_PREGNANT_WOMEN, R.string.anc_indicator2, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(mainLayout.getContext(), anc_indicator2).createView());

        IndicatorDisplayModel anc_indicator3_1 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_DUE_HOME_VISIT, R.string.anc_indicator3, indicatorTallies);
        IndicatorDisplayModel anc_indicator3_2 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_OVERDUE_HOME_VISIT, R.string.anc_indicator3, indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(anc_indicator3_1, anc_indicator3_2, null, null)).createView());

        IndicatorDisplayModel anc_indicator4_1 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_DUE_HEALTH_FACILITY_VISIT, R.string.anc_indicator4, indicatorTallies);
        IndicatorDisplayModel anc_indicator4_2 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_OVERDUE_HEALTH_FACILITY_VISIT, R.string.anc_indicator4, indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(anc_indicator4_1, anc_indicator4_2, null, null)).createView());

        IndicatorDisplayModel anc_indicator5_1 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_TESTED_HIV, R.string.anc_indicator5, indicatorTallies);
        IndicatorDisplayModel anc_indicator5_2 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_NOT_TESTED_HIV, R.string.anc_indicator5, indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(anc_indicator5_1, anc_indicator5_2, null, null)).createView());

        IndicatorDisplayModel anc_indicator6_1 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_TESTED_SYPHILIS, R.string.anc_indicator6, indicatorTallies);
        IndicatorDisplayModel anc_indicator6_2 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_NOT_TESTED_SYPHILIS, R.string.anc_indicator6, indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(anc_indicator6_1, anc_indicator6_2, null, null)).createView());

        IndicatorDisplayModel anc_indicator7_1 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_DUE_TT_IMMUNIZATION, R.string.anc_indicator7, indicatorTallies);
        IndicatorDisplayModel anc_indicator7_2 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_OVERDUE_TT_IMMUNIZATION, R.string.anc_indicator7, indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(anc_indicator7_1, anc_indicator7_2, null, null)).createView());

        IndicatorDisplayModel anc_indicator8_1 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_DUE_IPTPSP, R.string.anc_indicator8, indicatorTallies);
        IndicatorDisplayModel anc_indicator8_2 = getIndicatorModel(LATEST_COUNT, COUNT_WOMEN_OVERDUE_IPTPSP, R.string.anc_indicator8, indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(anc_indicator8_1, anc_indicator8_2, null, null)).createView());
    }

    public static void createChildReportViews(ViewGroup mainLayout, List<Map<String, IndicatorTally>> indicatorTallies) {
        //Disclaimer: Pie charts have binary slices yes and no with different tallying done separately ;)
        IndicatorDisplayModel indicator1 = getIndicatorModel(STATIC_COUNT, countOfChildrenUnder5, R.string.total_under_5_children_label, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(mainLayout.getContext(), indicator1).createView());

        IndicatorDisplayModel indicator2_1 = getIndicatorModel(LATEST_COUNT, countOfChildren_0_24UptoDateVaccinations, R.string.children_0_24_months_upto_date_vaccinations, indicatorTallies);
        IndicatorDisplayModel indicator2_2 = getIndicatorModel(LATEST_COUNT, countOfChildren_0_24OverdueVaccinations, R.string.children_0_24_months_overdue_vaccinations, indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(indicator2_1, indicator2_2, null, mainLayout.getContext().getResources().getString(R.string.opv_0_not_included))).createView());

        IndicatorDisplayModel indicator3_1 = getIndicatorModel(LATEST_COUNT, countOfChildren0_5ExclusivelyBreastfeeding, R.string.children_0_5_months_exclusively_breastfeeding, indicatorTallies);
        IndicatorDisplayModel indicator3_2 = getIndicatorModel(LATEST_COUNT, countOfChildren0_5NotExclusivelyBreastfeeding, R.string.children_0_5_months_not_exclusively_breastfeeding, indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(indicator3_1, indicator3_2, null, null)).createView());

        IndicatorDisplayModel indicator4_1 = getIndicatorModel(STATIC_COUNT, countOfChildren6_59VitaminReceivedA, R.string.children_6_59_months_received_vitamin_A, indicatorTallies);
        IndicatorDisplayModel indicator4_2 = getIndicatorModel(STATIC_COUNT, countOfChildren6_59VitaminNotReceivedA, R.string.children_6_59_months_not_received_vitamin_A, indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(indicator4_1, indicator4_2, null, null)).createView());

        IndicatorDisplayModel indicator5_1 = getIndicatorModel(STATIC_COUNT, countOfChildren12_59Dewormed, R.string.children_12_59_months_dewormed, indicatorTallies);
        IndicatorDisplayModel indicator5_2 = getIndicatorModel(STATIC_COUNT, countOfChildren12_59NotDewormed, R.string.children_12_59_months_not_dewormed, indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(indicator5_1, indicator5_2, null, null)).createView());

        IndicatorDisplayModel indicator6_1 = getIndicatorModel(LATEST_COUNT, countOfChildren_6_23UptoDateMNP, R.string.children_6_23_months_upto_date_mnp, indicatorTallies);
        IndicatorDisplayModel indicator6_2 = getIndicatorModel(LATEST_COUNT, countOfChildren_6_23OverdueMNP, R.string.children_6_23_months_overdue_mnp, indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(indicator6_1, indicator6_2, null, null)).createView());

        IndicatorDisplayModel indicator7_1 = getIndicatorModel(STATIC_COUNT, countOfChildren0_59WithBirthCert, R.string.children_0_59_months_with_birth_certificate, indicatorTallies);
        IndicatorDisplayModel indicator7_2 = getIndicatorModel(STATIC_COUNT, countOfChildren0_59WithNoBirthCert, R.string.children_0_59_months_without_birth__certificate, indicatorTallies);
        mainLayout.addView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(indicator7_1, indicator7_2, null, null)).createView());

        IndicatorDisplayModel indicator8 = getIndicatorModel(STATIC_COUNT, deceasedChildren0_11Months, R.string.deceased_children_0_11_months, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(mainLayout.getContext(), indicator8).createView());

        IndicatorDisplayModel indicator9 = getIndicatorModel(STATIC_COUNT, deceasedChildren12_59Months, R.string.deceased_children_12_59_months, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(mainLayout.getContext(), indicator9).createView());
    }
}
