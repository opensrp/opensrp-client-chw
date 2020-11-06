package org.smartregister.chw.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class JsonFormUtilsTest {

    @Test
    public void getTimeZone() {
        String timeZone = JsonFormUtils.getTimeZone();
        boolean matches = timeZone.matches("^\\+\\d\\d:\\d0$");
        assert (matches);
    }

    @Test
    public void testGetValue() {
        JSONObject jsonObject = null;
        String key = "exclusive_breast_feeding";
        try {
             jsonObject = new JSONObject("{\"count\":\"1\",\"encounter_type\":\"Exclusive breast feeding\",\"entity_id\":\"1234567890\",\"metadata\":{\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"openmrs_entity_id\"},\"encounter_location\":\"\"},\"step1\":{\"title\":\"Exclusive breastfeeding\",\"fields\":[{\"key\":\"exclusive_breast_feeding\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"exclusive_breast_feeding\",\"type\":\"spinner\",\"image\":\"ic_form_bf\",\"hint\":\"Did the child receive any liquid or food other than breast milk yesterday and last night?\",\"values\":[\"Yes\",\"No\"],\"openmrs_choice_ids\":{\"Yes\":\"key_yes\",\"No\":\"key_no\"},\"value\":\"Yes\"}]}}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("Yes", JsonFormUtils.getValue(jsonObject, key));
    }
}
