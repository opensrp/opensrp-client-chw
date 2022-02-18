package org.smartregister.chw.activity;

import static org.smartregister.AllConstants.CLIENT_TYPE;
import static org.smartregister.chw.core.utils.CoreConstants.DOB;
import static org.smartregister.chw.core.utils.CoreConstants.EventType.DEATH_CERTIFICATION;
import static org.smartregister.chw.core.utils.CoreConstants.EventType.UPDATE_DEATH_CERTIFICATION;
import static org.smartregister.chw.core.utils.CoreConstants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.DEATH_CERTIFICATE_ISSUE_DATE;
import static org.smartregister.chw.core.utils.CoreConstants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.DEATH_CERTIFICATE_NUMBER;
import static org.smartregister.chw.core.utils.CoreConstants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.DEATH_NOTIFICATION_DONE;
import static org.smartregister.chw.core.utils.CoreConstants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.RECEIVED_DEATH_CERTIFICATE;
import static org.smartregister.chw.util.Constants.INFORMANT_ADDRESS;
import static org.smartregister.chw.util.Constants.INFORMANT_NAME;
import static org.smartregister.chw.util.Constants.INFORMANT_PHONE;
import static org.smartregister.chw.util.Constants.INFORMANT_RELATIONSHIP;
import static org.smartregister.chw.util.Constants.OFFICIAL_ADDRESS;
import static org.smartregister.chw.util.Constants.OFFICIAL_NAME;
import static org.smartregister.chw.util.Constants.OFFICIAL_NUMBER;
import static org.smartregister.chw.util.Constants.OFFICIAL_POSITION;
import static org.smartregister.chw.util.CrvsConstants.BASE_ENTITY_ID;
import static org.smartregister.chw.util.CrvsConstants.HAS_DEATH_CERTIFICATE;
import static org.smartregister.chw.util.CrvsConstants.OFFICIAL_ID;

import android.content.Intent;
import android.view.View;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.activity.CoreCertificationRegisterActivity;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fragment.DeathCertificationRegisterFragment;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class DeathCertificationRegisterActivity extends CoreCertificationRegisterActivity {

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
        return new DeathCertificationRegisterFragment();
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                JSONObject form = new JSONObject(jsonString);
                String encounter_type = form.optString(Constants.JSON_FORM_EXTRA.ENCOUNTER_TYPE);

                if (DEATH_CERTIFICATION.equalsIgnoreCase(encounter_type)
                        || UPDATE_DEATH_CERTIFICATION.equalsIgnoreCase(encounter_type)
                ) {
                    presenter().saveForm(jsonString, getIntent().getStringExtra(CLIENT_TYPE));
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList("death_certification_register");
    }

    @Override
    public void startUpdateFormActivity() {
        try {
            String death_cert = getIntent().getStringExtra(RECEIVED_DEATH_CERTIFICATE);
            String baseEntityId = getIntent().getStringExtra(BASE_ENTITY_ID);

            if (death_cert == null) {
                death_cert = "";
            }

            if (StringUtils.isNotBlank(death_cert)) {
                startEditCertificationForm(baseEntityId);
            } else {
                presenter().startCertificationForm(CoreConstants.JSON_FORM.getDeathRegistrationForm(), baseEntityId);
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void startEditCertificationForm(String baseEntityId) throws Exception {

        Map<String, String> valueMap = new HashMap<>();
        valueMap.put(DOB, getIntent().getStringExtra(DOB));
        valueMap.put(HAS_DEATH_CERTIFICATE, getIntent().getStringExtra(RECEIVED_DEATH_CERTIFICATE));
        valueMap.put(DEATH_CERTIFICATE_ISSUE_DATE, getIntent().getStringExtra(DEATH_CERTIFICATE_ISSUE_DATE));
        valueMap.put(DEATH_NOTIFICATION_DONE, getIntent().getStringExtra(DEATH_NOTIFICATION_DONE));
        valueMap.put(DEATH_CERTIFICATE_NUMBER, getIntent().getStringExtra(DEATH_CERTIFICATE_NUMBER));
        valueMap.put(INFORMANT_NAME, getIntent().getStringExtra(INFORMANT_NAME));
        valueMap.put(INFORMANT_RELATIONSHIP, getIntent().getStringExtra(INFORMANT_RELATIONSHIP));
        valueMap.put(INFORMANT_ADDRESS, getIntent().getStringExtra(INFORMANT_ADDRESS));
        valueMap.put(INFORMANT_PHONE, getIntent().getStringExtra(INFORMANT_PHONE));
        valueMap.put(OFFICIAL_NAME, getIntent().getStringExtra(OFFICIAL_NAME));
        valueMap.put(OFFICIAL_ID, getIntent().getStringExtra(OFFICIAL_ID));
        valueMap.put(OFFICIAL_POSITION, getIntent().getStringExtra(OFFICIAL_POSITION));
        valueMap.put(OFFICIAL_ADDRESS, getIntent().getStringExtra(OFFICIAL_ADDRESS));
        valueMap.put(OFFICIAL_NUMBER, getIntent().getStringExtra(OFFICIAL_NUMBER));

        presenter().startEditCertForm(CoreConstants.JSON_FORM.getDeathRegistrationForm(), UPDATE_DEATH_CERTIFICATION, baseEntityId, valueMap);

    }

    @Override
    public String getFormTitle() {
        return getString(R.string.death_certification);
    }

    @Override
    public Class<? extends CoreCertificationRegisterActivity> getActivityClass() {
        return DeathCertificationRegisterActivity.class;
    }
}
