package org.smartregister.chw.core.utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Pair;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Photo;
import org.smartregister.family.util.DBConstants;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.FormUtils;
import org.smartregister.util.ImageUtils;
import org.smartregister.view.LocationPickerView;

import java.util.Date;
import java.util.HashMap;

import timber.log.Timber;

import static org.smartregister.util.AssetHandler.jsonStringToJava;

public class BAJsonFormUtils {
    private static final String TITLE = "title";
    private HashMap<String, String> JSON_DB_MAP;
    private CoreChwApplication coreChwApplication;

    public BAJsonFormUtils(CoreChwApplication coreChwApplication) {
        this.coreChwApplication = coreChwApplication;
        JSON_DB_MAP = new HashMap<>();
        JSON_DB_MAP.put(CoreConstants.JsonAssets.SEX, DBConstants.KEY.GENDER);
        JSON_DB_MAP.put(CoreConstants.JsonAssets.NATIONAL_ID, CoreConstants.JsonAssets.NATIONAL_ID);
        JSON_DB_MAP.put(CoreConstants.JsonAssets.VOTER_ID, ChwDBConstants.VOTER_ID);
        JSON_DB_MAP.put(CoreConstants.JsonAssets.DRIVER_LICENSE, ChwDBConstants.DRIVER_LICENSE);
        JSON_DB_MAP.put(CoreConstants.JsonAssets.PASSPORT, ChwDBConstants.PASSPORT);
        JSON_DB_MAP.put(CoreConstants.JsonAssets.INSURANCE_PROVIDER, ChwDBConstants.INSURANCE_PROVIDER);
        JSON_DB_MAP.put(CoreConstants.JsonAssets.INSURANCE_PROVIDER_OTHER, ChwDBConstants.INSURANCE_PROVIDER_OTHER);
        JSON_DB_MAP.put(CoreConstants.JsonAssets.INSURANCE_PROVIDER_NUMBER, ChwDBConstants.INSURANCE_PROVIDER_NUMBER);
        JSON_DB_MAP.put(CoreConstants.JsonAssets.DISABILITIES, ChwDBConstants.DISABILITIES);
        JSON_DB_MAP.put(CoreConstants.JsonAssets.DISABILITY_TYPE, ChwDBConstants.DISABILITY_TYPE);
        JSON_DB_MAP.put(CoreConstants.JsonAssets.OTHER_LEADER, ChwDBConstants.OTHER_LEADER);
    }

    public JSONObject getAutoJsonEditMemberFormString(String title, String formName, Context context, CommonPersonObjectClient client, String eventType, String familyName, boolean isPrimaryCaregiver) {
        try {

            // get the event and the client from ec model
            Pair<Event, Client> eventClientPair = getEditMemberLatestProperties(client.getCaseId());


            JSONObject form = FormUtils.getInstance(context).getFormJson(formName);
            LocationPickerView lpv = new LocationPickerView(context);
            lpv.init();
            // JsonFormUtils.addWomanRegisterHierarchyQuestions(form);
            if (form != null) {
                form.put(org.smartregister.family.util.JsonFormUtils.ENTITY_ID, client.getCaseId());
                form.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE, eventType);

                JSONObject metadata = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.METADATA);
                String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());

