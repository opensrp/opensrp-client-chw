package org.smartregister.chw.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.domain.cbhs_reports.CbhsMonthlyReportObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.RequiresApi;
import androidx.webkit.WebViewAssetLoader;
import timber.log.Timber;

public class ReportUtils {
    private static final int year = Calendar.getInstance().get(Calendar.YEAR);
    private static final int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
    public static String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
    private static String printJobName;
    private static String reportPeriod;

    public static String getDefaultReportPeriod() {
        String monthString = String.valueOf(month);
        if (month < 10) {
            monthString = "0" + monthString;
        }
        return monthString + "-" + year;
    }

    public static int getMonth() {
        return month;
    }

    public static int getYear() {
        return year;
    }

    public static String displayMonthAndYear(int month, int year) {
        return monthNames[month] + ", " + year;
    }

    public static String displayMonthAndYear() {
        return monthNames[getMonth() - 1] + ", " + getYear();
    }

    public static String getPrintJobName() {
        return printJobName;
    }

    public static void setPrintJobName(String printJobName) {
        ReportUtils.printJobName = printJobName;
    }

    public static Date getReportDate() {
        if (StringUtils.isNotBlank(reportPeriod)) {
            try {
                return new SimpleDateFormat("MM-yyyy", Locale.getDefault()).parse(reportPeriod);
            } catch (ParseException e) {
                Timber.e(e);
            }
        }

        return new Date();
    }

    public static String getReportPeriod() {
        return reportPeriod;
    }

    public static void setReportPeriod(String reportPeriod) {
        ReportUtils.reportPeriod = reportPeriod;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void printTheWebPage(WebView webView, Context context) {

        // Creating  PrintManager instance
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(getPrintJobName());

        // Create a print job with name and adapter instance
        assert printManager != null;
        printManager.print(getPrintJobName(), printAdapter,
                new PrintAttributes.Builder().build());
    }

    @SuppressLint("SetJavaScriptEnabled")
    public static void loadReportView(String reportPath, WebView mWebView, Context context, String reportType) {

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(context))
                .build();
        mWebView.setWebViewClient(new LocalContentWebViewClient(assetLoader));
        mWebView.addJavascriptInterface(new ChwWebAppInterface(context, reportType), "Android");
        mWebView.loadUrl("https://appassets.androidplatform.net/assets/reports/" + reportPath + ".html");
    }


    public static class CBHSReport {
        public static String computeReport(Date now) {
            String report = "";
            CbhsMonthlyReportObject cbhsMonthlyReportObject = new CbhsMonthlyReportObject(now);
            try {
                report = cbhsMonthlyReportObject.getIndicatorDataAsGson(cbhsMonthlyReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }


}
