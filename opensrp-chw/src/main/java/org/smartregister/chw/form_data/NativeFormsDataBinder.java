package org.smartregister.chw.form_data;

import android.content.Context;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class NativeFormsDataBinder {

    private Map<String, Map<String, Object>> dbData = new HashMap<>();
    private String baseEntityID;
    private Context context;
    private FormLoader formLoader;
    private DataLoader dataLoader;

    public NativeFormsDataBinder(Context context, String baseEntityID) {
        this.baseEntityID = baseEntityID;
        this.context = context;
    }

    public FormLoader getFormLoader() {
        if (formLoader == null) {
            formLoader = new NativeFormsFormLoader();
        }
        return formLoader;
    }

    public void setFormLoader(FormLoader formLoader) {
        this.formLoader = formLoader;
    }

    public DataLoader getDataLoader() {
        if (dataLoader == null) {
            dataLoader = new NativeFormsDataLoader();
        }
        return dataLoader;
    }

    public void setDataLoader(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    @Nullable
    public JSONObject getPrePopulatedForm(String formName) {
        try {
            JSONObject jsonObjectForm = getFormLoader().getJsonForm(context, formName);
            getDataLoader().bindNativeFormsMetaData(jsonObjectForm, context, baseEntityID);
            return getPrePopulatedForm(context, jsonObjectForm, baseEntityID);
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public JSONObject getPrePopulatedForm(Context context, @NotNull JSONObject jsonObjectForm, String baseEntityID) throws JSONException {
        String eventName = jsonObjectForm.optString(Constants.JSON_FORM_EXTRA.ENCOUNTER_TYPE);
        List<String> tables = getDataLoader().getFormTables(context, eventName);
        for (String table : tables) {
            Map<String, Object> results = getDataLoader().getValues(context, table, baseEntityID);
            if (results != null)
                dbData.put(table, results);
        }

        getDataLoader().loadForm(context, jsonObjectForm, baseEntityID, dbData);

        return jsonObjectForm;
    }
}
