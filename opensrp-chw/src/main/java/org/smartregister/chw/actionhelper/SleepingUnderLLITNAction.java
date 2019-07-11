package org.smartregister.chw.actionhelper;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.JsonFormUtils;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class SleepingUnderLLITNAction implements BaseAncHomeVisitAction.AncHomeVisitActionHelper {
    private Context context;
    private String sleeping_llitn;

    @Override
    public void onJsonFormLoaded(String s, Context context, Map<String, List<VisitDetail>> map) {
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
            sleeping_llitn = JsonFormUtils.getValue(jsonObject, "sleeping_llitn");
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
        if (StringUtils.isBlank(sleeping_llitn))
            return null;

        return sleeping_llitn.equalsIgnoreCase("Yes") ? context.getString(R.string.yes) : context.getString(R.string.no);
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(sleeping_llitn))
            return BaseAncHomeVisitAction.Status.PENDING;

        if (sleeping_llitn.equalsIgnoreCase("Yes")) {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        } else if (sleeping_llitn.equalsIgnoreCase("No")) {
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
