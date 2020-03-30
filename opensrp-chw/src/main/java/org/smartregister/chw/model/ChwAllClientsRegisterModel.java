package org.smartregister.chw.model;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.family.util.Utils;
import org.smartregister.opd.model.OpdRegisterActivityModel;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.util.FormUtils;

import java.util.HashMap;

import timber.log.Timber;

import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_KEY.UNIQUE_ID;

public class ChwAllClientsRegisterModel extends OpdRegisterActivityModel {

    private FormUtils formUtils;

    @Nullable
    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId, @Nullable HashMap<String, String> injectedFieldValues) throws JSONException {
        JSONObject form = injectFields(formName, injectedFieldValues);

        if (StringUtils.isNotBlank(entityId)) {
            entityId = entityId.replace("-", "");
        }

        // Inject OPenSrp id into the form
        JSONObject stepOne = form.getJSONObject(OpdJsonFormUtils.STEP1);
        JSONArray jsonArray = stepOne.getJSONArray(OpdJsonFormUtils.FIELDS);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.getString(OpdJsonFormUtils.KEY).equalsIgnoreCase(UNIQUE_ID)) {
                jsonObject.remove(OpdJsonFormUtils.VALUE);
                jsonObject.put(OpdJsonFormUtils.VALUE, entityId);
            }
        }

        return form;
    }

    private JSONObject injectFields(String formName, @Nullable HashMap<String, String> injectedFieldValues)
            throws JSONException {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (injectedFieldValues != null && injectedFieldValues.size() > 0) {
            JSONObject stepOne = form.getJSONObject(OpdJsonFormUtils.STEP1);
            JSONArray jsonArray = stepOne.getJSONArray(OpdJsonFormUtils.FIELDS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String fieldKey = jsonObject.getString(OpdJsonFormUtils.KEY);

                String fieldValue = injectedFieldValues.get(fieldKey);

                if (!TextUtils.isEmpty(fieldValue)) {
                    jsonObject.put(OpdJsonFormUtils.VALUE, fieldValue);
                }
            }
        }
        return form;
    }

    private FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(Utils.context().applicationContext());
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return formUtils;
    }

}
