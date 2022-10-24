package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.R;
import org.smartregister.chw.util.Constants;


public class AGYWReportsViewActivity extends ChwReportsViewActivity{
    public static void startMe(Activity activity, String reportPath, String reportDate) {
        Intent intent = new Intent(activity, AGYWReportsViewActivity.class);
        intent.putExtra(ARG_REPORT_PATH, reportPath);
        intent.putExtra(ARG_REPORT_DATE, reportDate);
        intent.putExtra(ARG_REPORT_TITLE, R.string.agyw_reports);
        intent.putExtra(ARG_REPORT_TYPE, Constants.ReportConstants.ReportTypes.AGYW_REPORT);
        activity.startActivity(intent);
    }
}
