package org.smartgresiter.wcaro.activity;

import android.support.v4.view.ViewPager;

import org.smartgresiter.wcaro.fragment.FamilyProfileActivityFragment;
import org.smartgresiter.wcaro.fragment.FamilyProfileDueFragment;
import org.smartgresiter.wcaro.fragment.FamilyProfileMemberFragment;
import org.smartgresiter.wcaro.model.FamilyProfileModel;
import org.smartgresiter.wcaro.presenter.FamilyProfilePresenter;
import org.smartregister.family.activity.BaseFamilyProfileActivity;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileActivityFragment;
import org.smartregister.family.fragment.BaseFamilyProfileDueFragment;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;

public class FamilyProfileActivity extends BaseFamilyProfileActivity {

    @Override
    protected void initializePresenter() {
        String familyBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        presenter = new FamilyProfilePresenter(this, new FamilyProfileModel(), familyBaseEntityId);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        BaseFamilyProfileMemberFragment profileMemberFragment = FamilyProfileMemberFragment.newInstance(this.getIntent().getExtras());
        BaseFamilyProfileDueFragment profileDueFragment = FamilyProfileDueFragment.newInstance(this.getIntent().getExtras());
        BaseFamilyProfileActivityFragment profileActivityFragment = FamilyProfileActivityFragment.newInstance(this.getIntent().getExtras());

        adapter.addFragment(profileMemberFragment, this.getString(org.smartregister.family.R.string.member));
        adapter.addFragment(profileDueFragment, this.getString(org.smartregister.family.R.string.due));
        adapter.addFragment(profileActivityFragment, this.getString(org.smartregister.family.R.string.activity));

        viewPager.setAdapter(adapter);

        return viewPager;
    }

    @Override
    protected void onStart() {
        super.onStart();
        NavigationActivity.getInstance(this, null, null);
    }

}
