package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import org.smartregister.chw.anc.activity.BaseAncMemberProfileActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.activity.CoreAboveFiveChildProfileActivity;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.activity.CoreFamilyProfileMenuActivity;
import org.smartregister.chw.core.activity.CoreFamilyRemoveMemberActivity;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fragment.FamilyProfileActivityFragment;
import org.smartregister.chw.fragment.FamilyProfileDueFragment;
import org.smartregister.chw.fragment.FamilyProfileMemberFragment;
import org.smartregister.chw.model.FamilyProfileModel;
import org.smartregister.chw.pnc.activity.BasePncMemberProfileActivity;
import org.smartregister.chw.presenter.FamilyProfilePresenter;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileDueFragment;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;

import static org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.TITLE_VIEW_TEXT;

public class FamilyProfileActivity extends CoreFamilyProfileActivity {
    private FamilyProfileActivityFragment profileActivityFragment;
    private BaseFamilyProfileDueFragment profileDueFragment;

    public void updateWashCheckActivity() {
        profileActivityFragment.updateWashCheck();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && profileDueFragment != null) {
            profileDueFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void refreshPresenter() {
        this.presenter = new FamilyProfilePresenter(this, new FamilyProfileModel(familyName),
                familyBaseEntityId, familyHead, primaryCaregiver, familyName);
    }

    @Override
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
        presenter = new FamilyProfilePresenter(this, new FamilyProfileModel(familyName), familyBaseEntityId, familyHead, primaryCaregiver, familyName);
    }

    public FamilyProfilePresenter getFamilyProfilePresenter() {
        return (FamilyProfilePresenter) presenter;
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        BaseFamilyProfileMemberFragment profileMemberFragment = FamilyProfileMemberFragment.newInstance(this.getIntent().getExtras());
        profileDueFragment = FamilyProfileDueFragment.newInstance(this.getIntent().getExtras());
        profileActivityFragment = (FamilyProfileActivityFragment) FamilyProfileActivityFragment.newInstance(this.getIntent().getExtras());

        adapter.addFragment(profileMemberFragment, this.getString(org.smartregister.family.R.string.member).toUpperCase());
        adapter.addFragment(profileDueFragment, this.getString(org.smartregister.family.R.string.due).toUpperCase());
        adapter.addFragment(profileActivityFragment, this.getString(org.smartregister.family.R.string.activity).toUpperCase());

        viewPager.setAdapter(adapter);

        if (getIntent().getBooleanExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, false) || getIntent().getBooleanExtra(Constants.INTENT_KEY.GO_TO_DUE_PAGE, false)) {
            viewPager.setCurrentItem(1);
        }

