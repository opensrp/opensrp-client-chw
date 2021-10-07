package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
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
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import timber.log.Timber;
import static org.smartregister.chw.core.utils.FormUtils.getFormUtils;
import static org.smartregister.chw.util.CrvsConstants.BASE_ENTITY_ID;
import static org.smartregister.chw.util.CrvsConstants.CLIENT_TYPE;
import static org.smartregister.chw.util.CrvsConstants.DEATH_CERTIFICATE_ISSUE_DATE;
import static org.smartregister.chw.util.CrvsConstants.DEATH_CERTIFICATE_NUMBER;
import static org.smartregister.chw.util.CrvsConstants.DEATH_FORM;
import static org.smartregister.chw.util.CrvsConstants.DEATH_MEMBER_FORM;
import static org.smartregister.chw.util.CrvsConstants.DEATH_NOTIFICATION_DONE;
import static org.smartregister.chw.util.CrvsConstants.DOB;
import static org.smartregister.chw.util.CrvsConstants.HAS_DEATH_CERTIFICATE;
import static org.smartregister.chw.util.CrvsConstants.MIN_DATE;
import static org.smartregister.chw.util.CrvsConstants.RECEIVED_DEATH_CERTIFICATE;
import static org.smartregister.chw.util.DateUtils.changeDateFormat;

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
        startAncDangerSignsOutcomeForm();
    }

    public void startAncDangerSignsOutcomeForm() {

        try {
            String death_cert_issue_date = getIntent().getStringExtra(DEATH_CERTIFICATE_ISSUE_DATE);
            String death_notification_done = getIntent().getStringExtra(DEATH_NOTIFICATION_DONE);
            String death_cert = getIntent().getStringExtra(RECEIVED_DEATH_CERTIFICATE);
            String death_cert_num = getIntent().getStringExtra(DEATH_CERTIFICATE_NUMBER);
            if (death_cert == null) {
                death_cert = "";
            }

            JSONObject formJsonObject = getFormUtils().getFormJson(DEATH_MEMBER_FORM);
            Map<String, String> valueMap = new HashMap<>();
            valueMap.put(HAS_DEATH_CERTIFICATE, death_cert);
            valueMap.put(DEATH_CERTIFICATE_ISSUE_DATE, death_cert_issue_date);
            valueMap.put(DEATH_NOTIFICATION_DONE, death_notification_done);
            valueMap.put(DEATH_CERTIFICATE_NUMBER, death_cert_num);

            try {
                JSONObject stepOne = formJsonObject.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                JSONObject min_date = CoreJsonFormUtils.getFieldJSONObject(jsonArray, DEATH_CERTIFICATE_ISSUE_DATE);
                assert min_date != null;
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
                    AllCommonsRepository childCommonsRepository = CoreChwApplication.getInstance().getAllCommonsRepository("ec_child");
                    AllCommonsRepository familyCommonsRepository = CoreChwApplication.getInstance().getAllCommonsRepository("ec_family_member");

                    if (childCommonsRepository != null && familyCommonsRepository != null) {
                        updateDeathCertificate(childCommonsRepository, familyCommonsRepository, jsonString);
                    }
                }
            } catch (Exception e) {
                Timber.e(e);
            }

        }
    }

    private void updateDeathCertificate(AllCommonsRepository childCommonsRepository, AllCommonsRepository familyCommonsRepository, String jsonString) {
        try {

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject stepOne = jsonObject.getJSONObject(CrvsConstants.STEP1);
            JSONArray fields = stepOne.getJSONArray(CrvsConstants.FIELDS);

            JSONObject has_death_certificate = fields.getJSONObject(1);
            JSONObject death_certificate_issue_date = fields.getJSONObject(2);
            JSONObject death_certificate_number = fields.getJSONObject(3);
            JSONObject death_notification_done = fields.getJSONObject(4);

            String hasCertificate = has_death_certificate.getString("value");
            String issueDate;
            String deathCertificationNumber;
            String deathNotificationDone;
            if (hasCertificate.equalsIgnoreCase(CrvsConstants.YES)) {
                issueDate = death_certificate_issue_date.getString("value");
                deathCertificationNumber = death_certificate_number.getString("value");
                deathNotificationDone = "";
            }else {
                issueDate = "";
                deathCertificationNumber = "";
                deathNotificationDone = death_notification_done.getString("value");
            }

            if (Objects.requireNonNull(getIntent().getStringExtra(CLIENT_TYPE)).equalsIgnoreCase(CrvsConstants.CHILD)) {
                String tableName = CoreConstants.TABLE_NAME.CHILD;
                if (!issueDate.equals("")) {
                    String sql = "UPDATE " + tableName + " SET received_death_certificate = ?, death_certificate_issue_date = ?, death_certificate_number = ? WHERE id = ?";
                    String[] selectionArgs = {hasCertificate, issueDate, deathCertificationNumber, Objects.requireNonNull(getIntent().getStringExtra(BASE_ENTITY_ID)).toLowerCase()};
                    childCommonsRepository.customQuery(sql, selectionArgs, tableName);
                }else {
                    String sql = "UPDATE " + tableName + " SET received_death_certificate = ?, death_notification_done = ? WHERE id = ?";
                    String[] selectionArgs = {hasCertificate, deathNotificationDone, Objects.requireNonNull(getIntent().getStringExtra(BASE_ENTITY_ID)).toLowerCase()};
                    childCommonsRepository.customQuery(sql, selectionArgs, tableName);
                }
            } else if (Objects.requireNonNull(getIntent().getStringExtra(CLIENT_TYPE)).equalsIgnoreCase(CrvsConstants.STILL)) {
                String tableName = CoreConstants.TABLE_NAME.FAMILY_MEMBER;
                if (!issueDate.equals("")) {
                    String sql = "UPDATE " + tableName + " SET received_death_certificate = ?, death_certificate_issue_date = ?, death_certificate_number = ? WHERE id = ?";
                    String[] selectionArgs = {hasCertificate, issueDate, deathCertificationNumber, Objects.requireNonNull(getIntent().getStringExtra(BASE_ENTITY_ID)).toLowerCase()};
                    familyCommonsRepository.customQuery(sql, selectionArgs, tableName);
                }else {
                    String sql = "UPDATE " + tableName + " SET received_death_certificate = ?, death_notification_done = ? WHERE id = ?";
                    String[] selectionArgs = {hasCertificate, deathNotificationDone, Objects.requireNonNull(getIntent().getStringExtra(BASE_ENTITY_ID)).toLowerCase()};
                    familyCommonsRepository.customQuery(sql, selectionArgs, tableName);
                }
            } else if (Objects.requireNonNull(getIntent().getStringExtra(CLIENT_TYPE)).equalsIgnoreCase(CrvsConstants.OUT_OF_AREA)) {
                String tableName = CrvsConstants.TABLE_OUT_OF_AREA_DEATH;
                if (!issueDate.equals("")) {
                    String sql = "UPDATE " + tableName + " SET received_death_certificate = ?, death_certificate_issue_date = ?, death_certificate_number = ? WHERE id = ?";
                    String[] selectionArgs = {hasCertificate, issueDate, deathCertificationNumber, Objects.requireNonNull(getIntent().getStringExtra(BASE_ENTITY_ID)).toLowerCase()};
                    familyCommonsRepository.customQuery(sql, selectionArgs, tableName);
                }else {
                    String sql = "UPDATE " + tableName + " SET received_death_certificate = ?, death_notification_done = ? WHERE id = ?";
                    String[] selectionArgs = {hasCertificate, deathNotificationDone, Objects.requireNonNull(getIntent().getStringExtra(BASE_ENTITY_ID)).toLowerCase()};
                    familyCommonsRepository.customQuery(sql, selectionArgs, tableName);
                }
            } else if (Objects.requireNonNull(getIntent().getStringExtra(CLIENT_TYPE)).equalsIgnoreCase(CrvsConstants.ADULT)) {
                String tableName = CrvsConstants.TABLE_FAMILY_MEMBER;
                if (!issueDate.equals("")) {
                    String sql = "UPDATE " + tableName + " SET received_death_certificate = ?, death_certificate_issue_date = ?, death_certificate_number = ? WHERE id = ?";
                    String[] selectionArgs = {hasCertificate, issueDate, deathCertificationNumber, Objects.requireNonNull(getIntent().getStringExtra(BASE_ENTITY_ID)).toLowerCase()};
                    familyCommonsRepository.customQuery(sql, selectionArgs, tableName);
                }else {
                    String sql = "UPDATE " + tableName + " SET received_death_certificate = ?, death_notification_done = ? WHERE id = ?";
                    String[] selectionArgs = {hasCertificate, deathNotificationDone, Objects.requireNonNull(getIntent().getStringExtra(BASE_ENTITY_ID)).toLowerCase()};
                    familyCommonsRepository.customQuery(sql, selectionArgs, tableName);
                }
            }
            startActivity(new Intent(this, DeadClientsActivity.class));
            finish();

        } catch (Exception e) {
            Timber.e(e);
        }
    }
}