package org.smartregister.chw.domain.mother_champion_report;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.domain.ReportObject;

import java.util.Date;

public class MotherChampionReportObject extends ReportObject {

    public MotherChampionReportObject(Date reportDate) {
        super(reportDate);

    }

    @Override
    public JSONObject getIndicatorData() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        //TODO: update this to return correct indicator data
        jsonObject.put("b-1", 20);
        jsonObject.put("b-2", 30);
        jsonObject.put("b-3", 40);
        jsonObject.put("b-4", 50);
        jsonObject.put("b-5", 60);
        jsonObject.put("b-6", 70);
        jsonObject.put("b-7", 80);

        return jsonObject;
    }


}
