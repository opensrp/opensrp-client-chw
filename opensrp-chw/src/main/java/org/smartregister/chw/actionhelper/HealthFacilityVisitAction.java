package org.smartregister.chw.actionhelper;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.util.JsonFormUtils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class HealthFacilityVisitAction implements BaseAncHomeVisitAction.AncHomeVisitActionHelper {
    private Context context;
    private String jsonPayload;
    private MemberObject memberObject;
    private Map<Integer, LocalDate> dateMap;

    private String anc_hf_visit;
    private String anc_hf_visit_date;
    private String weight;
    private String sys_bp;
    private String dia_bp;
    private String hb_level;
    private String ifa_received;
    private String tests_done;
    private BaseAncHomeVisitAction.ScheduleStatus scheduleStatus;
    private String subTitle;

    public HealthFacilityVisitAction(MemberObject memberObject, Map<Integer, LocalDate> dateMap) {
        this.memberObject = memberObject;
        this.dateMap = dateMap;
    }

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> map) {
        this.context = context;
        this.jsonPayload = jsonPayload;
    }

    @Override
    public String getPreProcessed() {

        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            JSONArray fields = JsonFormUtils.fields(jsonObject);

            if (dateMap.size() > 0) {
                List<LocalDate> dateList = new ArrayList<>(dateMap.values());
                LocalDate visitDate = dateList.get(0);

                scheduleStatus = (visitDate.isBefore(LocalDate.now())) ? BaseAncHomeVisitAction.ScheduleStatus.OVERDUE : BaseAncHomeVisitAction.ScheduleStatus.DUE;
                String due = (visitDate.isBefore(LocalDate.now())) ? context.getString(R.string.overdue) : context.getString(R.string.due);

                subTitle = MessageFormat.format("{0} {1}", due, DateTimeFormat.forPattern("dd MMM yyyy").print(visitDate));

                String title = jsonObject.getJSONObject(JsonFormConstants.STEP1).getString(JsonFormConstants.STEP_TITLE);
                jsonObject.getJSONObject(JsonFormConstants.STEP1).put("title", MessageFormat.format(title, memberObject.getConfirmedContacts() + 1));

                JSONObject visit_field = JsonFormUtils.getFieldJSONObject(fields, "anc_hf_visit");
                visit_field.put("label_info_title", MessageFormat.format(visit_field.getString(JsonFormConstants.LABEL_INFO_TITLE), memberObject.getConfirmedContacts() + 1));
                visit_field.put("hint", MessageFormat.format(visit_field.getString(JsonFormConstants.HINT), memberObject.getConfirmedContacts() + 1, visitDate));


                if (dateList.size() > 1) {
                    JSONObject anc_hf_next_visit_date = JsonFormUtils.getFieldJSONObject(fields, "anc_hf_next_visit_date");
                    anc_hf_next_visit_date.put(JsonFormConstants.VALUE, DateTimeFormat.forPattern("dd-MM-yyyy").print(dateList.get(1)));
                }

                // current visit count
                JsonFormUtils.getFieldJSONObject(fields, "confirmed_visits").put(JsonFormConstants.VALUE, memberObject.getConfirmedContacts());
            }

            return jsonObject.toString();
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            anc_hf_visit = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "anc_hf_visit");
            anc_hf_visit_date = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "anc_hf_visit_date");
            weight = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "weight");
            sys_bp = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "sys_bp");
            dia_bp = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "dia_bp");
            hb_level = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "hb_level");
            ifa_received = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "ifa_received");
            tests_done = org.smartregister.chw.util.JsonFormUtils.getCheckBoxValue(jsonObject, "tests_done");
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public BaseAncHomeVisitAction.ScheduleStatus getPreProcessedStatus() {
        return scheduleStatus;
    }

    @Override
    public String getPreProcessedSubTitle() {
        return subTitle;
    }

    @Override
    public String postProcess(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);

            JSONArray field = JsonFormUtils.fields(jsonObject);
            JSONObject confirmed_visits = JsonFormUtils.getFieldJSONObject(field, "confirmed_visits");
            JSONObject anc_hf_next_visit_date = JsonFormUtils.getFieldJSONObject(field, "anc_hf_next_visit_date");

            String count = String.valueOf(memberObject.getConfirmedContacts());
            String value = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "anc_hf_visit");
            if (value.equalsIgnoreCase("Yes")) {
                count = String.valueOf(memberObject.getConfirmedContacts() + 1);
            } else {
                anc_hf_next_visit_date.put(JsonFormConstants.VALUE, "");
            }

            if (!confirmed_visits.getString(JsonFormConstants.VALUE).equals(count)) {
                confirmed_visits.put(JsonFormConstants.VALUE, count);
                return jsonObject.toString();
            }

        } catch (JSONException e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    public String evaluateSubTitle() {
        if (StringUtils.isBlank(anc_hf_visit))
            return null;

        StringBuilder stringBuilder = new StringBuilder();
        if (anc_hf_visit.equalsIgnoreCase("No")) {
            stringBuilder.append(context.getString(R.string.visit_not_done).replace("\n", ""));
        } else {
            try {
                Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(anc_hf_visit_date);
                stringBuilder.append(MessageFormat.format("{0}: {1}\n", context.getString(R.string.date), new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)));
                stringBuilder.append(MessageFormat.format("{0}: {1} {2}\n", context.getString(R.string.weight), weight, context.getString(R.string.kg)));
                stringBuilder.append(MessageFormat.format("{0}: {1}/{2} {3}\n", context.getString(R.string.str_bp).replace(":", ""), sys_bp, dia_bp, context.getString(R.string.mmHg)));
                stringBuilder.append(MessageFormat.format("{0}: {1} {2}\n", context.getString(R.string.hb_level), hb_level, context.getString(R.string.gdl)));
                stringBuilder.append(MessageFormat.format("{0}: {1}\n", context.getString(R.string.ifa_received), ifa_received));
                stringBuilder.append(MessageFormat.format("{0}: {1}\n", context.getString(R.string.tests_done), tests_done));
            } catch (ParseException e) {
                Timber.e(e);
            }
        }

        return stringBuilder.toString();
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(anc_hf_visit))
            return BaseAncHomeVisitAction.Status.PENDING;

        if (anc_hf_visit.equalsIgnoreCase("Yes")) {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        } else if (anc_hf_visit.equalsIgnoreCase("No")) {
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        } else {
            return BaseAncHomeVisitAction.Status.PENDING;
        }
    }

    @Override
    public void onPayloadReceived(BaseAncHomeVisitAction baseAncHomeVisitAction) {
        Timber.d("onPayloadReceived");
    }
}
