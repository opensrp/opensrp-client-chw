package org.smartregister.chw.activity;

import static org.smartregister.chw.core.utils.ChildDBConstants.KEY.BIRTH_CERT_ISSUE_DATE;
import static org.smartregister.chw.core.utils.ChildDBConstants.KEY.BIRTH_CERT_NUMBER;
import static org.smartregister.chw.core.utils.FormUtils.getFormUtils;
import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_REG_TYPE;
import static org.smartregister.chw.util.ChildDBConstants.KEY.INFORMANT_REASON;
import static org.smartregister.chw.util.ChildDBConstants.KEY.SYSTEM_BIRTH_NOTIFICATION;
import static org.smartregister.chw.util.CrvsConstants.BIRTH_CERT;
import static org.smartregister.chw.util.CrvsConstants.BIRTH_CERTIFICATE_ISSUE_DATE;
import static org.smartregister.chw.util.CrvsConstants.BIRTH_CERTIFICATION_CHANGED;
import static org.smartregister.chw.util.CrvsConstants.BIRTH_CLIENT_TYPE;
import static org.smartregister.chw.util.CrvsConstants.BIRTH_FORM;
import static org.smartregister.chw.util.CrvsConstants.BIRTH_NOTIFICATION;
import static org.smartregister.chw.util.CrvsConstants.BIRTH_REGISTRATION;
import static org.smartregister.chw.util.CrvsConstants.CLIENT_TYPE;
import static org.smartregister.chw.util.CrvsConstants.DOB;
import static org.smartregister.chw.util.CrvsConstants.MIN_DATE;
import static org.smartregister.chw.util.DateUtils.changeDateFormat;
import static org.smartregister.util.JsonFormUtils.fields;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;

import android.app.Activity;
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
import org.smartregister.chw.fragment.FamilyRegisterFragment;
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

public class BirthNotificationUpdateActivity extends CoreFamilyRegisterActivity {

    public CommonPersonObject commonPersonObject;

    public static void registerBottomNavigation(
            BottomNavigationHelper bottomNavigationHelper, BottomNavigationView bottomNavigationView, Activity activity
    ) {
        Utils.setupBottomNavigation(bottomNavigationHelper, bottomNavigationView, new ChwBottomNavigationListener(activity));
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();
        BirthNotificationUpdateActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
        ChwApplication.getInstance().notifyAppContextChange(); // initialize the language (bug in translation)

        action = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.ACTION);
        startBirthCertificationUpdateForm();
    }

    public void startBirthCertificationUpdateForm() {

        try {
            String birthCert = getIntent().getStringExtra(BIRTH_CERT);
            String birthRegistration = getIntent().getStringExtra(BIRTH_REGISTRATION);
            String birthNotification = getIntent().getStringExtra(BIRTH_NOTIFICATION);
            String birthCertIssueDate = getIntent().getStringExtra(BIRTH_CERT_ISSUE_DATE);
            String birthCertNum = getIntent().getStringExtra(BIRTH_CERT_NUMBER);
            String systemBirthNotification = getIntent().getStringExtra(SYSTEM_BIRTH_NOTIFICATION);
            String birthRegType = getIntent().getStringExtra(BIRTH_REG_TYPE);
            String informantReason = getIntent().getStringExtra(INFORMANT_REASON);
            if (birthCert == null) {
                birthCert = "";
            }

            JSONObject formJsonObject = getFormUtils().getFormJson(BIRTH_CERTIFICATION_CHANGED);
            Map<String, String> valueMap = new HashMap<>();

            valueMap.put(BIRTH_CERT, birthCert);
            valueMap.put(BIRTH_REGISTRATION, birthRegistration);
            valueMap.put(BIRTH_NOTIFICATION, birthNotification);
            valueMap.put(BIRTH_CERT_ISSUE_DATE, birthCertIssueDate);
            valueMap.put(BIRTH_CERT_NUMBER, birthCertNum);
            valueMap.put(SYSTEM_BIRTH_NOTIFICATION, systemBirthNotification);
            valueMap.put(BIRTH_REG_TYPE, birthRegType);
            valueMap.put(INFORMANT_REASON, informantReason);
            try {
                JSONObject stepOne = formJsonObject.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                JSONObject min_date = getFieldJSONObject(jsonArray, BIRTH_CERTIFICATE_ISSUE_DATE);
                assert min_date != null;
                min_date.put(MIN_DATE, changeDateFormat(Objects.requireNonNull(getIntent().getStringExtra(DOB))));
            } catch (Exception e) {
                Timber.e(e);
            }
            CoreJsonFormUtils.populateJsonForm(formJsonObject, valueMap);
            JsonFormUtilsFlv.startFormActivity(BirthNotificationUpdateActivity.this, formJsonObject, BIRTH_FORM);

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new FamilyRegisterFragment();
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

                if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equalsIgnoreCase(CoreConstants.EventType.BIRTH_CERTIFICATION)) {

                    AllCommonsRepository allCommonsRepository = CoreChwApplication.getInstance().getAllCommonsRepository("ec_child");
                    //Update ec_child table
                    if (allCommonsRepository != null) {
                        updateBirthCertificate(allCommonsRepository, form);
                    }
                }
            } catch (Exception e) {
                Timber.e(e);
            }

        }
    }

    private void updateBirthCertificate(AllCommonsRepository allCommonsRepository, JSONObject jsonObject) {
        String client_type = getIntent().getStringExtra(CLIENT_TYPE);
        try {
            JSONArray field = fields(jsonObject);
            String birthCert = getFieldJSONObjectValue(field, BIRTH_CERT);
            String birthRegistration = getFieldJSONObjectValue(field, BIRTH_REGISTRATION);
            String birthNotification = getFieldJSONObjectValue(field, BIRTH_NOTIFICATION);
            String birthCertIssueDate = getFieldJSONObjectValue(field, BIRTH_CERT_ISSUE_DATE);
            String birthCertNum = getFieldJSONObjectValue(field, BIRTH_CERT_NUMBER);
            String systemBirthNotification = getFieldJSONObjectValue(field, SYSTEM_BIRTH_NOTIFICATION);
            String birthRegType = getFieldJSONObjectValue(field, BIRTH_REG_TYPE);
            String informantReason = getFieldJSONObjectValue(field, INFORMANT_REASON);


            String tableName;
            if (client_type == null) return;
            if (client_type.equalsIgnoreCase(BIRTH_CLIENT_TYPE)) {
                tableName = Constants.TABLE_NAME.CHILD;
            } else {
                tableName = CrvsConstants.TABLE_OUT_OF_AREA_CHILD;
            }

            String sql = "UPDATE " + tableName + " SET birth_cert = ?, birth_registration = ?, birth_notification = ?, birth_cert_issue_date = ?,  " +
                    "birth_cert_num = ?, system_birth_notification = ?, birth_reg_type = ?, informant_reason = ? WHERE id = ?";
            String[] selectionArgs = {birthCert, birthRegistration, birthNotification, birthCertIssueDate,
                    birthCertNum, systemBirthNotification, birthRegType, informantReason,
                    getIntent().getStringExtra(DBConstants.KEY.BASE_ENTITY_ID)};
            allCommonsRepository.customQuery(sql, selectionArgs, tableName);

            startActivity(new Intent(this, BirthNotificationRegisterActivity.class));
            finish();

        } catch (Exception e) {
            Timber.e(e);
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