        return viewPager;
    }

    public void goToProfileActivity(View view, Bundle fragmentArguments) {
        if (view.getTag() instanceof CommonPersonObjectClient) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) view.getTag();
            String entityType = Utils.getValue(commonPersonObjectClient.getColumnmaps(), ChildDBConstants.KEY.ENTITY_TYPE, false);
            if (CoreConstants.TABLE_NAME.FAMILY_MEMBER.equals(entityType)) {
                if (isAncMember(commonPersonObjectClient.entityId())) {
                    goToAncProfileActivity(commonPersonObjectClient, fragmentArguments);
                } else if (isPncMember(commonPersonObjectClient.entityId())) {
                    gotToPncProfileActivity(commonPersonObjectClient, fragmentArguments);
                } else {
                    goToOtherMemberProfileActivity(commonPersonObjectClient, fragmentArguments);
                }
            } else {
                goToChildProfileActivity(commonPersonObjectClient, fragmentArguments);
            }
        }
    }

    public void goToOtherMemberProfileActivity(CommonPersonObjectClient patient, Bundle bundle) {
        Intent intent = new Intent(this, getFamilyOtherMemberProfileActivityClass());
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, patient);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, getFamilyHead());
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, getPrimaryCaregiver());
        startActivity(intent);
    }

    public void goToChildProfileActivity(CommonPersonObjectClient patient, Bundle bundle) {
        String dobString = Utils.getDuration(Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.DOB, false));
        Integer yearOfBirth = CoreChildUtils.dobStringToYear(dobString);
        Intent intent;
        if (yearOfBirth != null && yearOfBirth >= 5) {
            intent = new Intent(this, getAboveFiveChildProfileActivityClass());
        } else {
            intent = new Intent(this, getChildProfileActivityClass());
        }
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra(CoreConstants.INTENT_KEY.IS_COMES_FROM_FAMILY, true);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, new MemberObject(patient));
        startActivity(intent);
    }

    public void goToAncProfileActivity(CommonPersonObjectClient patient, Bundle bundle) {
        patient.getColumnmaps().putAll(getAncCommonPersonObject(patient.entityId()).getColumnmaps());
        startActivity(initProfileActivityIntent(patient, bundle, getAncMemberProfileActivityClass()));
    }

    public void gotToPncProfileActivity(CommonPersonObjectClient patient, Bundle bundle) {
        patient.getColumnmaps().putAll(getPncCommonPersonObject(patient.entityId()).getColumnmaps());
        startActivity(initProfileActivityIntent(patient, bundle, getPncMemberProfileActivityClass()));
    }

    private Intent initProfileActivityIntent(CommonPersonObjectClient patient, Bundle bundle, Class activityClass) {
        HashMap<String, String> familyHeadDetails = getAncFamilyHeadNameAndPhone(getFamilyHead());
        String familyHeadName = "";
        String familyHeadPhone = "";
        if (familyHeadDetails != null) {
            familyHeadName = familyHeadDetails.get(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_NAME);
            familyHeadPhone = familyHeadDetails.get(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_PHONE);
        }
        Intent intent = new Intent(this, activityClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, new MemberObject(patient));
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_NAME, familyHeadName);
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_PHONE, familyHeadPhone);
        intent.putExtra(TITLE_VIEW_TEXT, String.format(getString(org.smartregister.chw.core.R.string.return_to_family_name), ""));
        return intent;
    }

    @Override
    protected Class<?> getFamilyOtherMemberProfileActivityClass() {
        return FamilyOtherMemberProfileActivity.class;
    }

    @Override
    protected Class<? extends CoreAboveFiveChildProfileActivity> getAboveFiveChildProfileActivityClass() {
        return AboveFiveChildProfileActivity.class;
    }

    @Override
    protected Class<? extends CoreChildProfileActivity> getChildProfileActivityClass() {
        return ChildProfileActivity.class;
    }

    @Override
    protected Class<? extends BaseAncMemberProfileActivity> getAncMemberProfileActivityClass() {
        return AncMemberProfileActivity.class;
    }

    @Override
    protected Class<? extends BasePncMemberProfileActivity> getPncMemberProfileActivityClass() {
        return PncMemberProfileActivity.class;
    }

    @Override
    protected boolean isAncMember(String baseEntityId) {
        return getFamilyProfilePresenter().isAncMember(baseEntityId);
    }

    @Override
    protected HashMap<String, String> getAncFamilyHeadNameAndPhone(String baseEntityId) {
        return getFamilyProfilePresenter().getAncFamilyHeadNameAndPhone(baseEntityId);
    }

    @Override
    protected CommonPersonObject getAncCommonPersonObject(String baseEntityId) {
        return getFamilyProfilePresenter().getAncCommonPersonObject(baseEntityId);
    }

    @Override
    protected boolean isPncMember(String baseEntityId) {
        return getFamilyProfilePresenter().isPncMember(baseEntityId);
    }

    @Override
    protected CommonPersonObject getPncCommonPersonObject(String baseEntityId) {
        return getFamilyProfilePresenter().getPncCommonPersonObject(baseEntityId);
    }
}