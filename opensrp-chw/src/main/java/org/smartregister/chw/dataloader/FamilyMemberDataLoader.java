package org.smartregister.chw.dataloader;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.form_data.NativeFormsDataLoader;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.chw.util.Utils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.domain.Photo;
import org.smartregister.domain.db.Client;
import org.smartregister.family.util.DBConstants;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.ImageUtils;
import org.smartregister.view.LocationPickerView;

import java.util.Map;

public class FamilyMemberDataLoader extends NativeFormsDataLoader {
    private String familyName;
    private boolean isPrimaryCaregiver;
    private String eventType;

    public FamilyMemberDataLoader(String familyName, boolean isPrimaryCaregiver) {
        this.familyName = familyName;
        this.isPrimaryCaregiver = isPrimaryCaregiver;
    }

    @Override
    public String getValue(Context context, String baseEntityID, JSONObject jsonObject, Map<String, Map<String, Object>> dbData) throws JSONException {
        String key = jsonObject.getString(JsonFormConstants.KEY);
        Client client = getClient(baseEntityID);
        switch (key) {
            case org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN:
                computeDOBUnknown(context, baseEntityID, jsonObject, dbData);
                break;

            case Constants.JsonAssets.AGE:
                return getAge(client);

            case DBConstants.KEY.DOB:
                return getDOB(client);

            case org.smartregister.family.util.Constants.KEY.PHOTO:
                return getPhoto(baseEntityID);

            case DBConstants.KEY.UNIQUE_ID:
                computeID(context, baseEntityID, jsonObject, dbData);
                break;

            case Constants.JsonAssets.PREGNANT_1_YR:
                computePregnantOneYr(jsonObject, null);
                break;

            case Constants.JsonAssets.FAM_NAME:
                computeFamName(client, jsonObject, jsonArray, familyName);
                break;

            case Constants.JsonAssets.PRIMARY_CARE_GIVER:
            case Constants.JsonAssets.IS_PRIMARY_CARE_GIVER:
                computePrimaryCareGiver(jsonObject, isPrimaryCaregiver);
                break;

            case Constants.JsonAssets.SERVICE_PROVIDER:
                computeServiceProvider(jsonObject, null);
                break;

            default:
                return super.getValue(context, baseEntityID, jsonObject, dbData);

        }

        return super.getValue(context, baseEntityID, jsonObject, dbData);
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

        jsonObjectForm.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE, eventType);
        /*
        form.put(org.smartregister.family.util.JsonFormUtils.CURRENT_OPENSRP_ID, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false));

        if (StringUtils.isNotBlank(title)) {
            stepOne.put("title", title);
        }
*/
    }

    private void computeDOBUnknown(Context context, String baseEntityID, JSONObject jsonObject, Map<String, Map<String, Object>> dbData) throws JSONException {
        String val = super.getValue(context, baseEntityID, jsonObject, dbData);
        jsonObject.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, false);
        JSONObject optionsObject = jsonObject.getJSONArray(org.smartregister.family.util.Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
        optionsObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, val);
    }

    private String getAge(Client client) {
        return String.valueOf(Years.yearsBetween(new DateTime(), client.getBirthdate()).getYears());
    }

    private String getDOB(Client client) {
        return JsonFormUtils.dd_MM_yyyy.format(client.getBirthdate().toDate());
    }

    private String getPhoto(String baseEntityID) {
        Photo photo = ImageUtils.profilePhotoByClientID(baseEntityID, Utils.getProfileImageResourceIDentifier());
        if (StringUtils.isNotBlank(photo.getFilePath())) {
            return photo.getFilePath();
        }
        return "";
    }

    private void computeID(Context context, String baseEntityID, JSONObject jsonObject, Map<String, Map<String, Object>> dbData) throws JSONException {
        String uniqueId = super.getValue(context, baseEntityID, jsonObject, dbData);
        jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, uniqueId.replace("-", ""));
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

    private void computeFamName(Client client, JSONObject jsonObject, JSONArray jsonArray, String familyName) throws JSONException {
        final String SAME_AS_FAM_NAME = "same_as_fam_name";
        final String SURNAME = "surname";

        jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, familyName);

        String lastName = client.getLastName();

        JSONObject sameAsFamName = org.smartregister.util.JsonFormUtils.getFieldJSONObject(jsonArray, SAME_AS_FAM_NAME);
        JSONObject sameOptions = sameAsFamName.getJSONArray(org.smartregister.family.util.Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);

        if (familyName.equals(lastName)) {
            sameOptions.put(org.smartregister.family.util.JsonFormUtils.VALUE, true);
        } else {
            sameOptions.put(org.smartregister.family.util.JsonFormUtils.VALUE, false);
        }

        JSONObject surname = org.smartregister.util.JsonFormUtils.getFieldJSONObject(jsonArray, SURNAME);
        if (!familyName.equals(lastName)) {
            surname.put(org.smartregister.family.util.JsonFormUtils.VALUE, lastName);
        } else {
            surname.put(org.smartregister.family.util.JsonFormUtils.VALUE, "");
        }
    }

    private void computePrimaryCareGiver(JSONObject jsonObject, boolean isPrimaryCaregiver) throws JSONException {
        jsonObject.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, true);
        String val = isPrimaryCaregiver ? "Yes" : "No";
        jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, val);
    }

    private void computeServiceProvider(JSONObject jsonObject, Event ecEvent) throws JSONException {

        // iterate and update all options wit the values from ec object
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
}
