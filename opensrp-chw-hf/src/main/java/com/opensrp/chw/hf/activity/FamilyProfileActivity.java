package com.opensrp.chw.hf.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.opensrp.chw.core.activity.CoreFamilyProfileActivity;
import com.opensrp.chw.core.activity.CoreFamilyProfileMenuActivity;
import com.opensrp.chw.core.activity.CoreFamilyRemoveMemberActivity;
import com.opensrp.chw.core.utils.CoreConstants;
import com.opensrp.chw.hf.fragement.FamilyProfileMemberFragment;
import com.opensrp.chw.hf.model.FamilyProfileModel;
import com.opensrp.chw.hf.presenter.FamilyProfilePresenter;

import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.util.Constants;

public class FamilyProfileActivity extends CoreFamilyProfileActivity {

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
    protected Class<? extends CoreFamilyRemoveMemberActivity> getFamilyRemoveMemberClass() {
        return FamilyRemoveMemberActivity.class;
    }

    @Override
    protected Class<? extends CoreFamilyProfileMenuActivity> getFamilyProfileMenuClass() {
        return FamilyProfileMenuActivity.class;
    }

    protected void refreshList(Fragment fragment) {
        if (fragment instanceof FamilyProfileMemberFragment) {
            FamilyProfileMemberFragment familyProfileMemberFragment = ((FamilyProfileMemberFragment) fragment);
            if (familyProfileMemberFragment.presenter() != null) {
                familyProfileMemberFragment.refreshListView();
            }
        }
    }

    @Override
    protected void refreshPresenter() {
        presenter = new FamilyProfilePresenter(this, new FamilyProfileModel(familyName), familyBaseEntityId, familyHead, primaryCaregiver, familyName);
    }

}
