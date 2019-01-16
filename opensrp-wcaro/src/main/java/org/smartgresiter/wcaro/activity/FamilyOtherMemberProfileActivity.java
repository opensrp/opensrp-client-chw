package org.smartgresiter.wcaro.activity;

import android.support.v4.view.ViewPager;

import org.smartgresiter.wcaro.fragment.FamilyOtherMemberProfileFragment;
import org.smartgresiter.wcaro.model.FamilyOtherMemberProfileActivityModel;
import org.smartgresiter.wcaro.presenter.FamilyOtherMemberActivityPresenter;
import org.smartregister.family.activity.BaseFamilyOtherMemberProfileActivity;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyOtherMemberProfileFragment;
import org.smartregister.family.util.Constants;

public class FamilyOtherMemberProfileActivity extends BaseFamilyOtherMemberProfileActivity {
    @Override
    protected void initializePresenter() {
        String baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        String familyHead = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_HEAD);
        String primaryCaregiver = getIntent().getStringExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
        String villageTown = getIntent().getStringExtra(Constants.INTENT_KEY.VILLAGE_TOWN);
        presenter = new FamilyOtherMemberActivityPresenter(this, new FamilyOtherMemberProfileActivityModel(), null, baseEntityId, familyHead, primaryCaregiver, villageTown);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        BaseFamilyOtherMemberProfileFragment profileOtherMemberFragment = FamilyOtherMemberProfileFragment.newInstance(this.getIntent().getExtras());
        adapter.addFragment(profileOtherMemberFragment, "");

        viewPager.setAdapter(adapter);

        return viewPager;
    }
}
