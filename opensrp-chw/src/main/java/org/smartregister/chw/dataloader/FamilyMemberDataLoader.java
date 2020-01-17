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
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.form_data.NativeFormsDataLoader;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.chw.util.Utils;
import org.smartregister.domain.Photo;
import org.smartregister.domain.db.Client;
import org.smartregister.family.util.DBConstants;
import org.smartregister.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FamilyMemberDataLoader extends NativeFormsDataLoader {
    private String familyName;
    private boolean isPrimaryCaregiver;
    private String title;
    private String eventType;
    private String uniqueID;

    public FamilyMemberDataLoader(String familyName, boolean isPrimaryCaregiver, String title, String eventType, String uniqueID) {
        this.familyName = familyName;
        this.isPrimaryCaregiver = isPrimaryCaregiver;
        this.title = title;
        this.eventType = eventType;
        this.uniqueID = uniqueID;
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
                return String.valueOf(Years.yearsBetween(client.getBirthdate(), new DateTime()).getYears());

            case DBConstants.KEY.DOB:
                return JsonFormUtils.dd_MM_yyyy.format(client.getBirthdate().toDate());

            case org.smartregister.family.util.Constants.KEY.PHOTO:
                return getPhoto(baseEntityID);

            case DBConstants.KEY.UNIQUE_ID:
                return super.getValue(context, baseEntityID, jsonObject, dbData)
                        .replace("-", "");

            case Constants.JsonAssets.FAM_NAME:
                computeFamName(client, jsonObject, jsonArray, familyName);
                break;

            case Constants.JsonAssets.PRIMARY_CARE_GIVER:
            case Constants.JsonAssets.IS_PRIMARY_CARE_GIVER:
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, true);
                return isPrimaryCaregiver ? "Yes" : "No";

            default:
                return super.getValue(context, baseEntityID, jsonObject, dbData);

        }

        return super.getValue(context, baseEntityID, jsonObject, dbData);
    }

    @Override
    public void bindNativeFormsMetaData(@NotNull JSONObject jsonObjectForm, Context context, String baseEntityID) throws JSONException {
        super.bindNativeFormsMetaData(jsonObjectForm, context, baseEntityID);

        jsonObjectForm.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE, eventType);
        if (StringUtils.isNotBlank(uniqueID))
            jsonObjectForm.put("current_opensrp_id", uniqueID);

        Map<String, Map<String, Object>> dbVals = getDbData(context, baseEntityID, eventType);
        if (dbVals != null) {
            for (Map.Entry<String, Map<String, Object>> entry : dbVals.entrySet()) {
                String val = (String) entry.getValue().get(DBConstants.KEY.UNIQUE_ID);
                if (StringUtils.isNotBlank(val))
                    jsonObjectForm.put(org.smartregister.family.util.JsonFormUtils.CURRENT_OPENSRP_ID, val);
            }
        }

        JSONObject stepOne = jsonObjectForm.getJSONObject("step1");
        if (StringUtils.isNotBlank(title))
            stepOne.put("title", title);
    }

    private void computeDOBUnknown(Context context, String baseEntityID, JSONObject jsonObject, Map<String, Map<String, Object>> dbData) throws JSONException {
        String val = super.getValue(context, baseEntityID, jsonObject, dbData);
        jsonObject.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, false);
        JSONObject optionsObject = jsonObject.getJSONArray(org.smartregister.family.util.Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
        optionsObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, val);
    }

    private String getPhoto(String baseEntityID) {
        Photo photo = ImageUtils.profilePhotoByClientID(baseEntityID, Utils.getProfileImageResourceIDentifier());
        if (StringUtils.isNotBlank(photo.getFilePath())) {
            return photo.getFilePath();
        }
        return "";
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

    @Override
    protected List<String> getEventTypes() {
        List<String> res = new ArrayList<>();
        res.add(CoreConstants.EventType.FAMILY_MEMBER_REGISTRATION);
        res.add(CoreConstants.EventType.UPDATE_FAMILY_MEMBER_REGISTRATION);
        return res;
    }

}
