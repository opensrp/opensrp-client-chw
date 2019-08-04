package org.smartregister.chw.activity;

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
import com.opensrp.chw.core.activity.CoreFamilyProfileMenuActivity;
import com.opensrp.chw.core.utils.CoreConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.fragment.FamilyProfileActivityFragment;
import org.smartregister.chw.fragment.FamilyProfileDueFragment;
import org.smartregister.chw.fragment.FamilyProfileMemberFragment;
import org.smartregister.chw.model.FamilyProfileModel;
import org.smartregister.chw.presenter.FamilyProfilePresenter;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileActivityFragment;
import org.smartregister.family.fragment.BaseFamilyProfileDueFragment;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import timber.log.Timber;

public class FamilyProfileActivity extends CoreFamilyProfileActivity {

    @Override
    protected void initializePresenter() {
        super.initializePresenter();
        presenter = new FamilyProfilePresenter(this, new FamilyProfileModel(familyName), familyBaseEntityId, familyHead, primaryCaregiver, familyName);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        BaseFamilyProfileMemberFragment profileMemberFragment = FamilyProfileMemberFragment.newInstance(this.getIntent().getExtras());
        BaseFamilyProfileDueFragment profileDueFragment = FamilyProfileDueFragment.newInstance(this.getIntent().getExtras());
        BaseFamilyProfileActivityFragment profileActivityFragment = FamilyProfileActivityFragment.newInstance(this.getIntent().getExtras());

        adapter.addFragment(profileMemberFragment, this.getString(org.smartregister.family.R.string.member).toUpperCase());
        adapter.addFragment(profileDueFragment, this.getString(org.smartregister.family.R.string.due).toUpperCase());
        adapter.addFragment(profileActivityFragment, this.getString(org.smartregister.family.R.string.activity).toUpperCase());

        viewPager.setAdapter(adapter);

        if (getIntent().getBooleanExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, false) || getIntent().getBooleanExtra(Constants.INTENT_KEY.GO_TO_DUE_PAGE, false)) {
            viewPager.setCurrentItem(1);
        }

        return viewPager;
    }

    protected void refreshList(Fragment fragment) {
        if (fragment instanceof BaseRegisterFragment) {
            if (fragment instanceof FamilyProfileMemberFragment) {
                FamilyProfileMemberFragment familyProfileMemberFragment = ((FamilyProfileMemberFragment) fragment);
                if (familyProfileMemberFragment.presenter() != null) {
                    familyProfileMemberFragment.refreshListView();
                }
            } else if (fragment instanceof FamilyProfileDueFragment) {
                FamilyProfileDueFragment familyProfileDueFragment = ((FamilyProfileDueFragment) fragment);
                if (familyProfileDueFragment.presenter() != null) {
                    familyProfileDueFragment.refreshListView();
                }
            } else if (fragment instanceof FamilyProfileActivityFragment) {
                FamilyProfileActivityFragment familyProfileActivityFragment = ((FamilyProfileActivityFragment) fragment);
                if (familyProfileActivityFragment.presenter() != null) {
                    familyProfileActivityFragment.refreshListView();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_family_details:

                startFormForEdit();

                break;
            case R.id.action_remove_member:

                Intent frm_intent = new Intent(this, FamilyRemoveMemberActivity.class);
                frm_intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, getFamilyBaseEntityId());
                frm_intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_HEAD, familyHead);
                frm_intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER, primaryCaregiver);
                startActivityForResult(frm_intent, CoreConstants.ProfileActivityResults.CHANGE_COMPLETED);

                break;
            case R.id.action_change_head:

                Intent fh_intent = new Intent(this, FamilyProfileMenuActivity.class);
                fh_intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.BASE_ENTITY_ID, getFamilyBaseEntityId());
                fh_intent.putExtra(FamilyProfileMenuActivity.MENU, CoreConstants.MenuType.ChangeHead);
                startActivityForResult(fh_intent, CoreConstants.ProfileActivityResults.CHANGE_COMPLETED);

                break;
            case R.id.action_change_care_giver:


                Intent pc_intent = new Intent(this, FamilyProfileMenuActivity.class);
                pc_intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.BASE_ENTITY_ID, getFamilyBaseEntityId());
                pc_intent.putExtra(FamilyProfileMenuActivity.MENU, CoreConstants.MenuType.ChangePrimaryCare);
                startActivityForResult(pc_intent, CoreConstants.ProfileActivityResults.CHANGE_COMPLETED);

                break;
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
                case org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON:
                    try {
                        String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                        Timber.d("JSONResult : %s", jsonString);

                        JSONObject form = new JSONObject(jsonString);
                        String encounter_type = form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE);
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
