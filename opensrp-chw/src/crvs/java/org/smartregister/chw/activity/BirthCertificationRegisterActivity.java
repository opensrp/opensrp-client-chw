package org.smartregister.chw.activity;

import static org.smartregister.chw.core.utils.FormUtils.getFormUtils;
import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_REG_TYPE;
import static org.smartregister.chw.util.ChildDBConstants.KEY.INFORMANT_REASON;
import static org.smartregister.chw.util.ChildDBConstants.KEY.SYSTEM_BIRTH_NOTIFICATION;
import static org.smartregister.chw.util.CrvsConstants.BIRTH_CERT;
import static org.smartregister.chw.util.CrvsConstants.BIRTH_CERTIFICATE_ISSUE_DATE;
import static org.smartregister.chw.util.CrvsConstants.BIRTH_CERTIFICATION_CHANGED;
import static org.smartregister.chw.util.CrvsConstants.BIRTH_CERT_NUM;
import static org.smartregister.chw.util.CrvsConstants.BIRTH_NOTIFICATION;
import static org.smartregister.chw.util.CrvsConstants.BIRTH_REGISTRATION;
import static org.smartregister.chw.util.CrvsConstants.DOB;
import static org.smartregister.chw.util.CrvsConstants.MIN_DATE;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;

import android.content.Intent;
import android.view.View;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.activity.CoreCertificationRegisterActivity;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.fragment.BirthCertificationRegisterFragment;
import org.smartregister.chw.util.DateUtils;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class BirthCertificationRegisterActivity extends CoreCertificationRegisterActivity {

    @Override
    protected void registerBottomNavigation() {
        // Hide bottom nav for birth certification register
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
        FamilyRegisterActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
        bottomNavigationView.setVisibility(View.GONE);
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new BirthCertificationRegisterFragment();
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        NavigationMenu menu = NavigationMenu.getInstance(this, null, null);
        if (menu != null) {
            menu.getNavigationAdapter().setSelectedView(CoreConstants.DrawerMenu.BIRTH_NOTIFICATION);
        }
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        super.onActivityResultExtended(requestCode, resultCode, data);
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                JSONObject form = new JSONObject(jsonString);
                String baseEntityId = form.optString(Constants.JSON_FORM_EXTRA.ENTITY_TYPE);
                String encounter_type = form.optString(Constants.JSON_FORM_EXTRA.ENCOUNTER_TYPE);

                if (encounter_type.equalsIgnoreCase("")) {
                    // Todo -> Generate "update" event
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public void startUpdateFormActivity() {
        try {
            JSONObject formJsonObject = getFormUtils().getFormJson(BIRTH_CERTIFICATION_CHANGED);
            String birthCert = getIntent().getStringExtra(BIRTH_CERT);

            if (birthCert == null) {
                birthCert = "";
            }

            if (StringUtils.isNotBlank(birthCert))
                prepopulateCertificationForm(formJsonObject);

            startFormActivity(formJsonObject);

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void prepopulateCertificationForm(JSONObject formJsonObject) throws JSONException {
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put(BIRTH_CERT, getIntent().getStringExtra(BIRTH_CERT));
        valueMap.put(BIRTH_REGISTRATION, getIntent().getStringExtra(BIRTH_REGISTRATION));
        valueMap.put(BIRTH_NOTIFICATION, getIntent().getStringExtra(ChildDBConstants.KEY.BIRTH_CERT_NOTIFIICATION));
        valueMap.put(BIRTH_CERTIFICATE_ISSUE_DATE, getIntent().getStringExtra(BIRTH_CERTIFICATE_ISSUE_DATE));
        valueMap.put(BIRTH_CERT_NUM, getIntent().getStringExtra(ChildDBConstants.KEY.BIRTH_CERT_NUMBER));
        valueMap.put(SYSTEM_BIRTH_NOTIFICATION, getIntent().getStringExtra(SYSTEM_BIRTH_NOTIFICATION));
        valueMap.put(BIRTH_REG_TYPE, getIntent().getStringExtra(BIRTH_REG_TYPE));
        valueMap.put(INFORMANT_REASON, getIntent().getStringExtra(INFORMANT_REASON));
        try {
            JSONObject stepOne = formJsonObject.getJSONObject(JsonFormUtils.STEP1);
            JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
            JSONObject min_date = getFieldJSONObject(jsonArray, BIRTH_CERTIFICATE_ISSUE_DATE);
            assert min_date != null;
            min_date.put(MIN_DATE, DateUtils.changeDateFormat(Objects.requireNonNull(getIntent().getStringExtra(DOB))));
        } catch (Exception e) {
            Timber.e(e);
        }
        CoreJsonFormUtils.populateJsonForm(formJsonObject, valueMap);
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList("birth_certification_register");
    }

    @Override
    public String getFormTitle() {
        return getString(R.string.birth_certification);
    }
}
