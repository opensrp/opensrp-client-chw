package org.smartregister.chw.util;

import android.content.Context;
import android.webkit.JavascriptInterface;

import static org.smartregister.util.Utils.getAllSharedPreferences;

public class ChwWebAppInterface {
    Context mContext;

    String reportType;


    public ChwWebAppInterface(Context c, String reportType) {
        mContext = c;
        this.reportType = reportType;
    }

    @JavascriptInterface
    public String getData(String reportKey) {
        if (reportType.equalsIgnoreCase(Constants.ReportConstants.ReportTypes.CBHS_REPORT)) {
            ReportUtils.setPrintJobName("cbhs_monthly_summary-" + ReportUtils.getReportPeriod() + ".pdf");
            return ReportUtils.CBHSReport.computeReport(ReportUtils.getReportDate());
        }


        return "";
    }

    @JavascriptInterface
    public String getDataPeriod() {
        return ReportUtils.getReportPeriod();
    }

    @JavascriptInterface
    public String getReportingFacility() {
        return getAllSharedPreferences().fetchCurrentLocality();
    }
}
