package org.smartregister.chw.domain.mother_champion_report;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.dao.ReportDao;
import org.smartregister.chw.domain.ReportObject;

import java.util.Date;

public class MotherChampionReportObject extends ReportObject {
    private final String[] indicatorCodes = new String[]{"b-1", "b-2", "b-3", "b-4", "b-5", "b-7"};
    private final Date reportDate;

    public MotherChampionReportObject(Date reportDate) {
        super(reportDate);
        this.reportDate = reportDate;

    }

    @Override
    public JSONObject getIndicatorData() throws JSONException {

        JSONObject jsonObject = new JSONObject();

        for (String indicatorCode : indicatorCodes) {
            jsonObject.put(indicatorCode, ReportDao.getReportPerIndicatorCode(indicatorCode, reportDate));
        }

        return jsonObject;
    }


}
