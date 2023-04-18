package org.smartregister.chw.domain.cbhs_reports;

import android.content.Context;
import android.content.res.Configuration;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.ReportDao;
import org.smartregister.chw.domain.ReportObject;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CbhsMonthlyReportObject extends ReportObject {
    private final Context context;
    private final Date reportDate;

    public CbhsMonthlyReportObject(Date reportDate, Context context) {
        super(reportDate);
        this.reportDate = reportDate;

        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale("sw"));
        this.context = context.createConfigurationContext(configuration);

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

            if (chwRegistrationFollowupClient.get("client_hiv_status_after_testing") == null)
                reportJsonObject.put("hali-ya-maamubikizi-ya-vvu", getCbhsClientDetails(chwRegistrationFollowupClient, "hiv_status_during_registration"));
            else
                reportJsonObject.put("hali-ya-maamubikizi-ya-vvu", getCbhsClientDetails(chwRegistrationFollowupClient, "client_hiv_status_after_testing"));

            if (chwRegistrationFollowupClient.get("client_tb_status_after_testing") == null)
                reportJsonObject.put("hali-ya-maamubikizi-ya-tb", getCbhsClientDetails(chwRegistrationFollowupClient, "tb_status_during_registration"));
            else
                reportJsonObject.put("hali-ya-maamubikizi-ya-tb", getCbhsClientDetails(chwRegistrationFollowupClient, "client_tb_status_after_testing"));


            String clinicRegistrationNumber = "";
            String clinicName = "";
            if (!getCbhsClientDetails(chwRegistrationFollowupClient, "ctc_number").equals("-")) {
                clinicRegistrationNumber += getCbhsClientDetails(chwRegistrationFollowupClient, "ctc_number") + "<br>";
                clinicName += "CTC <br> ";
            }

            if (!getCbhsClientDetails(chwRegistrationFollowupClient, "tb_number").equals("-")) {
                clinicRegistrationNumber += getCbhsClientDetails(chwRegistrationFollowupClient, "tb_number") + "<br>";
                clinicName += "TB <br> ";
            }
            if (!getCbhsClientDetails(chwRegistrationFollowupClient, "rch_number").equals("-")) {
                clinicRegistrationNumber += getCbhsClientDetails(chwRegistrationFollowupClient, "rch_number") + "<br>";
                clinicName += "RCH <br> ";
            }
            if (!getCbhsClientDetails(chwRegistrationFollowupClient, "mat_number").equals("-")) {
                clinicRegistrationNumber += getCbhsClientDetails(chwRegistrationFollowupClient, "mat_number") + "<br>";
                clinicName += "MAT <br> ";
            }

            if (StringUtils.isBlank(clinicRegistrationNumber))
                clinicRegistrationNumber = "-";

            if (StringUtils.isBlank(clinicName))
                clinicName = "-";

            reportJsonObject.put("namba-ya-usajili-wa kliniki", clinicRegistrationNumber);
            reportJsonObject.put("aina-ya-kliniki", clinicName);


            String referralIssued = "";
            if (!getCbhsClientDetails(chwRegistrationFollowupClient, "issued_referrals").equals("-")) {
                referralIssued += getCbhsClientDetails(chwRegistrationFollowupClient, "issued_referrals") + "<br>";
            }
            if (!getCbhsClientDetails(chwRegistrationFollowupClient, "referrals_issued_to_other_services").equals("-")) {
                referralIssued += getCbhsClientDetails(chwRegistrationFollowupClient, "referrals_issued_to_other_services") + "<br>";
            }

            String referralsCompleted = "";
            if (!getCbhsClientDetails(chwRegistrationFollowupClient, "successful_referrals").equals("-")) {
                referralsCompleted += getCbhsClientDetails(chwRegistrationFollowupClient, "successful_referrals") + "<br>";
            }
            if (!getCbhsClientDetails(chwRegistrationFollowupClient, "referrals_to_other_services_completed").equals("-")) {
                referralsCompleted += getCbhsClientDetails(chwRegistrationFollowupClient, "referrals_to_other_services_completed") + "<br>";
            }


            reportJsonObject.put("umri", getCbhsClientDetails(chwRegistrationFollowupClient, "age"));
            reportJsonObject.put("jinsia", getCbhsClientDetails(chwRegistrationFollowupClient, "gender"));
            reportJsonObject.put("huduma-zilizotolewa", getCbhsClientDetails(chwRegistrationFollowupClient, "hiv_services_provided"));
            reportJsonObject.put("vifaa-vilivyotolewa", getCbhsClientDetails(chwRegistrationFollowupClient, "supplies_provided"));
            reportJsonObject.put("rufaa-zilizotolewa", referralIssued);
            reportJsonObject.put("rufaa-zilizofanikiwa", referralsCompleted);


            String stateOfHivCareAndTreatment = getCbhsClientDetails(chwRegistrationFollowupClient, "state_of_hiv_care_and_treatment");

            if (chwRegistrationFollowupClient.get("state_of_registration_in_tb_and_pwid_clinics") != null && !chwRegistrationFollowupClient.get("state_of_registration_in_tb_and_pwid_clinics").equals("not_applicable")) {
                stateOfHivCareAndTreatment = stateOfHivCareAndTreatment + ", " + getCbhsClientDetails(chwRegistrationFollowupClient, "state_of_registration_in_tb_and_pwid_clinics");
            }


            reportJsonObject.put("hali-ya-tiba-na-matunzo", stateOfHivCareAndTreatment);
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
                case "issued_referrals":
                case "successful_referrals":
                    return getTranslatedReferralFocus(details);
                case "registration_reason":
                    return getStringValues(details, "reason_for_registration_");
                case "referrals_issued_to_other_services":
                case "referrals_to_other_services_completed":
                case "supplies_provided":
                case "hiv_services_provided":
                case "registration_or_followup_status":
                case "state_of_hiv_care_and_treatment":
                case "state_of_registration_in_tb_and_pwid_clinics":
                case "gender":
                case "hiv_status_during_registration":
                case "tb_status_during_registration":
                case "client_hiv_status_after_testing":
                case "client_tb_status_after_testing":
                    return getStringValues(details, "cbhs_");
                default:
                    return details;
            }

        }
        return "-";
    }

    private String getStringValues(String receivedVal, String resourceKey) {
        if (receivedVal.contains(",") || receivedVal.startsWith("[")) {
            String[] values;
            if (receivedVal.startsWith("[")) {
                //remove the [ and ] and add the values separated in a comma to array
                values = receivedVal.substring(1, receivedVal.length() - 1).split(",");
            } else {
                values = receivedVal.split(",");
            }
            StringBuilder sb = new StringBuilder();
            for (String value : values) {
                int humanReadableValueId = context.getResources().getIdentifier(resourceKey.toLowerCase() + value.trim().toLowerCase(), "string", context.getPackageName());
                if (humanReadableValueId != 0) {
                    sb.append(context.getString(humanReadableValueId)).append(", ");
                } else
                    sb.append(value).append(", ");
            }

            String stringValues = sb.toString().trim();
            if (stringValues.charAt(stringValues.length() - 1) == ',') {
                stringValues = stringValues.substring(0, stringValues.length() - 1);
            }

            return stringValues;
        }

        int humanReadableValueId = context.getResources().getIdentifier(resourceKey + receivedVal.trim().toLowerCase(), "string", context.getPackageName());
        if (humanReadableValueId != 0) {
            return context.getString(humanReadableValueId);
        }
        return receivedVal;
    }

    private String getTranslatedReferralFocus(String focusString) {
        String focusList[];
        if (focusString.contains(",")) {
            focusList = focusString.split(",");
        } else {
            focusList = new String[]{focusString};
        }
        StringBuilder translatedFocus = new StringBuilder();
        for (String focus : focusList) {
            switch (focus) {
                case CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS:
                    translatedFocus.append(context.getString(R.string.anc_danger_signs)).append(",");
                    break;
                case CoreConstants.TASKS_FOCUS.PNC_DANGER_SIGNS:
                    translatedFocus.append(context.getString(R.string.pnc_danger_signs)).append(",");
                    break;
                case CoreConstants.TASKS_FOCUS.SUSPECTED_MALARIA:
                    translatedFocus.append(context.getString(R.string.client_malaria_follow_up)).append(",");
                    break;
                case CoreConstants.TASKS_FOCUS.SICK_CHILD:
                    translatedFocus.append(context.getString(R.string.sick_child)).append(",");
                    break;
                case CoreConstants.TASKS_FOCUS.FP_SIDE_EFFECTS:
                    translatedFocus.append(context.getString(R.string.family_planning_referral)).append(",");
                    break;
                case CoreConstants.TASKS_FOCUS.SUSPECTED_TB:
                    translatedFocus.append(context.getString(R.string.tb_referral)).append(",");
                    break;
                case CoreConstants.TASKS_FOCUS.CONVENTIONAL_HIV_TEST:
                    translatedFocus.append(context.getString(R.string.hts_referral)).append(",");
                    break;
                case CoreConstants.TASKS_FOCUS.SICK_HIV:
                    translatedFocus.append(context.getString(R.string.hiv_referral)).append(",");
                    break;
                case CoreConstants.TASKS_FOCUS.PREGNANCY_CONFIRMATION:
                    translatedFocus.append(context.getString(R.string.pregnancy_confirmation)).append(",");
                    break;
                case CoreConstants.TASKS_FOCUS.SUSPECTED_GBV:
                    translatedFocus.append(context.getString(R.string.gbv_referral)).append(",");
                    break;
                case CoreConstants.TASKS_FOCUS.SUSPECTED_CHILD_GBV:
                    translatedFocus.append(context.getString(R.string.child_gbv_referral)).append(",");
                    break;
                default:
                    translatedFocus.append(focus).append(",");
            }
        }

        if (StringUtils.isBlank(translatedFocus.toString())) {
            return "-";
        }

        String stringValues = translatedFocus.toString().trim();
        if (stringValues.charAt(translatedFocus.length() - 1) == ',') {
            stringValues = stringValues.substring(0, stringValues.length() - 1);
        }
        return stringValues;
    }


}
