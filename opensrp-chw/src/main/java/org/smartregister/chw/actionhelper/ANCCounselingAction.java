package org.smartregister.chw.actionhelper;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.util.JsonFormUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ANCCounselingAction implements BaseAncHomeVisitAction.AncHomeVisitActionHelper {
    private Context context;
    private String anc_counseling;
    private String birth_hf_counseling;
    private String nutrition_counseling;
    private String jsonPayload;
    private MemberObject memberObject;
    private Map<Integer, LocalDate> dateMap;

    public ANCCounselingAction(MemberObject memberObject, Map<Integer, LocalDate> dateMap) {
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
            int x = 1;
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<Integer, LocalDate> entry : dateMap.entrySet()) {
                builder.append(MessageFormat.format("{0} {1} · {2} \n",
                        context.getString(R.string.counseling_visit),
                        memberObject.getConfirmedContacts() + x,
                        entry.getValue())
                );
                x++;
            }

            JSONObject visit_field = JsonFormUtils.getFieldJSONObject(fields, "anc_counseling_toaster");
            visit_field.put("text", MessageFormat.format(visit_field.getString("text"), builder.toString()));

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
            anc_counseling = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "anc_counseling");
            birth_hf_counseling = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "birth_hf_counseling");
            nutrition_counseling = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "nutrition_counseling");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BaseAncHomeVisitAction.ScheduleStatus getPreProcessedStatus() {
        return null;
    }

    @Override
    public String getPreProcessedSubTitle() {
        return null;
    }

    @Override
    public String postProcess(String s) {
        return s;
    }

    @Override
    public String evaluateSubTitle() {
        List<String> yes = new ArrayList<>();
        List<String> nos = new ArrayList<>();
        if ("Yes".equalsIgnoreCase(anc_counseling)) {
            yes.add(context.getString(R.string.anc_visit_counselling));
        } else {
            nos.add(context.getString(R.string.anc_visit_counselling));
        }

        if ("Yes".equalsIgnoreCase(birth_hf_counseling)) {
            yes.add(context.getString(R.string.delivery_at_facilty_counselling));
        } else {
            nos.add(context.getString(R.string.delivery_at_facilty_counselling));
        }

        if ("Yes".equalsIgnoreCase(nutrition_counseling)) {
            yes.add(context.getString(R.string.nutrition_counselling));
        } else {
            nos.add(context.getString(R.string.nutrition_counselling));
        }
        StringBuilder stringBuilder = new StringBuilder();

        if (yes.size() > 0) {
            for (String s : yes) {
                stringBuilder.append(s).append(", ");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 2);
            stringBuilder.append(context.getString(R.string.done).toLowerCase());
        }

        if (nos.size() > 0) {
            if (stringBuilder.toString().trim().length() > 0) {
                stringBuilder.append(" · ");
            }

            for (String s : nos) {
                stringBuilder.append(s).append(", ");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 2);
            stringBuilder.append(context.getString(R.string.not_done).toLowerCase());
        }

        if (StringUtils.isNotBlank(anc_counseling) & StringUtils.isNotBlank(birth_hf_counseling) & StringUtils.isNotBlank(nutrition_counseling)) {
            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(anc_counseling) && StringUtils.isBlank(birth_hf_counseling) && StringUtils.isBlank(nutrition_counseling))
            return BaseAncHomeVisitAction.Status.PENDING;

        if (anc_counseling.equalsIgnoreCase("Yes")
                && birth_hf_counseling.equalsIgnoreCase("Yes")
                && nutrition_counseling.equalsIgnoreCase("Yes")
        ) {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        } else if (StringUtils.isNotBlank(anc_counseling) & StringUtils.isNotBlank(birth_hf_counseling) & StringUtils.isNotBlank(nutrition_counseling)) {
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
