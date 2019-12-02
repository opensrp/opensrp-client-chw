package org.smartregister.chw.form_data;

import android.content.Context;

import androidx.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.view.LocationPickerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class NativeFormsDataBinder {

    private Map<String, Map<String, Object>> dbData = new HashMap<>();
    private DataProvider dataProvider;

    private String baseEntityID;
    private CommonPersonObjectClient client;
    private Context context;
    private FormLoader formLoader;
    private DataLoader dataLoader;

    public NativeFormsDataBinder(Context context, String baseEntityID) {
        this.baseEntityID = baseEntityID;
        this.context = context;
        formLoader = new NativeFormsFormLoader();
        dataLoader = new NativeFormsDataLoader();
    }

    @Nullable
    public JSONObject getPrePopulatedForm(Context context, String formName) {
        try {
            JSONObject jsonObjectForm = formLoader.getJsonForm(context, formName);
            bindNativeFormsMetaData(jsonObjectForm, context, baseEntityID);
            return getPrePopulatedForm(context, jsonObjectForm, baseEntityID);
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public JSONObject getPrePopulatedForm(Context context, @NotNull JSONObject jsonObjectForm, String baseEntityID) throws JSONException {
        String eventName = jsonObjectForm.optString(Constants.JSON_FORM_EXTRA.ENCOUNTER_TYPE);
        List<String> tables = dataLoader.getFormTables(context, eventName);
        for (String table : tables) {
            Map<String, Object> results = dataLoader.getValues(context, table, baseEntityID);
            if (results != null)
                dbData.put(table, results);
        }

        List<JSONObject> steps = getFormSteps(jsonObjectForm);
        for (JSONObject step : steps) {
            JSONArray jsonArray = step.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);
            loadFormData(context, jsonArray, baseEntityID);
        }
        return jsonObjectForm;
    }

    private void bindNativeFormsMetaData(@NotNull JSONObject jsonObjectForm, Context context, String baseEntityID) throws JSONException {
        // baseEntityID
        jsonObjectForm.put(org.smartregister.family.util.JsonFormUtils.ENTITY_ID, baseEntityID);

        // metaData
        LocationPickerView lpv = new LocationPickerView(context);
        lpv.init();
        JSONObject metadata = jsonObjectForm.getJSONObject(org.smartregister.family.util.JsonFormUtils.METADATA);
        String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());
        metadata.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);
    }

    private void loadFormData(Context context, JSONArray jsonArray, String baseEntityID) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            try {
                // get value of key
                jsonObject.put(JsonFormConstants.VALUE, dataLoader.getValue(context, baseEntityID, jsonObject, dbData));
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private List<JSONObject> getFormSteps(JSONObject jsonObject) throws JSONException {
        List<JSONObject> steps = new ArrayList<>();
        int x = 1;
        while (true) {
            String step_name = "step" + x;
            if (jsonObject.has(step_name)) {
                steps.add(jsonObject.getJSONObject(step_name));
            } else {
                break;
            }
            x++;
        }

        return steps;
    }

    public void setClient(CommonPersonObjectClient client) {
        this.client = client;
    }
}
