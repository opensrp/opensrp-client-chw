package org.smartregister.chw.fragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.anc.domain.VisitDetail;

import java.util.HashMap;
import java.util.Map;

public class FamilyProfileActivityFragmentTest extends BaseUnitTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testStartWashCheckNativeForm() {
        FamilyProfileActivityFragment mockFragment = Mockito.mock(FamilyProfileActivityFragment.class);
        String date = "1600007095000";
        Map<String, VisitDetail> map = new HashMap<>();
        VisitDetail detail = new VisitDetail();
        detail.setHumanReadable("Yes");
        map.put("handwash_facility",detail);
        map.put("drinking_water_source",detail);
        map.put("clean_drinking_water",detail);

        try {
            Mockito.doReturn(getJsonFrom()).when(mockFragment).getWashCheckForm();
            Mockito.doReturn(map).when(mockFragment).queryWashCheckDetails(Long.parseLong(date),"123456");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONObject washCheckFields = mockFragment.populateWashCheckFields(Long.parseLong(date), "123456");
            JSONObject jsonObjectField = washCheckFields.getJSONObject("step1").getJSONArray("fields").getJSONObject(0);
            Assert.assertEquals(jsonObjectField.get("key").toString(), "handwash_facility");
            Assert.assertEquals(jsonObjectField.get("value").toString(), "Yes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject getJsonFrom() throws JSONException {
        return new JSONObject("{\n" +
                "  \"count\": \"1\",\n" +
                "  \"encounter_type\": \"WASH check\",\n" +
                "  \"entity_id\": \"\",\n" +
                "  \"step1\": {\n" +
                "    \"title\": \"{{wash_check.step1.title}}\",\n" +
                "    \"fields\": [\n" +
                "      {\n" +
                "        \"key\": \"handwash_facility\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"concept\",\n" +
                "        \"openmrs_entity_id\": \"164863AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "        \"openmrs_data_type\": \"select one\",\n" +
                "        \"type\": \"spinner\",\n" +
                "        \"label_info_title\": \"{{wash_check.step1.handwash_facility.label_info_title}}\",\n" +
                "        \"label_info_text\": \"{{wash_check.step1.handwash_facility.label_info_text}}\",\n" +
                "        \"hint\": \"{{wash_check.step1.handwash_facility.hint}}\",\n" +
                "        \"values\": [\n" +
                "          \"{{wash_check.step1.handwash_facility.values[0]}}\",\n" +
                "          \"{{wash_check.step1.handwash_facility.values[1]}}\"\n" +
                "        ],\n" +
                "        \"keys\": [\n" +
                "          \"Yes\",\n" +
                "          \"No\"\n" +
                "        ],\n" +
                "        \"openmrs_choice_ids\": {\n" +
                "          \"Yes\": \"1267AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"No\": \"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"v_required\": {\n" +
                "          \"value\": true,\n" +
                "          \"err\": \"{{wash_check.step1.handwash_facility.v_required.err}}\"\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"clean_drinking_water\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"concept\",\n" +
                "        \"openmrs_entity_id\": \"\",\n" +
                "        \"openmrs_data_type\": \"select one\",\n" +
                "        \"type\": \"spinner\",\n" +
                "        \"label_info_title\": \"{{wash_check.step1.clean_drinking_water.label_info_title}}\",\n" +
                "        \"label_info_text\": \"{{wash_check.step1.clean_drinking_water.label_info_text}}\",\n" +
                "        \"hint\": \"{{wash_check.step1.clean_drinking_water.hint}}\",\n" +
                "        \"values\": [\n" +
                "          \"{{wash_check.step1.clean_drinking_water.values[0]}}\",\n" +
                "          \"{{wash_check.step1.clean_drinking_water.values[1]}}\"\n" +
                "        ],\n" +
                "        \"keys\": [\n" +
                "          \"Yes\",\n" +
                "          \"No\"\n" +
                "        ],\n" +
                "        \"openmrs_choice_ids\": {\n" +
                "          \"Yes\": \"1267AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "          \"No\": \"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        },\n" +
                "        \"v_required\": {\n" +
                "          \"value\": true,\n" +
                "          \"err\": \"{{wash_check.step1.clean_drinking_water.v_required.err}}\"\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"drinking_water_source\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"concept\",\n" +
                "        \"openmrs_entity_id\": \"\",\n" +
                "        \"openmrs_data_type\": \"select one\",\n" +
                "        \"type\": \"spinner\",\n" +
                "        \"label_info_title\": \"{{wash_check.step1.drinking_water_source.label_info_title}}\",\n" +
                "        \"label_info_text\": \"{{wash_check.step1.drinking_water_source.label_info_text}}\",\n" +
                "        \"hint\": \"{{wash_check.step1.drinking_water_source.hint}}\",\n" +
                "        \"values\": [\n" +
                "          \"{{wash_check.step1.drinking_water_source.values[0]}}\",\n" +
                "          \"{{wash_check.step1.drinking_water_source.values[1]}}\",\n" +
                "          \"{{wash_check.step1.drinking_water_source.values[2]}}\",\n" +
                "          \"{{wash_check.step1.drinking_water_source.values[3]}}\",\n" +
                "          \"{{wash_check.step1.drinking_water_source.values[4]}}\",\n" +
                "          \"{{wash_check.step1.drinking_water_source.values[5]}}\"\n" +
                "        ],\n" +
                "        \"keys\": [\n" +
                "          \"Piped Water through tap\",\n" +
                "          \"Protected Dug Well\",\n" +
                "          \"Unprotected Well\",\n" +
                "          \"Rainwater harvesting\",\n" +
                "          \"Open surface water (River, Dam, Lake, Pond, Stream, Canal\",\n" +
                "          \"OTHER ( SPECIFY)\"\n" +
                "        ],\n" +
                "        \"relevance\": {\n" +
                "          \"step1:clean_drinking_water\": {\n" +
                "            \"type\": \"string\",\n" +
                "            \"ex\": \"equalTo(., \\\"Yes\\\")\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"properties_file_name\": \"wash_check\"\n" +
                "}");
    }
}
