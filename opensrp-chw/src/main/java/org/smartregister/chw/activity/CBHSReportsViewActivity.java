package org.smartregister.chw.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.smartregister.chw.R;
import org.smartregister.chw.util.Constants;

public class CBHSReportsViewActivity extends ChwReportsViewActivity {
    public static void startMe(Activity activity, String reportPath, String reportDate) {
        Intent intent = new Intent(activity, CBHSReportsViewActivity.class);
        intent.putExtra(ARG_REPORT_PATH, reportPath);
        intent.putExtra(ARG_REPORT_DATE, reportDate);
        intent.putExtra(ARG_REPORT_TITLE, R.string.cbhs_reports_title);
        intent.putExtra(ARG_REPORT_TYPE, Constants.ReportConstants.ReportTypes.CBHS_REPORT);
        activity.startActivity(intent);
    }
}