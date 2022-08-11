package org.smartregister.chw.domain.cbhs_reports;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.dao.ReportDao;
import org.smartregister.chw.domain.ReportObject;
import org.smartregister.util.Log;
import org.smartregister.util.StringUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CbhsMonthlyReportObject extends ReportObject {
    private Date reportDate;
    private final String[] indicatorCodes = new String[]{"namba-za-mteja", "sababu-za-usajili",
            "hali-ya-maamubikizi-ya-vvu", "namba-ya-usajili-wa kliniki", "aina-ya-kliniki", "umri",
            "jinsia", "hali-ya-mteja", "tabia-ya-mteja", "huduma-zilizotolewa",
            "vifaa-vilivyotolewa", "rufaa-zilizotolewa", "rufaa-zilizofanikiwa",
            "hali-ya-tiba-na-matunzo", "hali-ya-usajili-na-ufuatiliaji"};

    public CbhsMonthlyReportObject(Date reportDate) {
        super(reportDate);
        this.reportDate = reportDate;

    }

    @Override
    public JSONObject getIndicatorData() throws JSONException {
        JSONArray dataArray = new JSONArray();
        List<Map<String, String>> chwRegistrationFollowupClientsList = ReportDao.getCHWRegistrationFollowUpClients(reportDate);

        int i = 0;
        for (Map<String, String> chwRegistrationFollowupClient : chwRegistrationFollowupClientsList) {
            JSONObject reportJsonObject = new JSONObject();
            reportJsonObject.put("id", ++i);

            reportJsonObject.put("namba-za-mteja", StringUtils.isNotBlank(chwRegistrationFollowupClient.get("cbhs_number")) ? chwRegistrationFollowupClient.get("cbhs_number") :"-");
            reportJsonObject.put("sababu-za-usajili", StringUtils.isNotBlank(chwRegistrationFollowupClient.get("registration_reason")) ? chwRegistrationFollowupClient.get("cbhs_number") :"-");
            reportJsonObject.put("hali-ya-maamubikizi-ya-vvu", StringUtils.isNotBlank(chwRegistrationFollowupClient.get("hiv_status_during_registration")) ? chwRegistrationFollowupClient.get("hiv_status_during_registration"):"-");
            reportJsonObject.put("hali-ya-maamubikizi-ya-tb", StringUtils.isNotBlank(chwRegistrationFollowupClient.get("tb_status_during_registration")) ? chwRegistrationFollowupClient.get("tb_status_during_registration"):"-");
            reportJsonObject.put("namba-ya-usajili-wa kliniki", StringUtils.isNotBlank(chwRegistrationFollowupClient.get("clinic_registration_number")) ? chwRegistrationFollowupClient.get("clinic_registration_number"): "-");
            reportJsonObject.put("aina-ya-kliniki", StringUtils.isNotBlank(chwRegistrationFollowupClient.get("type_of_clinic")) ? chwRegistrationFollowupClient.get("type_of_clinic") : "-");
            reportJsonObject.put("umri", StringUtils.isNotBlank(chwRegistrationFollowupClient.get("age")) ? chwRegistrationFollowupClient.get("age") : "-");
            reportJsonObject.put("jinsia", StringUtils.isNotBlank(chwRegistrationFollowupClient.get("gender")) ? chwRegistrationFollowupClient.get("gender") : "-");
            reportJsonObject.put("hali-ya-mteja", StringUtils.isNotBlank(chwRegistrationFollowupClient.get("status_after_testing")) ? chwRegistrationFollowupClient.get("status_after_testing") : "-");
            reportJsonObject.put("huduma-zilizotolewa", StringUtils.isNotBlank(chwRegistrationFollowupClient.get("hiv_services_provided")) ? chwRegistrationFollowupClient.get("hiv_services_provided") : "-");
            reportJsonObject.put("vifaa-vilivyotolewa", StringUtils.isNotBlank(chwRegistrationFollowupClient.get("supplies_provided")) ?chwRegistrationFollowupClient.get("supplies_provided") : "-");
            reportJsonObject.put("rufaa-zilizotolewa", StringUtils.isNotBlank(chwRegistrationFollowupClient.get("issued_referrals")) ? chwRegistrationFollowupClient.get("issued_referrals"):"-");
            reportJsonObject.put("rufaa-zilizofanikiwa", StringUtils.isNotBlank(chwRegistrationFollowupClient.get("successful_referrals")) ? chwRegistrationFollowupClient.get("successful_referrals"):"-");
            reportJsonObject.put("hali-ya-tiba-na-matunzo", StringUtils.isNotBlank(chwRegistrationFollowupClient.get("state_of_therapy")) ? chwRegistrationFollowupClient.get("state_of_therapy") : "-");
            reportJsonObject.put("hali-ya-usajili-na-ufuatiliaji", StringUtils.isNotBlank(chwRegistrationFollowupClient.get("registration_or_followup_status")) ? chwRegistrationFollowupClient.get("registration_or_followup_status"):"-");

            dataArray.put(reportJsonObject);
        }

        JSONObject resultJsonObject = new JSONObject();
        resultJsonObject.put("reportData", dataArray);

        return resultJsonObject;
    }


}
