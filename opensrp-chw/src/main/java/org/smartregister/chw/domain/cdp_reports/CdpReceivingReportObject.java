package org.smartregister.chw.domain.cdp_reports;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.dao.ReportDao;
import org.smartregister.chw.domain.ReportObject;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CdpReceivingReportObject extends ReportObject {
    private Date reportDate;

    public CdpReceivingReportObject(Date reportDate) {
        super(reportDate);
        this.reportDate = reportDate;
    }


    @Override
    public JSONObject getIndicatorData() throws JSONException {
        JSONArray dataArray = new JSONArray();
        List<Map<String, String>> getHfCdpStockLogList = ReportDao.getHfCdpStockLog(reportDate);

        int i = 0;
        for (Map<String, String> getHfCdpStockLog : getHfCdpStockLogList) {
            JSONObject reportJsonObject = new JSONObject();
            reportJsonObject.put("id", ++i);

            reportJsonObject.put("source", getCbhsClientDetails(getHfCdpStockLog, "name"));
            reportJsonObject.put("condom-brand", getCbhsClientDetails(getHfCdpStockLog, "condom_brand"));
            reportJsonObject.put("number-of-male-condom", getCbhsClientDetails(getHfCdpStockLog, "number_male_condom"));
            reportJsonObject.put("number-of-female-condom", getCbhsClientDetails(getHfCdpStockLog, "number_female_condom"));
            dataArray.put(reportJsonObject);
        }

        JSONObject resultJsonObject = new JSONObject();
        resultJsonObject.put("reportData", dataArray);

        return resultJsonObject;
    }

    private String getCbhsClientDetails(Map<String, String> chwRegistrationFollowupClient, String key) {
        String details = chwRegistrationFollowupClient.get(key);
        if (StringUtils.isNotBlank(details)) {
            return details;
        }
        return "-";
    }


}
