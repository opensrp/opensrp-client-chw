package org.smartregister.brac.hnpp.location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Address;

import java.io.Serializable;
import java.util.HashMap;

public class SSLocations implements Serializable {
    public BaseLocation division;
    public BaseLocation country;
    public BaseLocation district;
    public BaseLocation city_corporation_upazila;
    public BaseLocation pourasabha;
    public BaseLocation union_ward;
    public BaseLocation village;
//
//    public JSONArray getAddressField() {
//
//        JSONArray jsonArray = new JSONArray();
//        try {
//
//
//            JSONObject object = new JSONObject();
//            object.put("addressType", "usual_residence");
//            JSONObject fieldObjects = new JSONObject();
//            fieldObjects.put("country", country.name);
//            fieldObjects.put("address1", union_ward.name);
//            fieldObjects.put("address2", city_corporation_upazila.name);
//            fieldObjects.put("address3", pourosava.name);
//            fieldObjects.put("address7", mouza.name);
//            fieldObjects.put("cityVillage", village.name);
//            fieldObjects.put("stateProvince", division.name);
//            fieldObjects.put("countyDistrict", district.name);
//            object.put("addressFields", fieldObjects);
//            jsonArray.put(object);
//        } catch (JSONException e) {
//
//        }
//
//        return jsonArray;
//    }

}
