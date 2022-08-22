package org.smartregister.chw.domain.cbhs_reports;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.dao.ReportDao;
import org.smartregister.chw.domain.ReportObject;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CbhsMonthlyReportObject extends ReportObject {
    private final Context context;
    private Date reportDate;

    public CbhsMonthlyReportObject(Date reportDate, Context context) {
        super(reportDate);
        this.reportDate = reportDate;
        this.context = context;
    }

    @Override
    public JSONObject getIndicatorData() throws JSONException {
        JSONArray dataArray = new JSONArray();
        List<Map<String, String>> chwRegistrationFollowupClientsList = ReportDao.getCHWRegistrationFollowUpClients(reportDate);

        int i = 0;
        for (Map<String, String> chwRegistrationFollowupClient : chwRegistrationFollowupClientsList) {
            JSONObject reportJsonObject = new JSONObject();
            reportJsonObject.put("id", ++i);

            reportJsonObject.put("namba-za-mteja", getCbhsClientDetails(chwRegistrationFollowupClient, "cbhs_number"));
            reportJsonObject.put("sababu-za-usajili", getCbhsClientDetails(chwRegistrationFollowupClient, "registration_reason"));
            reportJsonObject.put("hali-ya-maamubikizi-ya-vvu", getCbhsClientDetails(chwRegistrationFollowupClient, "hiv_status_during_registration"));
            reportJsonObject.put("hali-ya-maamubikizi-ya-tb", getCbhsClientDetails(chwRegistrationFollowupClient, "tb_status_during_registration"));
            reportJsonObject.put("namba-ya-usajili-wa kliniki", getCbhsClientDetails(chwRegistrationFollowupClient, "clinic_registration_number"));
            reportJsonObject.put("aina-ya-kliniki", getCbhsClientDetails(chwRegistrationFollowupClient, "type_of_clinic"));
            reportJsonObject.put("umri", getCbhsClientDetails(chwRegistrationFollowupClient, "age"));
            reportJsonObject.put("jinsia", getCbhsClientDetails(chwRegistrationFollowupClient, "gender"));
            reportJsonObject.put("hali-ya-mteja", getCbhsClientDetails(chwRegistrationFollowupClient, "status_after_testing"));
            reportJsonObject.put("huduma-zilizotolewa", getCbhsClientDetails(chwRegistrationFollowupClient, "hiv_services_provided"));
            reportJsonObject.put("vifaa-vilivyotolewa", getCbhsClientDetails(chwRegistrationFollowupClient, "supplies_provided"));
            reportJsonObject.put("rufaa-zilizotolewa", getCbhsClientDetails(chwRegistrationFollowupClient, "issued_referrals"));
            reportJsonObject.put("rufaa-zilizofanikiwa", getCbhsClientDetails(chwRegistrationFollowupClient, "successful_referrals"));
            reportJsonObject.put("hali-ya-tiba-na-matunzo", getCbhsClientDetails(chwRegistrationFollowupClient, "state_of_therapy"));
            reportJsonObject.put("hali-ya-usajili-na-ufuatiliaji", getCbhsClientDetails(chwRegistrationFollowupClient, "registration_or_followup_status"));

            dataArray.put(reportJsonObject);
        }

        JSONObject resultJsonObject = new JSONObject();
        resultJsonObject.put("reportData", dataArray);

        return resultJsonObject;
    }

    private String getCbhsClientDetails(Map<String, String> chwRegistrationFollowupClient, String key) {
        String details = chwRegistrationFollowupClient.get(key);
        if (StringUtils.isNotBlank(details)) {
            switch (key) {
                case "registration_reason":
                    return getStringValues(details, "reason_for_registration_");
                case "hiv_services_provided":
                    return getStringValues(details, "hiv_services_provided_");
                case "supplies_provided":
                    return getStringValues(details, "supplies_provided_");
                default:
                    return details;
            }
        }
        return "-";
    }

    private String getStringValues(String receivedVal, String resourceKey) {
        if (receivedVal.startsWith("[")) {
            //remove the [ and ] and add the values separated in a comma to array
            String[] values = receivedVal.substring(1, receivedVal.length() - 1).split(",");
            StringBuilder sb = new StringBuilder();
            for (String value : values) {
                int humanReadableValueId = context.getResources().getIdentifier(resourceKey + value, "string", context.getPackageName());
                if (humanReadableValueId != 0) {
                    sb.append(context.getString(humanReadableValueId)).append(",");
                }
                sb.append(value).append(",");
            }
            return sb.toString();
        }
        int humanReadableValueId = context.getResources().getIdentifier(resourceKey + receivedVal, "string", context.getPackageName());
        if (humanReadableValueId != 0) {
            return context.getString(humanReadableValueId);
        }
        return receivedVal;
    }


}
