package org.smartregister.chw.activity;

import static org.smartregister.chw.core.utils.FormUtils.getFormUtils;
import static org.smartregister.chw.util.CrvsConstants.BASE_ENTITY_ID;
import static org.smartregister.chw.util.CrvsConstants.CLIENT_TYPE;
import static org.smartregister.chw.util.CrvsConstants.DEATH_CERTIFICATE_ISSUE_DATE;
import static org.smartregister.chw.util.CrvsConstants.DEATH_CERTIFICATE_NUMBER;
import static org.smartregister.chw.util.CrvsConstants.DEATH_FORM;
import static org.smartregister.chw.util.CrvsConstants.DEATH_MEMBER_FORM;
import static org.smartregister.chw.util.CrvsConstants.DEATH_NOTIFICATION_DONE;
import static org.smartregister.chw.util.CrvsConstants.DOB;
import static org.smartregister.chw.util.CrvsConstants.INFORMANT_ADDRESS;
import static org.smartregister.chw.util.CrvsConstants.INFORMANT_NAME;
import static org.smartregister.chw.util.CrvsConstants.INFORMANT_PHONE;
import static org.smartregister.chw.util.CrvsConstants.INFORMANT_RELATIONSHIP;
import static org.smartregister.chw.util.CrvsConstants.MIN_DATE;
import static org.smartregister.chw.util.CrvsConstants.OFFICIAL_ADDRESS;
import static org.smartregister.chw.util.CrvsConstants.OFFICIAL_ID;
import static org.smartregister.chw.util.CrvsConstants.OFFICIAL_NAME;
import static org.smartregister.chw.util.CrvsConstants.OFFICIAL_NUMBER;
import static org.smartregister.chw.util.CrvsConstants.OFFICIAL_POSITION;
import static org.smartregister.chw.util.CrvsConstants.RECEIVED_DEATH_CERTIFICATE;
import static org.smartregister.chw.util.DateUtils.changeDateFormat;
import static org.smartregister.util.JsonFormUtils.fields;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.activity.CoreFamilyRegisterActivity;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.fragment.DeadClientsFragment;
import org.smartregister.chw.listener.ChwBottomNavigationListener;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.CrvsConstants;
import org.smartregister.chw.util.JsonFormUtilsFlv;
import org.smartregister.chw.util.Utils;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class DeadClientsUpdateActivity extends CoreFamilyRegisterActivity {

    public CommonPersonObject commonPersonObject;

    public static void registerBottomNavigation(
            BottomNavigationHelper bottomNavigationHelper, BottomNavigationView bottomNavigationView, Activity activity
    ) {
        Utils.setupBottomNavigation(bottomNavigationHelper, bottomNavigationView, new ChwBottomNavigationListener(activity));
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();
        DeadClientsUpdateActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
        ChwApplication.getInstance().notifyAppContextChange(); // initialize the language (bug in translation)

        action = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.ACTION);
        startDeathCertificateForm();
    }

    public void startDeathCertificateForm() {

        try {
            JSONObject formJsonObject = getFormUtils().getFormJson(DEATH_MEMBER_FORM);
            Map<String, String> valueMap = new HashMap<>();
            valueMap.put(BASE_ENTITY_ID, getIntent().getStringExtra(BASE_ENTITY_ID) == null ? "" : getIntent().getStringExtra(BASE_ENTITY_ID));
            valueMap.put(CLIENT_TYPE, getIntent().getStringExtra(CLIENT_TYPE) == null ? "" : getIntent().getStringExtra(CLIENT_TYPE));
            valueMap.put(DOB, getIntent().getStringExtra(DOB) == null ? "" : getIntent().getStringExtra(DOB));
            valueMap.put(RECEIVED_DEATH_CERTIFICATE, getIntent().getStringExtra(RECEIVED_DEATH_CERTIFICATE) == null ? "" : getIntent().getStringExtra(RECEIVED_DEATH_CERTIFICATE));
            valueMap.put(DEATH_CERTIFICATE_ISSUE_DATE, getIntent().getStringExtra(DEATH_CERTIFICATE_ISSUE_DATE) == null ? "" : getIntent().getStringExtra(DEATH_CERTIFICATE_ISSUE_DATE));
            valueMap.put(DEATH_CERTIFICATE_NUMBER, getIntent().getStringExtra(DEATH_CERTIFICATE_NUMBER) == null ? "" : getIntent().getStringExtra(DEATH_CERTIFICATE_NUMBER));
            valueMap.put(DEATH_NOTIFICATION_DONE, getIntent().getStringExtra(DEATH_NOTIFICATION_DONE) == null ? "" : getIntent().getStringExtra(DEATH_NOTIFICATION_DONE));
            valueMap.put(INFORMANT_NAME, getIntent().getStringExtra(INFORMANT_NAME) == null ? "" : getIntent().getStringExtra(INFORMANT_NAME));
            valueMap.put(INFORMANT_RELATIONSHIP, getIntent().getStringExtra(INFORMANT_RELATIONSHIP) == null ? "" : getIntent().getStringExtra(INFORMANT_RELATIONSHIP));
            valueMap.put(INFORMANT_ADDRESS, getIntent().getStringExtra(INFORMANT_ADDRESS) == null ? "" : getIntent().getStringExtra(INFORMANT_ADDRESS));
            valueMap.put(INFORMANT_PHONE, getIntent().getStringExtra(INFORMANT_PHONE) == null ? "" : getIntent().getStringExtra(INFORMANT_PHONE));
            valueMap.put(OFFICIAL_NAME, getIntent().getStringExtra(OFFICIAL_NAME) == null ? "" : getIntent().getStringExtra(OFFICIAL_NAME));
            valueMap.put(OFFICIAL_ID, getIntent().getStringExtra(OFFICIAL_ID) == null ? "" : getIntent().getStringExtra(OFFICIAL_ID));
            valueMap.put(OFFICIAL_POSITION, getIntent().getStringExtra(OFFICIAL_POSITION) == null ? "" : getIntent().getStringExtra(OFFICIAL_POSITION));
            valueMap.put(OFFICIAL_ADDRESS, getIntent().getStringExtra(OFFICIAL_ADDRESS) == null ? "" : getIntent().getStringExtra(OFFICIAL_ADDRESS));
            valueMap.put(OFFICIAL_NUMBER, getIntent().getStringExtra(OFFICIAL_NUMBER) == null ? "" : getIntent().getStringExtra(OFFICIAL_NUMBER));

            try {
                JSONObject stepOne = formJsonObject.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                JSONObject min_date = CoreJsonFormUtils.getFieldJSONObject(jsonArray, DEATH_CERTIFICATE_ISSUE_DATE);
                if (min_date != null)
                    min_date.put(MIN_DATE, changeDateFormat(Objects.requireNonNull(getIntent().getStringExtra(DOB))));
            } catch (Exception e) {
                Timber.e(e);
            }

            CoreJsonFormUtils.populateJsonForm(formJsonObject, valueMap);
            JsonFormUtilsFlv.startFormActivity(DeadClientsUpdateActivity.this, formJsonObject, DEATH_FORM);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new DeadClientsFragment();
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {
        // code
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                assert jsonString != null;
                JSONObject form = new JSONObject(jsonString);

                if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(CrvsConstants.REMOVE_FAMILY_MEMBER)
                ) {
                    updateDeathCertificate(form);
                }
            } catch (Exception e) {
                Timber.e(e);
            }

        }
    }

    private void updateDeathCertificate(JSONObject jsonObject) {
        try {
            String clientType = getIntent().getStringExtra(CLIENT_TYPE);
            if (clientType != null) {
                switch (clientType) {
                    case "child":
                        updateDeathCertificateDB(CoreConstants.TABLE_NAME.CHILD, jsonObject);
                        break;
                    case "still":
                        updateDeathCertificateDB(CoreConstants.TABLE_NAME.FAMILY_MEMBER, jsonObject);
                        break;
                    case "outOfArea":
                        updateDeathCertificateDB(CrvsConstants.TABLE_OUT_OF_AREA_DEATH, jsonObject);
                        break;
                    case "adult":
                        updateDeathCertificateDB(CrvsConstants.TABLE_FAMILY_MEMBER, jsonObject);
                        break;
                    default:
                        break;
                }
            }

            startActivity(new Intent(this, DeadClientsActivity.class));
            finish();

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void updateDeathCertificateDB(String tableName, JSONObject jsonObject) {
        AllCommonsRepository allCommonsRepository = CoreChwApplication.getInstance().getAllCommonsRepository(tableName);
        if (allCommonsRepository != null) {
            ContentValues values = new ContentValues();

            JSONArray field = fields(jsonObject);
            values.put(RECEIVED_DEATH_CERTIFICATE, getFieldJSONObjectValue(field, RECEIVED_DEATH_CERTIFICATE));
            values.put(DEATH_CERTIFICATE_ISSUE_DATE, getFieldJSONObjectValue(field, DEATH_CERTIFICATE_ISSUE_DATE));
            values.put(DEATH_CERTIFICATE_NUMBER, getFieldJSONObjectValue(field, DEATH_CERTIFICATE_NUMBER));
            values.put(DEATH_NOTIFICATION_DONE, getFieldJSONObjectValue(field, DEATH_NOTIFICATION_DONE));
            values.put(INFORMANT_NAME, getFieldJSONObjectValue(field, INFORMANT_NAME));
            values.put(INFORMANT_RELATIONSHIP, getFieldJSONObjectValue(field, INFORMANT_RELATIONSHIP));
            values.put(INFORMANT_ADDRESS, getFieldJSONObjectValue(field, INFORMANT_ADDRESS));
            values.put(INFORMANT_PHONE, getFieldJSONObjectValue(field, INFORMANT_PHONE));
            values.put(OFFICIAL_NAME, getFieldJSONObjectValue(field, OFFICIAL_NAME));
            values.put(OFFICIAL_ID, getFieldJSONObjectValue(field, OFFICIAL_ID));
            values.put(OFFICIAL_POSITION, getFieldJSONObjectValue(field, OFFICIAL_POSITION));
            values.put(OFFICIAL_ADDRESS, getFieldJSONObjectValue(field, OFFICIAL_ADDRESS));
            values.put(OFFICIAL_NUMBER, getFieldJSONObjectValue(field, OFFICIAL_NUMBER));
            allCommonsRepository.update(tableName, values, getIntent().getStringExtra(DBConstants.KEY.BASE_ENTITY_ID));
        }
    }

    public static String getFieldJSONObjectValue(JSONArray field, String key) {
        JSONObject jsonObject = getFieldJSONObject(field, key);
        if (jsonObject != null && jsonObject.has(CoreJsonFormUtils.VALUE)) {
            try {
                return jsonObject.get(CoreJsonFormUtils.VALUE).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}