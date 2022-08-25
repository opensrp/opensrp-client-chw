package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import org.apache.commons.lang3.StringUtils;
import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.activity.CoreHivstProfileActivity;
import org.smartregister.chw.core.interactor.CoreHivstProfileInteractor;
import org.smartregister.chw.core.presenter.CoreFamilyOtherMemberActivityPresenter;
import org.smartregister.chw.core.presenter.CoreHivstMemberProfilePresenter;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.chw.hiv.dao.HivDao;
import org.smartregister.chw.hivst.custom_views.BaseHivstFloatingMenu;
import org.smartregister.chw.hivst.dao.HivstDao;
import org.smartregister.chw.hivst.listener.OnClickFloatingMenu;
import org.smartregister.chw.hivst.util.Constants;
import org.smartregister.chw.util.HivstUtils;
import org.smartregister.domain.AlertStatus;

import timber.log.Timber;


public class HivstProfileActivity extends CoreHivstProfileActivity {

    public static void startProfile(Activity activity, String baseEntityId) {
        Intent intent = new Intent(activity, HivstProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        activity.startActivity(intent);
    }

    @Override
    protected void initializePresenter() {
        showProgressBar(true);
        String baseEntityId = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID);
        memberObject = HivstDao.getMember(baseEntityId);
        profilePresenter = new CoreHivstMemberProfilePresenter(this, new CoreHivstProfileInteractor(), memberObject);
        fetchProfileData();
        profilePresenter.refreshProfileBottom();
    }

    @Override
    protected Class<? extends CoreFamilyProfileActivity> getFamilyProfileActivityClass() {
        return null;
    }

    @Override
    public void startIssueSelfTestingKitsForm(String baseEntityId) {
        JSONObject form = FormUtils.getFormUtils().getFormJson(Constants.FORMS.HIVST_ISSUE_KITS);
        try {
            form.put(org.smartregister.util.JsonFormUtils.ENTITY_ID, baseEntityId);
            JSONObject global = form.getJSONObject("global");
            boolean knownPositiveFromHIV = HivDao.isRegisteredForHiv(baseEntityId) && StringUtils.isNotBlank(HivDao.getMember(baseEntityId).getCtcNumber());
            global.put("known_positive", HivstDao.isTheClientKnownPositiveAtReg(baseEntityId) || knownPositiveFromHIV);
        } catch (JSONException e) {
            Timber.e(e);
        }
        startFormActivity(form);
    }

    @Override
    public void startResultViewActivity(Context context, String baseEntityId) {
        HivstResultViewActivity.startResultViewActivity(context, baseEntityId);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rlSelfTestingResults) {
            startResultViewActivity(this, memberObject.getBaseEntityId());
        } else {
            super.onClick(view);
        }
    }

    @Override
    public void initializeFloatingMenu() {
        baseHivstFloatingMenu = new BaseHivstFloatingMenu(this, memberObject);
        checkPhoneNumberProvided(StringUtils.isNotBlank(memberObject.getPhoneNumber()));
        OnClickFloatingMenu onClickFloatingMenu = viewId -> {
            switch (viewId) {
                case R.id.hivst_fab:
                    //Animates the actual FAB
                    ((BaseHivstFloatingMenu) baseHivstFloatingMenu).animateFAB();
                    break;
                case R.id.call_layout:
                    ((BaseHivstFloatingMenu) baseHivstFloatingMenu).launchCallWidget();
                    ((BaseHivstFloatingMenu) baseHivstFloatingMenu).animateFAB();
                    break;
                case R.id.refer_to_facility_layout:
                    HivstUtils.startHIVSTReferral(this, memberObject.getBaseEntityId());
                    break;
                default:
                    Timber.d("Unknown FAB action");
                    break;
            }
        };

        ((BaseHivstFloatingMenu) baseHivstFloatingMenu).setFloatMenuClickListener(onClickFloatingMenu);
        baseHivstFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.END);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(baseHivstFloatingMenu, linearLayoutParams);
    }

    private void checkPhoneNumberProvided(boolean hasPhoneNumber) {
        baseHivstFloatingMenu.redrawWithOption(baseHivstFloatingMenu, hasPhoneNumber);
    }

    @Override
    protected void removeMember() {
        //implement
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
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
    public void refreshMedicalHistory(boolean hasHistory) {
        rlLastVisit.setVisibility(View.GONE);
    }

    @Override
    public void refreshFamilyStatus(AlertStatus status) {
        super.refreshFamilyStatus(status);
        rlFamilyServicesDue.setVisibility(View.GONE);
    }
}
