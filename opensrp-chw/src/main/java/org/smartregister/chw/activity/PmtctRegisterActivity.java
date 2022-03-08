package org.smartregister.chw.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.core.activity.CorePmtctRegisterActivity;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.chw.fragment.PmtctRegisterFragment;
import org.smartregister.chw.pmtct.util.NCUtils;
import org.smartregister.chw.util.Constants;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.view.fragment.BaseRegisterFragment;

import timber.log.Timber;

public class PmtctRegisterActivity extends CorePmtctRegisterActivity {
    private static String referralFormSubmissionId;
    private static String baseEntityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new PmtctRegisterFragment();
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
        FamilyRegisterActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
    }

    public static void startPmtctFollowupFeedbackActivity(Activity activity, String baseEntityID, String jsonString, String formSubmissionId) {
        Intent intent = new Intent(activity, PmtctRegisterActivity.class);
        baseEntityId = baseEntityID;
        referralFormSubmissionId = formSubmissionId;
        intent.putExtra(org.smartregister.chw.pmtct.util.Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        intent.putExtra(org.smartregister.chw.pmtct.util.Constants.ACTIVITY_PAYLOAD.PMTCT_FORM_NAME, Constants.JsonForm.getPmtctCommunityFollowupFeedback());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.JSON, jsonString);
        intent.putExtra(org.smartregister.chw.pmtct.util.Constants.ACTIVITY_PAYLOAD.ACTION, Constants.ActionList.PMTCT_FOLLOWUP_FEEDBACK);
        activity.startActivity(intent);
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        if (ACTION.equalsIgnoreCase(Constants.ActionList.PMTCT_FOLLOWUP_FEEDBACK)) {
            String jsonString = getIntent().getStringExtra(org.smartregister.family.util.Constants.INTENT_KEY.JSON);
            try {
                JSONObject form = new JSONObject(jsonString);
                startActivityForResult(FormUtils.getStartFormActivity(form, getString(R.string.pmtct_followup_feedback_title), this), JsonFormUtils.REQUEST_CODE_GET_JSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == org.smartregister.chw.pmtct.util.Constants.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(JsonFormUtils.ENCOUNTER_TYPE);
                if (encounterType.equals(Constants.EncounterType.PMTCT_COMMUNITY_FOLLOWUP_FEEDBACK)) {
                    AllSharedPreferences allSharedPreferences = org.smartregister.util.Utils.getAllSharedPreferences();
                    Event baseEvent = org.smartregister.chw.pmtct.util.JsonFormUtils.processJsonForm(allSharedPreferences, tagReferralFormId(jsonString, referralFormSubmissionId), Constants.TableName.PMTCT_COMMUNITY_FEEDBACK);
                    org.smartregister.chw.pmtct.util.JsonFormUtils.tagEvent(allSharedPreferences, baseEvent);
                    baseEvent.setBaseEntityId(baseEntityId);
                    NCUtils.processEvent(baseEvent.getBaseEntityId(), new JSONObject(org.smartregister.chw.pmtct.util.JsonFormUtils.gson.toJson(baseEvent)));
                }
            } catch (Exception e) {
                Timber.e(e, "PmtctRegisterActivity -- > onActivityResult");
            }
        }
    }

    private String tagReferralFormId(String jsonString, String formSubmissionId) throws JSONException {
        JSONObject form = new JSONObject(jsonString);
        JSONArray fields = org.smartregister.util.JsonFormUtils.fields(form);
        JSONObject referralFormId = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, Constants.PmtctFollowupFeedbackConstants.referralFormId);
        assert referralFormId != null;
        referralFormId.put(JsonFormUtils.VALUE, formSubmissionId);
        return form.toString();
    }

}
