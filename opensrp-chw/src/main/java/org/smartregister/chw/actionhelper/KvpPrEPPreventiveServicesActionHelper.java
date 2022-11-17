package org.smartregister.chw.actionhelper;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.util.JsonFormUtils.STEP1;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.kvp.dao.KvpDao;
import org.smartregister.chw.kvp.domain.VisitDetail;
import org.smartregister.chw.kvp.model.BaseKvpVisitAction;

import java.util.List;
import java.util.Map;

public class KvpPrEPPreventiveServicesActionHelper implements BaseKvpVisitAction.KvpVisitActionHelper {

    private String condoms_given;
    private String jsonPayload;
    private String baseEntityId;

    public KvpPrEPPreventiveServicesActionHelper(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> map) {
        this.jsonPayload = jsonPayload;
    }

    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            if (!KvpDao.getDominantKVPGroup(baseEntityId).equalsIgnoreCase("pwud") &&
                    !KvpDao.getDominantKVPGroup(baseEntityId).equalsIgnoreCase("pwid")) {
                getFieldJSONObject(fields(jsonObject, STEP1), "number_of_needles_and_syringes_distributed").put("type", "hidden");
                getFieldJSONObject(fields(jsonObject, STEP1), "number_of_sterile_water_for_injection_distributed").put("type", "hidden");
                getFieldJSONObject(fields(jsonObject, STEP1), "number_of_alcohol_swabs_distributed").put("type", "hidden");
                getFieldJSONObject(fields(jsonObject, STEP1), "number_of_disposable_safety_boxes_distributed").put("type", "hidden");
                getFieldJSONObject(fields(jsonObject, STEP1), "number_of_plasters_distributed").put("type", "hidden");
                getFieldJSONObject(fields(jsonObject, STEP1), "protective_items_for_PWID_label").put("type", "hidden");
            }
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            condoms_given = CoreJsonFormUtils.getValue(jsonObject, "condoms_given");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BaseKvpVisitAction.ScheduleStatus getPreProcessedStatus() {
        return null;
    }

    @Override
    public String getPreProcessedSubTitle() {
        return null;
    }

    @Override
    public String postProcess(String jsonPayload) {
        return null;
    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseKvpVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(condoms_given))
            return BaseKvpVisitAction.Status.PENDING;
        else {
            return BaseKvpVisitAction.Status.COMPLETED;
        }
    }

    @Override
    public void onPayloadReceived(BaseKvpVisitAction baseKvpVisitAction) {
        //overridden
    }
}
