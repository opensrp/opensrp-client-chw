package com.opensrp.chw.hf.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.Toast;

import com.opensrp.chw.core.activity.CoreFamilyProfileActivity;
import com.opensrp.chw.core.utils.CoreConstants;
import com.opensrp.chw.hf.fragement.FamilyProfileMemberFragment;
import com.opensrp.chw.hf.model.FamilyProfileModel;
import com.opensrp.chw.hf.presenter.FamilyProfilePresenter;
import com.opensrp.hf.R;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import timber.log.Timber;

public class FamilyProfileActivity extends CoreFamilyProfileActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_family_details:

                startFormForEdit();

                break;
           /* case R.id.action_remove_member:

                Intent frm_intent = new Intent(this, FamilyRemoveMemberActivity.class);
                frm_intent.putExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, getFamilyBaseEntityId());
                frm_intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, familyHead);
                frm_intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, primaryCaregiver);
                startActivityForResult(frm_intent, ProfileActivityResults.CHANGE_COMPLETED);

                break;
            case R.id.action_change_head:

                Intent fh_intent = new Intent(this, CoreFamilyProfileMenuActivity.class);
                fh_intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, getFamilyBaseEntityId());
                fh_intent.putExtra(CoreFamilyProfileMenuActivity.MENU, MenuType.ChangeHead);
                startActivityForResult(fh_intent, ProfileActivityResults.CHANGE_COMPLETED);

                break;
            case R.id.action_change_care_giver:


                Intent pc_intent = new Intent(this, CoreFamilyProfileMenuActivity.class);
                pc_intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, getFamilyBaseEntityId());
                pc_intent.putExtra(CoreFamilyProfileMenuActivity.MENU, MenuType.ChangePrimaryCare);
                startActivityForResult(pc_intent, ProfileActivityResults.CHANGE_COMPLETED);

                break;*/
            default:
                super.onOptionsItemSelected(item);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case JsonFormUtils.REQUEST_CODE_GET_JSON:
                    try {
                        String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                        Timber.d("JSONResult : %s", jsonString);

                        JSONObject form = new JSONObject(jsonString);
                        String encounter_type = form.getString(JsonFormUtils.ENCOUNTER_TYPE);
                        // process child registration
                        if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyRegister.updateEventType)) {

                            presenter().updateFamilyRegister(jsonString);
                            presenter().verifyHasPhone();

                        } else if (encounter_type.equals(CoreConstants.EventType.CHILD_REGISTRATION)) {

                            presenter().saveChildForm(jsonString, false);

                        } else if (encounter_type.equals(Utils.metadata().familyMemberRegister.registerEventType)) {

                            String careGiver = presenter().saveChwFamilyMember(jsonString);
                            if (presenter().updatePrimaryCareGiver(getApplicationContext(), jsonString, familyBaseEntityId, careGiver)) {
                                setPrimaryCaregiver(careGiver);
                                refreshPresenter();
                                refreshMemberFragment(careGiver, null);
                            }

                            presenter().verifyHasPhone();
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CoreConstants.ProfileActivityResults.CHANGE_COMPLETED:
                    try {

                        String careGiverID = data.getStringExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
                        String familyHeadID = data.getStringExtra(Constants.INTENT_KEY.FAMILY_HEAD);

                        setPrimaryCaregiver(careGiverID);
                        setFamilyHead(familyHeadID);

                        refreshMemberFragment(careGiverID, familyHeadID);
                        presenter().verifyHasPhone();

                    } catch (Exception e) {
                        Timber.e(e);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    @Override
    protected void initializePresenter() {
        super.initializePresenter();
        presenter = new FamilyProfilePresenter(this, new FamilyProfileModel(familyName), familyBaseEntityId, familyHead, primaryCaregiver, familyName);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(FamilyProfileMemberFragment.newInstance(this.getIntent().getExtras()),
                this.getString(org.smartregister.family.R.string.member).toUpperCase());

        viewPager.setAdapter(adapter);

        if (getIntent().getBooleanExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, false) ||
                getIntent().getBooleanExtra(Constants.INTENT_KEY.GO_TO_DUE_PAGE, false)) {
            viewPager.setCurrentItem(1);
        }

        return viewPager;
    }

    @Override
    public void refreshMemberList(FetchStatus fetchStatus) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            for (int i = 0; i < adapter.getCount(); i++) {
                refreshList(adapter.getItem(i));
            }
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    for (int i = 0; i < adapter.getCount(); i++) {
                        refreshList(adapter.getItem(i));
                    }
                }
            });
        }
    }

    protected void refreshList(Fragment fragment) {
        if (fragment instanceof FamilyProfileMemberFragment) {
            FamilyProfileMemberFragment familyProfileMemberFragment = ((FamilyProfileMemberFragment) fragment);
            if (familyProfileMemberFragment.presenter() != null) {
                familyProfileMemberFragment.refreshListView();
            }
        }
    }

    private void refreshPresenter() {
        presenter = new FamilyProfilePresenter(this, new FamilyProfileModel(familyName), familyBaseEntityId, familyHead, primaryCaregiver, familyName);
    }

    private void refreshMemberFragment(String careGiverID, String familyHeadID) {
        BaseFamilyProfileMemberFragment memberFragment = this.getProfileMemberFragment();
        if (memberFragment != null) {
            if (StringUtils.isNotBlank(careGiverID)) {
                memberFragment.setPrimaryCaregiver(careGiverID);
            }
            if (StringUtils.isNotBlank(familyHeadID)) {
                memberFragment.setFamilyHead(familyHeadID);
            }
            refreshMemberList(FetchStatus.fetched);
        }
    }

    public void updateDueCount(final int dueCount) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                adapter.updateCount(Pair.create(1, dueCount));
            }
        });
    }

}
