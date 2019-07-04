package org.smartregister.chw.util;

import android.content.Context;
import android.graphics.Color;

import org.smartregister.chw.R;
import org.smartregister.reporting.domain.PieChartSlice;

/**
 * The Dashboard provides util functions to work with the dashboard visualizations
 *
 * @author allan
 */
public class DashboardUtil {

    // Indicator keys which should match values provided in child-reporting-indicator-definitions.yml file
    public final static String countOfChildrenUnder5 = "CHW_001";
    public final static String deceasedChildren0_11Months = "CHW_002";
    public final static String deceasedChildren12_59Months = "CHW_003";
    public final static String countOfChildren0_59WithBirthCert = "CHW_004";
    public final static String countOfChildren0_59WithNoBirthCert = "CHW_005";
    public final static String countOfChildren12_59Dewormed = "CHW_006";
    public final static String countOfChildren12_59NotDewormed = "CHW_007";
    public final static String countOfChildren6_59VitaminReceivedA = "CHW_008";
    public final static String countOfChildren6_59VitaminNotReceivedA = "CHW_009";
    public final static String countOfChildren0_5ExclusivelyBreastfeeding = "CHW_010";
    public final static String countOfChildren0_5NotExclusivelyBreastfeeding = "CHW_011";
    public final static String countOfChildren_6_23UptoDateMNP = "CHW_012";
    public final static String countOfChildren_6_23OverdueMNP = "CHW_013";
    public final static String countOfChildren_0_24UptoDateVaccinations = "CHW_014";
    public final static String countOfChildren_0_24OverdueVaccinations = "CHW_015";

    public static final class AncReportingIndicators {
        public static final String COUNT_WRA = "anc_report_indicator_1";
        public static final String COUNT_PREGNANT_WOMEN = "anc_report_indicator_2";
        public static final String COUNT_WOMEN_DUE_HOME_VISIT = "anc_report_indicator_3_1";
        public static final String COUNT_WOMEN_OVERDUE_HOME_VISIT = "anc_report_indicator_3_2";
        public static final String COUNT_WOMEN_DUE_HEALTH_FACILITY_VISIT = "anc_report_indicator_4_1";
        public static final String COUNT_WOMEN_OVERDUE_HEALTH_FACILITY_VISIT = "anc_report_indicator_4_2";
        public static final String COUNT_WOMEN_TESTED_HIV = "anc_report_indicator_5_1";
        public static final String COUNT_WOMEN_NOT_TESTED_HIV = "anc_report_indicator_5_2";
        public static final String COUNT_WOMEN_TESTED_SYPHILIS = "anc_report_indicator_6_1";
        public static final String COUNT_WOMEN_NOT_TESTED_SYPHILIS= "anc_report_indicator_6_2";
        public static final String COUNT_WOMEN_DUE_TT_IMMUNIZATION = "anc_report_indicator_7_1";
        public static final String COUNT_WOMEN_OVERDUE_TT_IMMUNIZATION = "anc_report_indicator_7_2";
        public static final String COUNT_WOMEN_DUE_IPTPSP = "anc_report_indicator_8_1";
        public static final String COUNT_WOMEN_OVERDUE_IPTPSP = "anc_report_indicator_8_2";
    }

    // Color definitions for the chart slices. This could essentially be defined in colors.xml
    public static final int YES_GREEN_SLICE_COLOR = Color.parseColor("#99CC00");
    public static final int NO_RED_SLICE_COLOR = Color.parseColor("#FF4444");

    /**
     * Returns the String label for a slice.
     * This is primarily used during handling of a slice click event.
     * It would have been better to have this as part of the PieChartSlice data attributes but
     * there's no mapping for the same in the SliceValue class
     *
     * @param sliceValue the PieChartSlice selected
     * @param context    the context used to retrieve the String value from strings.xml
     * @return
     */
    public static String getPieSelectionValue(PieChartSlice sliceValue, Context context) {
        if (sliceValue.getColor() == YES_GREEN_SLICE_COLOR) {
            return context.getString(R.string.yes_slice_label);
        } else {
            return context.getString(R.string.no_slice_label);
        }
    }
}
