package org.smartregister.chw.domain.agyw_reports;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.dao.ReportDao;
import org.smartregister.chw.domain.ReportObject;

import java.util.Date;

public class AGYWReportObject extends ReportObject {

    private final String[] noOfQuestions = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13"};
    private final String[] agesGroups = new String[]{"10-14","15-19","20-24"};
    private final Date reportDate;
    private JSONObject jsonObject ;

    public AGYWReportObject(Date reportDate) {
        super(reportDate);
        this.reportDate = reportDate;
    }

    @Override
    public JSONObject getIndicatorData() throws JSONException {



        jsonObject = new JSONObject();
        for (String questions : noOfQuestions){
            for (String ageGroup : agesGroups) {
                jsonObject.put("agyw"+"-"+questions+"-"+ageGroup,
                        ReportDao.getReportPerIndicatorCode("agyw"+"-"+questions+"-"+ageGroup, reportDate));
            }
        }
        // get total of all
        getTotalPerEachIndicator();

        return jsonObject;
    }

    private void getTotalPerEachIndicator() throws JSONException {
        int finalTotal = 0;
        for (String question: noOfQuestions){
            for (String ageGroup : agesGroups) {
                finalTotal += ReportDao.getReportPerIndicatorCode("agyw"+"-"+question+"-"+ageGroup, reportDate);
            }
            jsonObject.put("agyw"+"-"+question+"-JUMLA",finalTotal);  //display the total for specified question
            finalTotal=0;
        }
    }
}
