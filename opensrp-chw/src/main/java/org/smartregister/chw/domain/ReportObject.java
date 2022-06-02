package org.smartregister.chw.domain;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public abstract class ReportObject {

    private List<String> indicatorCodes;
    private final Date reportDate;

    public ReportObject(Date reportDate) {
        this.reportDate = reportDate;
    }

    public List<String> getIndicatorCodes() {
        return indicatorCodes;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public JSONObject getIndicatorData() throws JSONException {
        return new JSONObject();
    }

    public String getIndicatorDataAsGson(JSONObject jsonObject) throws JSONException {
        Gson gson = new Gson();
        return gson.toJson(jsonObject);
    }
}
