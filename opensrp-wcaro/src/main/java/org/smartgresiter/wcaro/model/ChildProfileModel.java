package org.smartgresiter.wcaro.model;

import android.util.Log;
import android.util.Pair;

import org.json.JSONObject;
import org.smartgresiter.wcaro.contract.ChildProfileContract;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.family.util.Utils;
import org.smartregister.util.FormUtils;

public class ChildProfileModel implements ChildProfileContract.Model {

    private FormUtils formUtils;
    private String familyName;

    public ChildProfileModel(String familyName) {
        this.familyName = familyName;
    }

    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId, String familyID) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        form = JsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId, familyID);
        if (formName.equals(Constants.JSON_FORM.CHILD_REGISTER)) {
            JsonFormUtils.updateJsonForm(form, familyName);
        }

        return form;
    }

    @Override
    public Pair<Client, Event> processMemberRegistration(String jsonString, String familyBaseEntityId) {
        return null;
    }


    private FormUtils getFormUtils() {

        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(Utils.context().applicationContext());
            } catch (Exception e) {
                Log.e(ChildRegisterModel.class.getCanonicalName(), e.getMessage(), e);
            }
        }
        return formUtils;
    }
}
