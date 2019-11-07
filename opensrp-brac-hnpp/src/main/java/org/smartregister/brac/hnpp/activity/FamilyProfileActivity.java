package org.smartregister.brac.hnpp.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.model.HnppFamilyProfileModel;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.chw.anc.activity.BaseAncMemberProfileActivity;
import org.smartregister.chw.core.activity.CoreAboveFiveChildProfileActivity;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.activity.CoreFamilyProfileMenuActivity;
import org.smartregister.chw.core.activity.CoreFamilyRemoveMemberActivity;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.brac.hnpp.fragment.FamilyProfileMemberFragment;
import org.smartregister.brac.hnpp.presenter.FamilyProfilePresenter;
import org.smartregister.chw.pnc.activity.BasePncMemberProfileActivity;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.HashMap;

import timber.log.Timber;

public class FamilyProfileActivity extends CoreFamilyProfileActivity {

    public String moduleId;
    public String houseHoldId;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        setupMenuOptions(menu);
        return true;
    }

    @Override
    protected void refreshPresenter() {
        presenter = new FamilyProfilePresenter(this, new HnppFamilyProfileModel(familyName,moduleId,houseHoldId,familyBaseEntityId),houseHoldId, familyBaseEntityId, familyHead, primaryCaregiver, familyName);
    }

    @Override
    protected void refreshList(Fragment fragment) {
        if (fragment instanceof FamilyProfileMemberFragment) {
            FamilyProfileMemberFragment familyProfileMemberFragment = ((FamilyProfileMemberFragment) fragment);
            if (familyProfileMemberFragment.presenter() != null) {
                familyProfileMemberFragment.refreshListView();
            }
        }
    }
    @Override
    public void startFormForEdit() {
        super.startFormForEdit();
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        try{
            if(jsonForm.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyRegister.updateEventType)){
                Intent intent = new Intent(this, Utils.metadata().familyFormActivity);
                intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
                Form form = new Form();
                form.setActionBarBackground(org.smartregister.family.R.color.family_actionbar);
                form.setWizard(false);
                intent.putExtra("form", form);
                this.startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
            }else{
                super.startFormActivity(jsonForm);
            }
        }catch (Exception e){

        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                Timber.d(jsonString);

                JSONObject form = new JSONObject(jsonString);
                if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyRegister.updateEventType)) {
                    presenter().updateFamilyRegister(jsonString);
                }else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
            } catch (Exception e) {
                Timber.e(e);
            }

        }
        //super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected Class<? extends CoreFamilyRemoveMemberActivity> getFamilyRemoveMemberClass() {
        return FamilyRemoveMemberActivity.class;
    }

    @Override
    protected Class<? extends CoreFamilyProfileMenuActivity> getFamilyProfileMenuClass() {
        return FamilyProfileMenuActivity.class;
    }

    @Override
    protected void initializePresenter() {
        super.initializePresenter();
        moduleId = getIntent().getStringExtra(HnppConstants.KEY.MODULE_ID);
        houseHoldId = getIntent().getStringExtra(DBConstants.KEY.UNIQUE_ID);
        presenter = new FamilyProfilePresenter(this, new HnppFamilyProfileModel(familyName,moduleId,houseHoldId,familyBaseEntityId),houseHoldId, familyBaseEntityId, familyHead, primaryCaregiver, familyName);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(FamilyProfileMemberFragment.newInstance(this.getIntent().getExtras()),
                this.getString(R.string.member));

        viewPager.setAdapter(adapter);

        if (getIntent().getBooleanExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, false) ||
                getIntent().getBooleanExtra(Constants.INTENT_KEY.GO_TO_DUE_PAGE, false)) {
            viewPager.setCurrentItem(1);
        }

        return viewPager;
    }

    @Override
    protected Class<?> getFamilyOtherMemberProfileActivityClass() {
        return null;
    }

    @Override
    protected Class<? extends CoreAboveFiveChildProfileActivity> getAboveFiveChildProfileActivityClass() {
        return null;
    }

    @Override
    protected Class<? extends CoreChildProfileActivity> getChildProfileActivityClass() {
        return null;
    }

    @Override
    protected Class<? extends BaseAncMemberProfileActivity> getAncMemberProfileActivityClass() {
        return null;
    }

    @Override
    protected Class<? extends BasePncMemberProfileActivity> getPncMemberProfileActivityClass() {
        return null;
    }

    @Override
    protected boolean isAncMember(String s) {
        return false;
    }

    @Override
    protected HashMap<String, String> getAncFamilyHeadNameAndPhone(String s) {
        return null;
    }

    @Override
    protected CommonPersonObject getAncCommonPersonObject(String s) {
        return null;
    }

    @Override
    protected CommonPersonObject getPncCommonPersonObject(String s) {
        return null;
    }

    @Override
    protected boolean isPncMember(String s) {
        return false;
    }

    private void setupMenuOptions(Menu menu) {

        MenuItem removeMember = menu.findItem(org.smartregister.chw.core.R.id.action_remove_member);
        MenuItem changeFamHead = menu.findItem(org.smartregister.chw.core.R.id.action_change_head);
        MenuItem changeCareGiver = menu.findItem(org.smartregister.chw.core.R.id.action_change_care_giver);

        if (removeMember != null) {
            removeMember.setVisible(false);
        }

        if (changeFamHead != null) {
            changeFamHead.setVisible(false);
        }

        if (changeCareGiver != null) {
            changeCareGiver.setVisible(false);
        }
    }
}
