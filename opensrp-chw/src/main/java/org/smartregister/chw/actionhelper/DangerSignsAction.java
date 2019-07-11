package org.smartregister.chw.actionhelper;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.JsonFormUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class DangerSignsAction implements BaseAncHomeVisitAction.AncHomeVisitActionHelper {
    private String signs_present;
    private String counseling;
    private Context context;

    @Override
    public void onJsonFormLoaded(String jsonString, Context context, Map<String, List<VisitDetail>> details) {
        this.context = context;
    }

    @Override
    public String getPreProcessed() {
        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            signs_present = JsonFormUtils.getCheckBoxValue(jsonObject, "danger_signs_present");
            counseling = JsonFormUtils.getValue(jsonObject, "danger_signs_counseling");
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MessageFormat.format("{0}: {1}", context.getString(R.string.anc_home_visit_danger_signs), signs_present));
        stringBuilder.append("\n");
        stringBuilder.append(MessageFormat.format("{0} {1}",
                context.getString(R.string.danger_signs_counselling),
                (counseling.equalsIgnoreCase("Yes") ? context.getString(R.string.done).toLowerCase() : context.getString(R.string.not_done).toLowerCase())
        ));

        if (counseling.equalsIgnoreCase("Yes") || counseling.equalsIgnoreCase("No"))
            return stringBuilder.toString();

        return null;
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(counseling))
            return BaseAncHomeVisitAction.Status.PENDING;

        if (counseling.equalsIgnoreCase("Yes")) {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        } else if (counseling.equalsIgnoreCase("No")) {
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
