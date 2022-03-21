package org.smartregister.chw.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.activity.CorePmtctProfileActivity;
import org.smartregister.chw.core.interactor.CorePmtctProfileInteractor;
import org.smartregister.chw.core.presenter.CoreFamilyOtherMemberActivityPresenter;
import org.smartregister.chw.core.presenter.CorePmtctMemberProfilePresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.MotherChampionDao;
import org.smartregister.chw.pmtct.util.Constants;
import org.smartregister.chw.pmtct.util.NCUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.AlertStatus;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class MotherChampionProfileActivity extends CorePmtctProfileActivity {
    private static String baseEntityId;

    public static void startProfile(Activity activity, String baseEntityId) {
        MotherChampionProfileActivity.baseEntityId = baseEntityId;
        Intent intent = new Intent(activity, MotherChampionProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        activity.startActivity(intent);
    }

    @Override
    protected void initializePresenter() {
        showProgressBar(true);
        String baseEntityId = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID);
        memberObject = MotherChampionDao.getMember(baseEntityId);
        profilePresenter = new CorePmtctMemberProfilePresenter(this, new CorePmtctProfileInteractor(), memberObject);
        fetchProfileData();
        profilePresenter.refreshProfileBottom();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.textview_record_pmtct) {
            JSONObject formJsonObject = null;
            try {
                formJsonObject = (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, org.smartregister.chw.util.Constants.JsonForm.getMotherChampionFollowupForm());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startFormActivity(formJsonObject);
        }
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        try {

            Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);
            intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

            Form form = new Form();
            form.setActionBarBackground(org.smartregister.chw.core.R.color.family_actionbar);
            form.setWizard(false);
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

            if (jsonForm.getString("encounter_type").equals(org.smartregister.chw.util.Constants.EncounterType.MOTHER_CHAMPION_FOLLOWUP)) {
                form.setWizard(true);
                form.setNavigationBackground(org.smartregister.chw.core.R.color.family_navigation);
                form.setName(getString(R.string.record_followup_visit_mother_champion));
                form.setNextLabel(this.getResources().getString(org.smartregister.chw.core.R.string.next));
                form.setPreviousLabel(this.getResources().getString(org.smartregister.chw.core.R.string.back));
            }

            startActivityForResult(intent, org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.return_to_mother_champion_clients);

        textViewRecordPmtct.setText(R.string.record_followup);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_remove_member).setVisible(false);
        menu.findItem(R.id.action_issue_pmtct_followup_referral).setVisible(false);
        return true;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(JsonFormUtils.ENCOUNTER_TYPE);
                if (encounterType.equals(org.smartregister.chw.util.Constants.EncounterType.MOTHER_CHAMPION_FOLLOWUP)) {
                    AllSharedPreferences allSharedPreferences = org.smartregister.util.Utils.getAllSharedPreferences();
                    Event baseEvent = org.smartregister.chw.pmtct.util.JsonFormUtils.processJsonForm(allSharedPreferences, jsonString, org.smartregister.chw.util.Constants.TableName.MOTHER_CHAMPION_FOLLOWUP);
                    org.smartregister.chw.pmtct.util.JsonFormUtils.tagEvent(allSharedPreferences, baseEvent);
                    baseEvent.setBaseEntityId(baseEntityId);
                    try {
                        NCUtils.processEvent(baseEvent.getBaseEntityId(), new JSONObject(org.smartregister.chw.pmtct.util.JsonFormUtils.gson.toJson(baseEvent)));
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
            } catch (Exception e) {
                Timber.e(e, "MotherChampionProfileActivity -- > onActivityResult");
            }
        }
    }

    @Override
    public void refreshFamilyStatus(AlertStatus status) {
        super.refreshFamilyStatus(status);
        rlFamilyServicesDue.setVisibility(View.GONE);
    }

    @Override
    protected Class<? extends CoreFamilyProfileActivity> getFamilyProfileActivityClass() {
        return null;
    }

    @Override
    protected void removeMember() {
        //implement
    }

    @NonNull
    @Override
    public CoreFamilyOtherMemberActivityPresenter presenter() {
        return null;
    }

    @Override
    public void setProfileImage(String s, String s1) {
        //implement
    }

    @Override
    public void setProfileDetailThree(String s) {
        //implement
    }

    @Override
    public void toggleFamilyHead(boolean b) {
        //implement
    }

    @Override
    public void togglePrimaryCaregiver(boolean b) {
        //implement
    }

    @Override
    public void refreshList() {
        //implement
    }

    @Override
    public void updateHasPhone(boolean b) {
        //implement
    }

    @Override
    public void setFamilyServiceStatus(String s) {
        //implement
    }

    @Override
    public void verifyHasPhone() {
        //implement
    }

    @Override
    public void notifyHasPhone(boolean b) {
        //implement
    }
}