                metadata.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);

                form.put(org.smartregister.family.util.JsonFormUtils.CURRENT_OPENSRP_ID, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false));

                //inject opensrp id into the form
                JSONObject stepOne = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP1);

                if (StringUtils.isNotBlank(title)) {
                    stepOne.put(TITLE, title);
                }

                JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    try {
                        processFieldsForMemberEdit(client, jsonObject, jsonArray, familyName, isPrimaryCaregiver, eventClientPair.first, eventClientPair.second);
                    } catch (Exception e) {
                        Timber.e(e);
                    }

                }

                return form;
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }

    private Pair<Event, Client> getEditMemberLatestProperties(String baseEntityID) {

        Event ecEvent = null;
        Client ecClient = null;


        String query_client = "select json from client where baseEntityId = ? order by updatedAt desc";
        try (Cursor cursor = coreChwApplication.getRepository().getReadableDatabase().rawQuery(query_client, new String[]{baseEntityID})) {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                ecClient = jsonStringToJava(cursor.getString(0), Client.class);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Timber.e(e);
        }


        String query_event = String.format("select json from event where baseEntityId = '%s' and eventType in ('%s','%s') order by updatedAt desc limit 1;",
                baseEntityID, CoreConstants.EventType.UPDATE_FAMILY_MEMBER_REGISTRATION, CoreConstants.EventType.FAMILY_MEMBER_REGISTRATION);

        try (Cursor cursor1 = coreChwApplication.getRepository().getReadableDatabase().rawQuery(query_event, new String[]{})) {
            cursor1.moveToFirst();

            while (!cursor1.isAfterLast()) {
                ecEvent = jsonStringToJava(cursor1.getString(0), Event.class);
                cursor1.moveToNext();
            }
        } catch (Exception e) {
            Timber.e(e, e.toString());
        }

        return Pair.create(ecEvent, ecClient);
    }

    protected void processFieldsForMemberEdit(CommonPersonObjectClient client, JSONObject jsonObject, JSONArray jsonArray, String familyName, boolean isPrimaryCaregiver, Event ecEvent, Client ecClient) throws JSONException {


        switch (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).toLowerCase()) {
            case org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN:
                computeDOBUnknown(jsonObject, client);
                break;
            case CoreConstants.JsonAssets.AGE:
                computeAge(jsonObject, client);
                break;
            case DBConstants.KEY.DOB:
                computeDOB(jsonObject, client);
                break;

            case org.smartregister.family.util.Constants.KEY.PHOTO:
                computePhoto(jsonObject, client);
                break;

            case DBConstants.KEY.UNIQUE_ID:
                String uniqueId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false);
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, uniqueId.replace("-", ""));
                break;

            case CoreConstants.JsonAssets.PREGNANT_1_YR:
                computePregnantOneYr(jsonObject, ecEvent);
                break;

            case CoreConstants.JsonAssets.SURNAME:
                computeSurname(jsonObject, ecClient, familyName);
                break;

            case CoreConstants.JsonAssets.FAM_NAME:
                computeFamName(client, jsonObject, jsonArray, ecClient, familyName);
                break;

            case CoreConstants.JsonAssets.PRIMARY_CARE_GIVER:
            case CoreConstants.JsonAssets.IS_PRIMARY_CARE_GIVER:
                computePrimaryCareGiver(jsonObject, isPrimaryCaregiver);
                break;

            case CoreConstants.JsonAssets.ID_AVAIL:
                computeIDAvail(jsonObject, ecClient);
                break;

            case CoreConstants.JsonAssets.SERVICE_PROVIDER:
                computeServiceProvider(jsonObject, ecEvent);
                break;

            case CoreConstants.JsonAssets.LEADER:
                computeLeader(jsonObject, ecClient);
                break;

            default:
                String db_key = JSON_DB_MAP.get(jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).toLowerCase());
                if (StringUtils.isNotBlank(db_key)) {
                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), db_key, false));
                } else {
                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY), false));
                }
                break;

        }
    }

    private void computeDOBUnknown(JSONObject jsonObject, CommonPersonObjectClient client) throws JSONException {
        jsonObject.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, false);
        JSONObject optionsObject = jsonObject.getJSONArray(org.smartregister.family.util.Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
        optionsObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN, false));
    }

    private void computeAge(JSONObject jsonObject, CommonPersonObjectClient client) throws JSONException {
        String dobString = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
        dobString = org.smartregister.family.util.Utils.getDuration(dobString);
        dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : "0";
        jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Integer.valueOf(dobString));
    }

    private void computeDOB(JSONObject jsonObject, CommonPersonObjectClient client) throws JSONException {

        String dobString = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
        if (StringUtils.isNotBlank(dobString)) {
            Date dob = Utils.dobStringToDate(dobString);
            if (dob != null) {
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, CoreJsonFormUtils.dd_MM_yyyy.format(dob));
            }
        }
    }

    private void computePhoto(JSONObject jsonObject, CommonPersonObjectClient client) throws JSONException {

        Photo photo = ImageUtils.profilePhotoByClientID(client.getCaseId(), Utils.getProfileImageResourceIDentifier());
        if (StringUtils.isNotBlank(photo.getFilePath())) {
            jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, photo.getFilePath());
        }
    }

    private void computePregnantOneYr(JSONObject jsonObject, Event ecEvent) throws JSONException {
        if (ecEvent != null) {
            String id = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
            for (Obs obs : ecEvent.getObs()) {
                if (obs.getValues() != null && obs.getFieldCode().contains(id)) {
                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, obs.getHumanReadableValues().get(0));
                }
            }
        }
    }

    private void computeSurname(JSONObject jsonObject, Client ecClient, String familyName) throws JSONException {
        if (ecClient != null) {
            jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,
                    (ecClient.getLastName() == null ? familyName : ecClient.getLastName()));
            jsonObject.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, true);
        }
    }

    private void computeFamName(CommonPersonObjectClient client, JSONObject jsonObject, JSONArray jsonArray, Client ecClient, String familyName) throws JSONException {

        if (ecClient.getLastName() != null) {

            final String SAME_AS_FAM_NAME = "same_as_fam_name";
            final String SURNAME = "surname";

            jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, familyName);


            String lastName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, false);

            JSONObject sameAsFamName = CoreJsonFormUtils.getFieldJSONObject(jsonArray, SAME_AS_FAM_NAME);
            JSONObject sameOptions = sameAsFamName.getJSONArray(org.smartregister.family.util.Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);

            if (familyName.equals(lastName)) {
                sameOptions.put(org.smartregister.family.util.JsonFormUtils.VALUE, true);
            } else {
                sameOptions.put(org.smartregister.family.util.JsonFormUtils.VALUE, false);
            }

            JSONObject surname = CoreJsonFormUtils.getFieldJSONObject(jsonArray, SURNAME);
            if (!familyName.equals(lastName)) {
                surname.put(org.smartregister.family.util.JsonFormUtils.VALUE, lastName);
                surname.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, false);
            } else {
                surname.put(org.smartregister.family.util.JsonFormUtils.VALUE, "");
                surname.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, true);
            }

        }
    }

    private void computePrimaryCareGiver(JSONObject jsonObject, boolean isPrimaryCaregiver) throws JSONException {
        if (isPrimaryCaregiver) {
            jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, "Yes");
            jsonObject.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, true);
        } else {
            jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, "No");
        }
    }

    private void computeIDAvail(JSONObject jsonObject, Client ecClient) throws JSONException {

        if (ecClient != null) {
            for (int i = 0; i < jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).length(); i++) {
                JSONObject obj = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).getJSONObject(i);
                String key = obj.getString(JsonFormConstants.KEY);

                String val = (String) ecClient.getAttribute("id_avail");

                if (val != null && key != null && val.contains(key)) {
                    obj.put(JsonFormConstants.VALUE, true);
                } else {
                    obj.put(JsonFormConstants.VALUE, false);
                }
            }
        }
    }

    private void computeServiceProvider(JSONObject jsonObject, Event ecEvent) throws JSONException {
        if (ecEvent != null) {
            for (int i = 0; i < jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).length(); i++) {
                JSONObject obj = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).getJSONObject(i);
                String id = obj.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
                for (Obs obs : ecEvent.getObs()) {
                    if (obs.getValues() != null && obs.getValues().contains(id)) {
                        obj.put(JsonFormConstants.VALUE, true);
                    }
                }
            }
        }
    }

    private void computeLeader(JSONObject jsonObject, Client ecClient) throws JSONException {
        if (ecClient != null) {
            for (int i = 0; i < jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).length(); i++) {
                JSONObject obj = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).getJSONObject(i);
                String key = obj.getString(JsonFormConstants.KEY);

                String val = (String) ecClient.getAttribute("Community_Leader");

                if (val != null && key != null && val.contains(key)) {
                    obj.put(JsonFormConstants.VALUE, true);
                } else {
                    obj.put(JsonFormConstants.VALUE, false);
                }
            }
        }
    }

}
