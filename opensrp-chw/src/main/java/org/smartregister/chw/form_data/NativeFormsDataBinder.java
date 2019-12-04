package org.smartregister.chw.form_data;

import android.content.Context;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import timber.log.Timber;

public class NativeFormsDataBinder {

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
            getDataLoader().loadForm(context, jsonObjectForm, baseEntityID);
            getDataLoader().bindNativeFormsMetaData(jsonObjectForm, context, baseEntityID);
            return jsonObjectForm;
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }
}
