package org.smartregister.chw.domain.cbhs_reports;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.domain.ReportObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CbhsMonthlyReportObject extends ReportObject {
    final Date reportDate;
    private final String[] indicatorCodesArray = new String[]{"reason_for_registration", "hiv_status", "clinic_number", "type_of_clinic", "age", "sex", "services_provided", "medication_received", "referral_received"};
    private final List<String> indicatorCodes = new ArrayList<>();

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
        /*
         * we'll pass a json object with the following structure:
         * reportData = [
            {
                 "cbhs_number": "102-2021-210-2"
                 "reason_for_registration": "reason"
                 "hiv_status": "status",
                 "clinic_number": "clinic_number",
                 "type_of_clinic": "type_of_clinic",
                 "age": "age",
                 "sex": "sex",
                 "services_provided": "services_provided"
                 "medication_received": "medication_received",
                 "referral_received": "referral_received"
             },
          ]
         * */
        JSONObject jsonObject = new JSONObject();
        JSONArray dataArray = new JSONArray();
        //Todo get an array of cbhs_numbers from the db for that report time period
        //something like : List<String> cbhsNumbers = ReportDao.getAllCbhsNumbersForTheReportPeriod(reportDate);
        //Todo loop through the cbhsNumbers and get the data for each cbhs_number
        //something like : for (String cbhsNumber : cbhsNumbers) {
        //                      JSONObject cbhsObject = new JSONObject();
        //                      cbhsObject.put("cbhs_number", cbhsNumber);
        //                      for(String indicatorCode : indicatorCodes) {
        //                          cbhsObject.put(indicatorCode, ReportDao.getCbhsDataForClientInReportDate(cbhsNumber, reportDate));
        //                      }
        //                      dataArray.put(cbhsObject);
        //                  }

        //TODO add the dataArray to the jsonObject
        jsonObject.put("reportData", dataArray);

        return jsonObject;
    }


}
