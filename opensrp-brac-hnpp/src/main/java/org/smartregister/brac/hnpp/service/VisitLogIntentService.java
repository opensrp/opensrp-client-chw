package org.smartregister.brac.hnpp.service;

import android.app.IntentService;
import android.content.Intent;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.brac.hnpp.HnppApplication;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.brac.hnpp.utils.VisitLog;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.AssetHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.smartregister.brac.hnpp.utils.HnppConstants.EVENT_TYPE.ANC1_REGISTRATION;
import static org.smartregister.brac.hnpp.utils.HnppConstants.EVENT_TYPE.ANC2_REGISTRATION;
import static org.smartregister.brac.hnpp.utils.HnppConstants.EVENT_TYPE.ANC3_REGISTRATION;
import static org.smartregister.brac.hnpp.utils.HnppConstants.EVENT_TYPE.ANC_GENERAL_DISEASE;
import static org.smartregister.brac.hnpp.utils.HnppConstants.EVENT_TYPE.ANC_PREGNANCY_HISTORY;
import static org.smartregister.util.JsonFormUtils.gson;

public class VisitLogIntentService extends IntentService {

    public VisitLogIntentService() {
        super("VisitLogService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        SQLiteDatabase db = HnppApplication.getInstance().getRepository().getReadableDatabase();
        ArrayList<String> visit_ids = HnppApplication.getHNPPInstance().getHnppVisitLogRepository().getVisitIds();
        for (int i = 0; i < visit_ids.size(); i++) {
            List<Visit> v = AncLibrary.getInstance().visitRepository().getVisitsByVisitId(visit_ids.get(i));
            for (Visit visit : v) {
                String eventJson = visit.getJson();
                if (!StringUtils.isEmpty(eventJson)) {
                    try {

                        Event baseEvent = gson.fromJson(eventJson, Event.class);
                        JSONObject eventObject = new JSONObject(eventJson);
                        String base_entity_id = baseEvent.getBaseEntityId();
                        HashMap<String,Object>form_details = getFormNamesFromEventObject(baseEvent);
                        ArrayList<String> encounter_types = (ArrayList<String>) form_details.get("form_name");
                        HashMap<String,String>details = (HashMap<String, String>) form_details.get("details");
                        final CommonPersonObjectClient client = new CommonPersonObjectClient(base_entity_id, details, "");
                        client.setColumnmaps(details);
                        for (String encounter_type : encounter_types) {
                            JSONObject form_object = loadFormFromAsset(encounter_type);
                            JSONObject stepOne = form_object.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP1);
                            JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);
                            for (int k = 0; k < jsonArray.length(); k++) {
                                populateValuesForFormObject(client, jsonArray.getJSONObject(k));
                            }
                            VisitLog log = new VisitLog();
                            log.setVisitId(visit.getVisitId());
                            log.setVisitType("ANC_HOME_VISIT");
                            log.setBaseEntityId(base_entity_id);
                            log.setVisitDate(visit.getCreatedAt().getTime());
                            log.setEventType(encounter_type);
                            log.setVisitJson(form_object.toString());
                            HnppApplication.getHNPPInstance().getHnppVisitLogRepository().add(log);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void populateValuesForFormObject(CommonPersonObjectClient client, JSONObject jsonObject) {
        try {
            String value = org.smartregister.chw.core.utils.Utils.getValue(client.getColumnmaps(),jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY),false);
            //spinner
            if (jsonObject.has("openmrs_choice_ids")) {
                JSONObject choiceObject = jsonObject.getJSONObject("openmrs_choice_ids");

                for (int i = 0; i < choiceObject.names().length(); i++) {
                    if (value.equalsIgnoreCase(choiceObject.getString(choiceObject.names().getString(i)))) {
                        value = choiceObject.names().getString(i);
                    }
                }
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,value);
            }else if (jsonObject.has("options")) {
                JSONArray option_array = jsonObject.getJSONArray("options");
                for (int i = 0; i < option_array.length(); i++) {
                    JSONObject option = option_array.getJSONObject(i);
                    if (value.contains(option.optString("key"))) {
                        option.put("value", "true");
                    }
                }
            }else{
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject loadFormFromAsset(String encounter_type) {
        String form_name = "";
        if (ANC_PREGNANCY_HISTORY.equalsIgnoreCase(encounter_type)) {
            form_name = HnppConstants.HOME_VISIT_FORMS.PREGNANCY_HISTORY+".json";
        } else if (ANC_GENERAL_DISEASE.equalsIgnoreCase(encounter_type)) {
            form_name = HnppConstants.HOME_VISIT_FORMS.GENERAL_DISEASE+".json";
        } else if (ANC1_REGISTRATION.equalsIgnoreCase(encounter_type)) {
            form_name = HnppConstants.HOME_VISIT_FORMS.ANC1_FORM+".json";
        } else if (ANC2_REGISTRATION.equalsIgnoreCase(encounter_type)) {
            form_name = HnppConstants.HOME_VISIT_FORMS.ANC2_FORM+".json";
        } else if (ANC3_REGISTRATION.equalsIgnoreCase(encounter_type)) {
            form_name = HnppConstants.HOME_VISIT_FORMS.ANC3_FORM+".json";
        }
        String jsonString = AssetHandler.readFileFromAssetsFolder("json.form/"+form_name, VisitLogIntentService.this);
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public HashMap<String,Object> getFormNamesFromEventObject(Event baseEvent) {
        ArrayList<String> forms = new ArrayList<>();
        HashMap<String,Object>details = new HashMap<>();
        for (Obs o : baseEvent.getObs()) {
            if ("form_name".equalsIgnoreCase(o.getFormSubmissionField())) {
                forms.add(o.getFieldCode());
            }
            details.put(o.getFormSubmissionField(),o.getValue());
        }
        HashMap<String,Object>form_details = new HashMap<>();
        form_details.put("form_name",forms);
        form_details.put("details",details);
        return form_details;
    }
}
