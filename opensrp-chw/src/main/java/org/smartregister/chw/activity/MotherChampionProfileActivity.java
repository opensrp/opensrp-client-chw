package org.smartregister.chw.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.activity.CorePmtctProfileActivity;
import org.smartregister.chw.core.interactor.CorePmtctProfileInteractor;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.presenter.CoreFamilyOtherMemberActivityPresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.custom_view.MotherChampionFloatingMenu;
import org.smartregister.chw.dao.MotherChampionDao;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.pmtct.util.Constants;
import org.smartregister.chw.pmtct.util.NCUtils;
import org.smartregister.chw.presenter.PmtctMemberProfilePresenter;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.AlertStatus;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MotherChampionProfileActivity extends CorePmtctProfileActivity {
    private static String baseEntityId;
    private List<ReferralTypeModel> referralTypeModels = new ArrayList<>();

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
        profilePresenter = new PmtctMemberProfilePresenter(this, new CorePmtctProfileInteractor(), memberObject);
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
    protected void onCreation() {
        super.onCreation();
        addPmtctReferralTypes();
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

    @Override
    public void initializeFloatingMenu() {
        basePmtctFloatingMenu = new MotherChampionFloatingMenu(this, memberObject);

        OnClickFloatingMenu onClickFloatingMenu = viewId -> {
            switch (viewId) {
                case R.id.anc_fab:
                    ((MotherChampionFloatingMenu) basePmtctFloatingMenu).animateFAB();
                    break;
                case R.id.call_layout:
                    ((MotherChampionFloatingMenu) basePmtctFloatingMenu).launchCallWidget();
                    ((MotherChampionFloatingMenu) basePmtctFloatingMenu).animateFAB();
                    break;
                case R.id.refer_to_facility_layout:
                    ((PmtctMemberProfilePresenter) profilePresenter).referToFacility();
                    ((MotherChampionFloatingMenu) basePmtctFloatingMenu).animateFAB();
                    break;
                default:
                    Timber.d("Unknown fab action");
                    break;
            }

        };

        ((MotherChampionFloatingMenu) basePmtctFloatingMenu).setFloatMenuClickListener(onClickFloatingMenu);
        basePmtctFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.END);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(basePmtctFloatingMenu, linearLayoutParams);
    }

    private void addPmtctReferralTypes() {
        if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH) {
            referralTypeModels.add(new ReferralTypeModel(getString(R.string.pmtct_referral),
                    CoreConstants.JSON_FORM.getPmtctReferralForm(), CoreConstants.TASKS_FOCUS.PMTCT));

        }

    }

    public List<ReferralTypeModel> getReferralTypeModels() {
        return referralTypeModels;
    }
}
