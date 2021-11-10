package org.smartregister.chw.activity;

import static org.smartregister.chw.core.utils.Utils.passToolbarTitle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.joda.time.DateTime;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.activity.BaseAncMemberProfileActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.activity.CoreAboveFiveChildProfileActivity;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.activity.CoreFamilyProfileMenuActivity;
import org.smartregister.chw.core.activity.CoreFamilyRemoveMemberActivity;
import org.smartregister.chw.core.listener.FloatingMenuListener;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.custom_view.ChwFamilyFloatingMenu;
import org.smartregister.chw.dao.ChwChildDao;
import org.smartregister.chw.dao.FamilyDao;
import org.smartregister.chw.fp.dao.FpDao;
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
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FamilyProfileActivity extends CoreFamilyProfileActivity {
    private BaseFamilyProfileDueFragment profileDueFragment;

    @Override
    public void setupViews() {
        super.setupViews();

        // Update profile border
        CircleImageView profileView = findViewById(org.smartregister.chw.core.R.id.imageview_profile);
        profileView.setBorderWidth(2);

        // add floating menu
        familyFloatingMenu = new ChwFamilyFloatingMenu(this);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        familyFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        addContentView(familyFloatingMenu, linearLayoutParams);
        familyFloatingMenu.setClickListener(
                FloatingMenuListener.getInstance(this, presenter().familyBaseEntityId())
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Integer noOfAdults = FamilyDao.countAdultsFamilyMembers(familyBaseEntityId);
        MenuItem cannotChangeCaregiver = menu.findItem(R.id.action_not_able_to_change_caregiver);
        MenuItem cannotChangeFamilyHead = menu.findItem(R.id.action_not_able_to_change_head);
        MenuItem changeCaregiver = menu.findItem(R.id.action_change_care_giver);
        MenuItem changeFamilyHead = menu.findItem(R.id.action_change_head);

        if (ChwApplication.getApplicationFlavor().hideCaregiverAndFamilyHeadWhenOnlyOneAdult() && noOfAdults == 1) {
            cannotChangeCaregiver.setVisible(true);
            cannotChangeFamilyHead.setVisible(true);
        }
        if ((!ChwApplication.getApplicationFlavor().hideCaregiverAndFamilyHeadWhenOnlyOneAdult())
                || (ChwApplication.getApplicationFlavor().hideCaregiverAndFamilyHeadWhenOnlyOneAdult() && noOfAdults > 1)) {
            changeCaregiver.setVisible(true);
            changeFamilyHead.setVisible(true);
        }
        return true;
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
        FamilyProfileMemberFragment profileMemberFragment = (FamilyProfileMemberFragment) FamilyProfileMemberFragment.newInstance(this.getIntent().getExtras());
        profileDueFragment = FamilyProfileDueFragment.newInstance(this.getIntent().getExtras());
        FamilyProfileActivityFragment profileActivityFragment = (FamilyProfileActivityFragment) FamilyProfileActivityFragment.newInstance(this.getIntent().getExtras());

        adapter.addFragment(profileMemberFragment, this.getString(org.smartregister.family.R.string.member).toUpperCase());
        adapter.addFragment(profileDueFragment, this.getString(org.smartregister.family.R.string.due).toUpperCase());
        adapter.addFragment(profileActivityFragment, this.getString(org.smartregister.family.R.string.activity).toUpperCase());

        viewPager.setAdapter(adapter);

        if (getIntent().getBooleanExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, false) || getIntent().getBooleanExtra(Constants.INTENT_KEY.GO_TO_DUE_PAGE, false)) {
            viewPager.setCurrentItem(1);
        }

        return viewPager;
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
    protected void goToFpProfile(String baseEntityId, Activity activity) {
        FamilyPlanningMemberProfileActivity.startFpMemberProfileActivity(activity, FpDao.getMember(baseEntityId));
    }


    @Override
    protected boolean isAncMember(String baseEntityId) {
        return ChwApplication.getApplicationFlavor().hasANC() && getFamilyProfilePresenter().isAncMember(baseEntityId);
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
        return ChwApplication.getApplicationFlavor().hasPNC() && getFamilyProfilePresenter().isPncMember(baseEntityId);
    }

    @Override
    protected CommonPersonObject getPncCommonPersonObject(String baseEntityId) {
        return getFamilyProfilePresenter().getPncCommonPersonObject(baseEntityId);
    }

    @Override
    public Context getApplicationContext() {
        return this;
    }

    private Intent getDefaultChildrenIntent(int age) {
        if (age < 5) {
            return new Intent(this, getChildProfileActivityClass());
        } else {
            return new Intent(this, getAboveFiveChildProfileActivityClass());
        }
    }

    private Intent getIntentForChildrenUnderFiveAndGirlsAgeNineToEleven(int age, String gender) {
        if (age < 5 || (gender.equalsIgnoreCase("Female") && (age >= 9 && age < 11))) {
            return new Intent(this, getChildProfileActivityClass());
        } else {
            return new Intent(this, getAboveFiveChildProfileActivityClass());
        }
    }

    private Intent getChildIntent(CommonPersonObjectClient patient) {
        String dobString = Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.DOB, false);
        //Today's date
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(new DateTime(dobString).toLocalDate().getYear(), new DateTime(dobString).toLocalDate().getMonthOfYear(), new DateTime(dobString).toLocalDate().getDayOfMonth()); //Birth date
        Period p = Period.between(birthday, today);
        int age = p.getYears();

        String gender = ChwChildDao.getChildGender(patient.entityId());
        if (ChwApplication.getApplicationFlavor().showChildrenUnderFiveAndGirlsAgeNineToEleven()) {
            return getIntentForChildrenUnderFiveAndGirlsAgeNineToEleven(age, gender);
        } else {
            return getDefaultChildrenIntent(age);
        }
    }

    @Override
    public void goToChildProfileActivity(CommonPersonObjectClient patient, Bundle bundle) {
        Intent intent = getChildIntent(patient);

        if (bundle != null) {
            intent.putExtras(bundle);
        }
        MemberObject memberObject = new MemberObject(patient);
        memberObject.setFamilyName(familyName);
        passToolbarTitle(this, intent);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        startActivity(intent);
    }
}
