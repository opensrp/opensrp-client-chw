package org.smartregister.chw.form_data;

import android.content.Context;

import com.google.gson.Gson;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.dao.EventDao;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.dao.AbstractDao;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.jsonmapping.ClassificationRule;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.ClientField;
import org.smartregister.domain.jsonmapping.Column;
import org.smartregister.domain.jsonmapping.Field;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.family.util.Utils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.util.AssetHandler;
import org.smartregister.view.LocationPickerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class NativeFormsDataLoader implements DataLoader {

    private ClientField clientField;
    private ClientClassification clientClassification;
    private Client client;
    private String eventName;
    private List<String> tableCache;
    private Map<String, Table> tableMap;
    protected JSONArray jsonArray;
    private Map<String, Map<String, Object>> dbData = new HashMap<>();

    private Event latestEvent;
    private Map<String, List<Obs>> obsMap;

    public ClientField getClientField(Context context) {
        if (clientField == null) {
            String file = AssetHandler.readFileFromAssetsFolder("ec_client_fields.json", context);
            clientField = new Gson().fromJson(file, ClientField.class);
        }
        return clientField;
    }

    public ClientClassification getClientClassification(Context context) {
        if (clientClassification == null) {
            String file = AssetHandler.readFileFromAssetsFolder("ec_client_classification.json", context);
            clientClassification = new Gson().fromJson(file, ClientClassification.class);
        }
        return clientClassification;
    }

    public Map<String, Map<String, Object>> getDbData(Context context, String baseEntityID, String eventName) {
        if (dbData == null) {
            dbData = new HashMap<>();
            List<String> tables = getFormTables(context, eventName);
            for (String table : tables) {
                Map<String, Object> results = getValues(context, table, baseEntityID);
                if (results != null)
                    dbData.put(table, results);
            }
        }
        return dbData;
    }

    @Override
    public List<String> getFormTables(Context context, String eventName) {
        if (!eventName.equalsIgnoreCase(this.eventName) || tableCache == null || tableCache.size() == 0) {
            ClientClassification clientClassification = getClientClassification(context);

            tableCache = new ArrayList<>();

            for (ClassificationRule rule : clientClassification.case_classification_rules) {
                for (Field field : rule.rule.fields) {
                    if ("eventType".equalsIgnoreCase(field.field) && field.field_value.equalsIgnoreCase(eventName)) {
                        if (field.creates_case != null)
                            tableCache.addAll(field.creates_case);

                        if (field.closes_case != null)
                            tableCache.addAll(field.closes_case);
                    }
                }
            }
            this.eventName = eventName;
        }
        return tableCache;
    }

    @Override
    public Map<String, Object> getValues(Context context, String tableName, String baseEntityID) {
        String sql = "select * from " + tableName + " where base_entity_id = '" + baseEntityID + "'";
        List<Map<String, Object>> value = AbstractDao.readData(sql, new String[]{});

        if (value != null && value.size() > 0)
            return value.get(0);

        return null;
    }

    @Override
    public CommonPersonObjectClient getClient(Context context, String baseEntityID) {
        // this object is returned regardless of the table passed
        CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);
        CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(baseEntityID);
        CommonPersonObjectClient client =
                new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
        client.setColumnmaps(commonPersonObject.getColumnmaps());

        return client;
    }

    @Override
    public void loadForm(Context context, JSONObject formJsonObject, String baseEntityID) throws JSONException {
        eventName = formJsonObject.optString(Constants.JSON_FORM_EXTRA.ENCOUNTER_TYPE);
        List<JSONObject> steps = JsonFormUtils.getFormSteps(formJsonObject);
        for (JSONObject step : steps) {
            JSONArray jsonArray = step.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);
            this.jsonArray = jsonArray;
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // get value of key
                    String value = getValue(context, baseEntityID, jsonObject, getDbData(context, baseEntityID, eventName));
                    if (StringUtils.isNotBlank(value))
                        jsonObject.put(JsonFormConstants.VALUE, value);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }
    }

    @Override
    public void bindNativeFormsMetaData(@NotNull JSONObject jsonObjectForm, Context context, String baseEntityID) throws JSONException {
        // baseEntityID
        jsonObjectForm.put(org.smartregister.family.util.JsonFormUtils.ENTITY_ID, baseEntityID);

        // metaData
        LocationPickerView lpv = new LocationPickerView(context);
        lpv.init();
        JSONObject metadata = jsonObjectForm.getJSONObject(org.smartregister.family.util.JsonFormUtils.METADATA);
        String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());
        metadata.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);
    }

    @Override
    public String getValue(Context context, String baseEntityID, JSONObject jsonObject, Map<String, Map<String, Object>> dbData) throws JSONException {
        String type = jsonObject.getString(JsonFormConstants.TYPE);
        String key = jsonObject.getString(JsonFormConstants.KEY);
        String entity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);

        if (!entity.contains("person") && (type.equalsIgnoreCase(JsonFormConstants.CHECK_BOX) || type.equalsIgnoreCase(JsonFormConstants.NATIVE_RADIO_BUTTON))) {
            readCheckBoxValue(baseEntityID, jsonObject, key);
        }

        String val = getExtractedValue(context, jsonObject, baseEntityID, dbData);
        return StringUtils.isBlank(val) ? getObsValue(baseEntityID, key, type) : val;
    }

    protected String getExtractedValue(Context context, JSONObject jsonObject, String baseEntityID, Map<String, Map<String, Object>> dbData) throws JSONException {
        String entity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String entity_id = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);

        preloadTables(context);
        if (entity.contains("person")) {
            return getValueFromPerson(baseEntityID, entity, entity_id);
        } else {
            return getValueFromConcept(entity_id, dbData);
        }
    }

    protected void readCheckBoxValue(String baseEntityID, JSONObject jsonObject, String key) throws JSONException {
        JSONArray optionsJson = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        List<Obs> obsList = getObs(baseEntityID, key);
        List<String> values = new ArrayList<>();
        if (obsList != null) {
            for (Obs o : obsList) {
                for (Object obj : o.getValues()) {
                    values.add((String) obj);
                }
            }
        }

        int x = optionsJson.length();
        while (x > 0) {
            JSONObject jo = optionsJson.getJSONObject(x - 1);
            if (jo.has(JsonFormConstants.OPENMRS_ENTITY_ID)) {
                String entityID = jo.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
                jo.put(JsonFormConstants.VALUE, values.contains(entityID) ? "true" : "false");
            }
            x--;
        }
    }

    protected void preloadTables(Context context) {
        if (tableMap == null) {
            tableMap = new HashMap<>();
            ClientField clientField = getClientField(context);
            if (clientField != null) {
                for (Table table : clientField.bindobjects) {
                    if (getFormTables(context, eventName).contains(table.name))
                        tableMap.put(table.name, table);
                }
            }
        }
    }

    protected Client getClient(String baseEntityId) {
        if (client == null || !client.getBaseEntityId().equalsIgnoreCase(baseEntityId)) {
            EventClientRepository db = CoreLibrary.getInstance().context().getEventClientRepository();
            client = db.fetchClientByBaseEntityId(baseEntityId);
        }
        return client;
    }

    protected String getValueFromPerson(String baseEntityID, String entity, String entityID) {
        Client client = getClient(baseEntityID);
        if (entity.contains("identifier")) {
            return client.getIdentifier(entityID);
        } else if (entity.contains("attribute")) {
            return (String) client.getAttribute(entityID);
        } else if (entity.contains("relationship")) {
            List<String> relations = client.getRelationships(entityID);
            if (relations.size() > 0) return relations.get(0);
        } else {
            return getClientAttribute(client, entityID);
        }
        return "";
    }

    private String getClientAttribute(Client client, String attributeName) {
        String attribute = attributeName.trim().toLowerCase().replace("_", "");

        switch (attribute) {
            case "baseentityid":
                return client.getBaseEntityId();
            case "firstname":
                return client.getFirstName();
            case "middlename":
                return client.getMiddleName();
            case "lastname":
                return client.getLastName();
            case "birthdate":
                return getNativeFormsDate(client.getBirthdate());
            case "deathdate":
                return getNativeFormsDate(client.getDeathdate());
            case "birthdateapprox":
                return client.getBirthdateApprox().toString();
            case "deathdateapprox":
                return client.getDeathdateApprox().toString();
            case "gender":
                return client.getGender();
            default:
                return "";
        }
    }

    private String getNativeFormsDate(DateTime date) {
        if (date == null) return "";

        return JsonFormUtils.dd_MM_yyyy.format(date.toDate());
    }

    private String getValueFromConcept(String entityID, Map<String, Map<String, Object>> dbData) {
        for (Map.Entry<String, Table> entry : tableMap.entrySet()) {
            for (Column column : entry.getValue().columns) {
                if ("Event".equalsIgnoreCase(column.type) &&
                        "obs.fieldCode".equalsIgnoreCase(column.json_mapping.field) &&
                        column.json_mapping.concept.equalsIgnoreCase(entityID)
                ) {
                    Map<String, Object> map = dbData.get(entry.getKey());
                    return map != null ? (String) map.get(column.column_name) : "";
                }
            }
        }
        return null;
    }

    protected List<String> getEventTypes() {
        return new ArrayList<>();
    }

    protected Event getEvent(String baseEntityID) {
        if (latestEvent == null)
            latestEvent = EventDao.getLatestEvent(baseEntityID, getEventTypes());

        return latestEvent;
    }

    protected List<Obs> getObs(String baseEntityID, String key) {
        if (obsMap == null) {
            obsMap = new HashMap<>();

            Event event = getEvent(baseEntityID);
            if (event == null)
                return new ArrayList<>();

            for (Obs obs : event.getObs()) {
                List<Obs> obsList = obsMap.get(obs.getFormSubmissionField());
                if (obsList == null)
                    obsList = new ArrayList<>();

                obsList.add(obs);
                obsMap.put(obs.getFormSubmissionField(), obsList);
            }
        }

        return obsMap.get(key);
    }

    protected String getObsValue(String baseEntityID, String key, String type) {
        List<Obs> obsList = getObs(baseEntityID, key);
        if (obsList == null || obsList.size() == 0)
            return null;

        switch (type) {
            case JsonFormConstants.SPINNER:
            case JsonFormConstants.NATIVE_RADIO_BUTTON:
                return (String) obsList.get(0).getHumanReadableValues().get(0);
            case JsonFormConstants.CHECK_BOX:
                StringBuilder builder = new StringBuilder();
                for (Obs o : obsList) {
                    if (builder.length() > 0)
                        builder.append(",");

                    builder.append("'").append(o.getValue()).append("'");
                }
                return "[" + builder.toString() + "]";
            default:
                return (String) obsList.get(0).getValue();
        }
    }
}
