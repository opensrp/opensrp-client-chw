package org.smartregister.chw.reporting.modules;

import android.view.ViewGroup;

import org.smartregister.chw.R;
import org.smartregister.chw.reporting.models.IndicatorModel;
import org.smartregister.chw.reporting.views.NumericIndicatorView;
import org.smartregister.chw.reporting.views.PieChartIndicatorView;
import org.smartregister.reporting.domain.IndicatorTally;

import java.util.List;
import java.util.Map;

import static org.smartregister.chw.reporting.ReportingUtil.getIndicatorModel;
import static org.smartregister.chw.reporting.ReportingUtil.getPieChartViewModel;
import static org.smartregister.chw.reporting.views.IndicatorView.CountType.LATEST_COUNT;
import static org.smartregister.chw.reporting.views.IndicatorView.CountType.STATIC_COUNT;
import static org.smartregister.chw.reporting.views.IndicatorViewFactory.createView;

public class ChildReportingModule implements ReportingModule {

    private final static String countOfChildrenUnder5 = "CHW_001";
    private final static String deceasedChildren0_11Months = "CHW_002";
    private final static String deceasedChildren12_59Months = "CHW_003";
    private final static String countOfChildren0_59WithBirthCert = "CHW_004";
    private final static String countOfChildren0_59WithNoBirthCert = "CHW_005";
    private final static String countOfChildren12_59Dewormed = "CHW_006";
    private final static String countOfChildren12_59NotDewormed = "CHW_007";
    private final static String countOfChildren6_59VitaminReceivedA = "CHW_008";
    private final static String countOfChildren6_59VitaminNotReceivedA = "CHW_009";
    private final static String countOfChildren0_5ExclusivelyBreastfeeding = "CHW_010";
    private final static String countOfChildren0_5NotExclusivelyBreastfeeding = "CHW_011";
    private final static String countOfChildren_6_23UptoDateMNP = "CHW_012";
    private final static String countOfChildren_6_23OverdueMNP = "CHW_013";
    private final static String countOfChildren_0_24UptoDateVaccinations = "CHW_014";
    private final static String countOfChildren_0_24OverdueVaccinations = "CHW_015";

    private List<Map<String, IndicatorTally>> indicatorTallies;

    @Override
    public void generateReport(ViewGroup mainLayout) {
        mainLayout.removeAllViews();
        //Disclaimer: Pie charts have binary slices yes and no with different tallying done separately ;)
        IndicatorModel indicator1 = getIndicatorModel(STATIC_COUNT, countOfChildrenUnder5, R.string.total_under_5_children_label, indicatorTallies);
        mainLayout.addView(createView(new NumericIndicatorView(mainLayout.getContext(), indicator1)));

        IndicatorModel indicator2_1 = getIndicatorModel(LATEST_COUNT, countOfChildren_0_24UptoDateVaccinations, R.string.children_0_24_months_upto_date_vaccinations, indicatorTallies);
        IndicatorModel indicator2_2 = getIndicatorModel(LATEST_COUNT, countOfChildren_0_24OverdueVaccinations, R.string.children_0_24_months_overdue_vaccinations, indicatorTallies);
        mainLayout.addView(createView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(indicator2_1, indicator2_2, null, mainLayout.getContext().getResources().getString(R.string.opv_0_not_included)))));

        IndicatorModel indicator3_1 = getIndicatorModel(LATEST_COUNT, countOfChildren0_5ExclusivelyBreastfeeding, R.string.children_0_5_months_exclusively_breastfeeding, indicatorTallies);
        IndicatorModel indicator3_2 = getIndicatorModel(LATEST_COUNT, countOfChildren0_5NotExclusivelyBreastfeeding, R.string.children_0_5_months_not_exclusively_breastfeeding, indicatorTallies);
        mainLayout.addView(createView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(indicator3_1, indicator3_2, null, null))));

        IndicatorModel indicator4_1 = getIndicatorModel(STATIC_COUNT, countOfChildren6_59VitaminReceivedA, R.string.children_6_59_months_received_vitamin_A, indicatorTallies);
        IndicatorModel indicator4_2 = getIndicatorModel(STATIC_COUNT, countOfChildren6_59VitaminNotReceivedA, R.string.children_6_59_months_not_received_vitamin_A, indicatorTallies);
        mainLayout.addView(createView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(indicator4_1, indicator4_2, null, null))));

        IndicatorModel indicator5_1 = getIndicatorModel(STATIC_COUNT, countOfChildren12_59Dewormed, R.string.children_12_59_months_dewormed, indicatorTallies);
        IndicatorModel indicator5_2 = getIndicatorModel(STATIC_COUNT, countOfChildren12_59NotDewormed, R.string.children_12_59_months_not_dewormed, indicatorTallies);
        mainLayout.addView(createView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(indicator5_1, indicator5_2, null, null))));

        IndicatorModel indicator6_1 = getIndicatorModel(LATEST_COUNT, countOfChildren_6_23UptoDateMNP, R.string.children_6_23_months_upto_date_mnp, indicatorTallies);
        IndicatorModel indicator6_2 = getIndicatorModel(LATEST_COUNT, countOfChildren_6_23OverdueMNP, R.string.children_6_23_months_overdue_mnp, indicatorTallies);
        mainLayout.addView(createView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(indicator6_1, indicator6_2, null, null))));

        IndicatorModel indicator7_1 = getIndicatorModel(STATIC_COUNT, countOfChildren0_59WithBirthCert, R.string.children_0_59_months_with_birth_certificate, indicatorTallies);
        IndicatorModel indicator7_2 = getIndicatorModel(STATIC_COUNT, countOfChildren0_59WithNoBirthCert, R.string.children_0_59_months_without_birth__certificate, indicatorTallies);
        mainLayout.addView(createView(new PieChartIndicatorView(mainLayout.getContext(), getPieChartViewModel(indicator7_1, indicator7_2, null, null))));

        IndicatorModel indicator8 = getIndicatorModel(STATIC_COUNT, deceasedChildren0_11Months, R.string.deceased_children_0_11_months, indicatorTallies);
        mainLayout.addView(createView(new NumericIndicatorView(mainLayout.getContext(), indicator8)));

        IndicatorModel indicator9 = getIndicatorModel(STATIC_COUNT, deceasedChildren12_59Months, R.string.deceased_children_12_59_months, indicatorTallies);
        mainLayout.addView(createView(new NumericIndicatorView(mainLayout.getContext(), indicator9)));

    }

    public List<Map<String, IndicatorTally>> getIndicatorTallies() {
        return indicatorTallies;
    }

    public void setIndicatorTallies(List<Map<String, IndicatorTally>> indicatorTallies) {
        this.indicatorTallies = indicatorTallies;
    }

}
