package org.smartgresiter.wcaro.model;

import android.util.Pair;

import org.json.JSONObject;
import org.smartgresiter.wcaro.contract.ChildProfileContract;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

public class ChildProfileModel implements ChildProfileContract.Model {
    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        return null;
    }

    @Override
    public Pair<Client, Event> processMemberRegistration(String jsonString, String familyBaseEntityId) {
        return null;
    }
}
