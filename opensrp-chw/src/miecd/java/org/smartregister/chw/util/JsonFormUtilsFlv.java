package org.smartregister.chw.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.BAJsonFormUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class JsonFormUtilsFlv extends BAJsonFormUtils implements JsonFormUtils.Flavor {

    public JsonFormUtilsFlv() {
        super(ChwApplication.getInstance());
    }

    @Override
    public JSONObject getAutoJsonEditMemberFormString(String title, String formName, Context context, CommonPersonObjectClient client, String eventType, String familyName, boolean isPrimaryCaregiver) {
        return super.getAutoJsonEditMemberFormString(title, formName, context, client, eventType, familyName, isPrimaryCaregiver);
    }

    @Override
    public void processFieldsForMemberEdit(CommonPersonObjectClient client, JSONObject jsonObject, JSONArray jsonArray, String familyName, boolean isPrimaryCaregiver, Event ecEvent, Client ecClient) throws JSONException {
        super.processFieldsForMemberEdit(client, jsonObject, jsonArray, familyName, isPrimaryCaregiver, ecEvent, ecClient);
    }
}
