package org.smartregister.chw.domain.cbhs_reports;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.domain.ReportObject;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CbhsMonthlyReportObject extends ReportObject {
    private final Date reportDate;
    private List<String> indicatorCodes = new ArrayList<>();
    private final String[] indicatorCodesArray = new String[]{"Test Code"};

    public CbhsMonthlyReportObject(Date reportDate) {
        super(reportDate);
        this.reportDate = reportDate;
        setIndicatorCodes(indicatorCodes);
    }

    public void setIndicatorCodes(List<String> indicatorCodes) {
        Collections.addAll(indicatorCodes, indicatorCodesArray);
    }

    @Override
    public JSONObject getIndicatorData() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (String indicatorCode : indicatorCodesArray) {
            // jsonObject.put(indicatorCode, ReportDao.getReportPerIndicatorCode(indicatorCode, reportDate));
        }


        return jsonObject;
    }



}
