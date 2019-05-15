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

    // Indicator keys which should match values provided in indicator-definitions.yml file
    public static String countOfChildrenUnder5 = "CHW_001";
    public static String deceasedChildren0_11Months = "CHW_002";
    public static String deceasedChildren12_59Months = "CHW_003";
    public static String countOfChildren0_59WithBirthCert = "CHW_004";
    public static String countOfChildren0_59WithNoBirthCert = "CHW_005";

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
