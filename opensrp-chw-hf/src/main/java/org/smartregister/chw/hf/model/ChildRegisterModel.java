package org.smartregister.chw.hf.model;

import android.util.Pair;

import org.json.JSONObject;
import org.smartregister.chw.core.model.CoreChildRegisterModel;
import org.smartregister.chw.hf.utils.JsonFormUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.family.util.Utils;

public class ChildRegisterModel extends CoreChildRegisterModel {

    @Override
    public Pair<Client, Event> processRegistration(String jsonString) {
        return JsonFormUtils.processChildRegistrationForm(Utils.context().allSharedPreferences(), jsonString);
    }

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId, String familyID) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        return JsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId, familyID);
    }
}
