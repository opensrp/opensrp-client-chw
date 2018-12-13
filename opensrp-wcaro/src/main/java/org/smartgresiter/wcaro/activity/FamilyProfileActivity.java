package org.smartgresiter.wcaro.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.custom_view.FamilyFloatingMenu;
import org.smartgresiter.wcaro.fragment.FamilyProfileActivityFragment;
import org.smartgresiter.wcaro.fragment.FamilyProfileDueFragment;
import org.smartgresiter.wcaro.fragment.FamilyProfileMemberFragment;
import org.smartgresiter.wcaro.model.FamilyProfileModel;
import org.smartgresiter.wcaro.presenter.FamilyProfilePresenter;
import org.smartgresiter.wcaro.util.OnClickFloatingMenu;
import org.smartregister.family.activity.BaseFamilyProfileActivity;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileActivityFragment;
import org.smartregister.family.fragment.BaseFamilyProfileDueFragment;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;

public class FamilyProfileActivity extends BaseFamilyProfileActivity {
    private String familyBaseEntityId;
    //add floating menu at runtime.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FamilyFloatingMenu familyFloatingMenu=new FamilyFloatingMenu(this);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        familyFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        addContentView(familyFloatingMenu,linearLayoutParams);
        familyFloatingMenu.setClickListener(onClickFloatingMenuListener);
    }
    @Override
    protected void initializePresenter() {
        familyBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
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
    private OnClickFloatingMenu onClickFloatingMenuListener=new OnClickFloatingMenu() {
        @Override
        public void onClickMenu(int viewId) {
            switch (viewId){
                case R.id.call_layout:
                    Toast.makeText(FamilyProfileActivity.this,"Go to call screen",Toast.LENGTH_SHORT).show();
                    //go to child add form activity
                    break;
                case R.id.family_detail_layout:
                    Toast.makeText(FamilyProfileActivity.this,"Go to family details",Toast.LENGTH_SHORT).show();
                    //go to child add form activity
                    break;
                case R.id.add_new_member_layout:
                    Toast.makeText(FamilyProfileActivity.this,"Go to new family member add",Toast.LENGTH_SHORT).show();
                    //go to child add form activity
                    break;

                case R.id.remove_member_layout:
                    Toast.makeText(FamilyProfileActivity.this,"Go to remove member",Toast.LENGTH_SHORT).show();
                    //go to child add form activity
                    break;
                case R.id.change_head_layout:
                    Toast.makeText(FamilyProfileActivity.this,"Go to change family head",Toast.LENGTH_SHORT).show();
                    //go to child add form activity
                    break;
                case R.id.change_primary_layout:
                    Toast.makeText(FamilyProfileActivity.this,"Go to change primary caregiver",Toast.LENGTH_SHORT).show();
                    //go to child add form activity
                    break;
            }

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        NavigationActivity.getInstance(this, null, null);
    }

}
