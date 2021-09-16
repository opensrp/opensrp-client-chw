package org.smartregister.chw.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.vijay.jsonwizard.domain.Form;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Photo;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.util.FormUtils;
import org.smartregister.util.ImageUtils;
import org.smartregister.view.LocationPickerView;
import java.util.Date;
import timber.log.Timber;
import static org.smartregister.util.JsonFormUtils.dd_MM_yyyy;

public class JsonFormUtilsFlv extends DefaultJsonFormUtilsFlv {

    public static void startFormActivity(Context context, JSONObject jsonForm, String title) {
        Intent intent = new Intent(context, Utils.metadata().familyFormActivity);
        intent.putExtra("json", jsonForm.toString());
        Form form = new Form();
        form.setHideSaveLabel(true);
        form.setName(title);
        form.setActionBarBackground(org.smartregister.chw.core.R.color.family_actionbar);
        form.setNavigationBackground(org.smartregister.chw.core.R.color.family_navigation);
        form.setHomeAsUpIndicator(org.smartregister.chw.core.R.mipmap.ic_cross_white);
        form.setPreviousLabel(context.getResources().getString(org.smartregister.chw.core.R.string.back));
        intent.putExtra("form", form);
        ((Activity) context).startActivityForResult(intent, 2244);
    }

    public static JSONObject getAutoPopulatedJsonEditFormString(String formName, Context context, CommonPersonObjectClient client,
                                                                String eventType) {
        try {
            JSONObject form = FormUtils.getInstance(context).getFormJson(formName);
            LocationPickerView lpv = new LocationPickerView(context);
            lpv.init();
            // JsonFormUtils.addWomanRegisterHierarchyQuestions(form);
            Timber.d("Form is %s", form.toString());
            form.put(JsonFormUtils.ENTITY_ID, client.getCaseId());
            form.put(JsonFormUtils.ENCOUNTER_TYPE, eventType);

            JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
            JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                processPopulatableFields(client, jsonObject);

            }

            JsonFormUtils.addLocHierarchyQuestions(form);

            return form;
        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }

    private static void getDob(CommonPersonObjectClient client, JSONObject jsonObject) throws JSONException {
        String dobString = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
        if (StringUtils.isNotBlank(dobString)) {
            Date dob = Utils.dobStringToDate(dobString);
            if (dob != null) {
                jsonObject.put(JsonFormUtils.VALUE, dd_MM_yyyy.format(dob));
            }
        }
    }

    private static void getPhoto(CommonPersonObjectClient client, JSONObject jsonObject) throws JSONException {
        Photo photo = ImageUtils.profilePhotoByClientID(client.getCaseId(), Utils.getProfileImageResourceIDentifier());
        if (StringUtils.isNotBlank(photo.getFilePath())) {
            jsonObject.put(JsonFormUtils.VALUE, photo.getFilePath());
        }
    }

    protected static void processPopulatableFields(CommonPersonObjectClient client, JSONObject jsonObject) throws JSONException {

        String key = jsonObject.getString(JsonFormUtils.KEY).toLowerCase();
        switch (key) {
            case "photo":
                getPhoto(client, jsonObject);
                break;
            case "name":
                String name = Utils.getValue(client.getColumnmaps(), "name", false);
                jsonObject.put(JsonFormUtils.VALUE, name);
                break;
            case DBConstants.KEY.DOB:
                getDob(client, jsonObject);
                break;
            default:
                String otherKey = Utils.getValue(client.getColumnmaps(), key, false);
                jsonObject.put(JsonFormUtils.VALUE, otherKey);
                break;
        }
    }
}
