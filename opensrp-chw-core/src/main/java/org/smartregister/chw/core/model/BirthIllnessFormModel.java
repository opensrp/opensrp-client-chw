package org.smartregister.chw.core.model;

import android.util.Pair;

import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

public class BirthIllnessFormModel {
    private String jsonString;
    private Pair<Client, Event> pair;

    public BirthIllnessFormModel(String jsonString, Pair<Client, Event> pair) {
        this.jsonString = jsonString;
        this.pair = pair;
    }

    public String getJsonString() {
        return jsonString;
    }

    public Pair<Client, Event> getPair() {
        return pair;
    }

}
