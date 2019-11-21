package org.smartregister.brac.hnpp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.smartregister.brac.hnpp.custom_view.FamilyMemberFloatingMenu;
import org.smartregister.brac.hnpp.fragment.HnppMemberProfileDueFragment;
import org.smartregister.brac.hnpp.fragment.HnppMemberProfileOtherServiceFragment;
import org.smartregister.brac.hnpp.fragment.MemberProfileActivityFragment;
import org.smartregister.brac.hnpp.utils.HnppChildUtils;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.brac.hnpp.utils.HnppJsonFormUtils;
import org.smartregister.brac.hnpp.utils.HnppUtils;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.activity.CoreFamilyOtherMemberProfileActivity;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.custom_views.CoreFamilyMemberFloatingMenu;
import org.smartregister.chw.core.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.fragment.FamilyOtherMemberProfileFragment;
import org.smartregister.brac.hnpp.presenter.HnppFamilyOtherMemberActivityPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyOtherMemberProfileFragment;
import org.smartregister.family.fragment.BaseFamilyProfileActivityFragment;
import org.smartregister.family.fragment.BaseFamilyProfileDueFragment;
import org.smartregister.family.model.BaseFamilyOtherMemberProfileActivityModel;
import org.smartregister.family.util.Constants;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.view.contract.BaseProfileContract;
import org.smartregister.view.customcontrols.CustomFontTextView;

import timber.log.Timber;

import static org.smartregister.brac.hnpp.utils.HnppConstants.MEMBER_ID_SUFFIX;

public class HnppFamilyOtherMemberProfileActivity extends CoreFamilyOtherMemberProfileActivity {
    private CustomFontTextView textViewDetails3;
    private String familyBaseEntityId;

    private TextView textViewAge,textViewName;

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_other_member_profile);

        Toolbar toolbar = findViewById(org.smartregister.family.R.id.family_toolbar);
        HnppConstants.updateAppBackground(toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        appBarLayout = findViewById(org.smartregister.family.R.id.toolbar_appbarlayout);

        imageRenderHelper = new ImageRenderHelper(this);

        initializePresenter();

        setupViews();
    }
    public void updateDueCount(final int dueCount) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> adapter.updateCount(Pair.create(1, dueCount)));
    }
    @Override
    public void setProfileName(String fullName) {
       try{
           String[] str = fullName.split(",");
           this.textViewName.setText(str[0]);
           this.textViewAge.setText(getString(R.string.age,str[1]));
       }catch (Exception e){

       }

    }
    @Override
    public void setFamilyServiceStatus(String status) {

    }

    @Override
    public void setProfileDetailOne(String detailOne) {
        ((TextView)findViewById(R.id.textview_detail_one)).setText(HnppConstants.getGender(detailOne));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        setupMenuOptions(menu);
        return true;
    }

    @Override
    protected void startAncRegister() {
//        //TODO implement start anc register for HF
        HnppAncRegisterActivity.startHnppAncRegisterActivity(HnppFamilyOtherMemberProfileActivity.this, baseEntityId, PhoneNumber,
                org.smartregister.brac.hnpp.utils.HnppConstants.JSON_FORM.getAncRegistration(), null, familyBaseEntityId, familyName);
    }

    @Override
    public void startMalariaRegister() {
        //TODO implement start anc malaria for HF
        HnppHomeVisitActivity.startMe(this, new MemberObject(commonPersonObject), false);

    }

    @Override
    protected void removeIndividualProfile() {
        Timber.d("Remove member action is not required in HF");
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        findViewById(org.smartregister.chw.core.R.id.viewpager).setVisibility(View.VISIBLE);

        textViewDetails3 = findViewById(R.id.textview_detail_three);
        textViewAge = findViewById(R.id.textview_age);
        textViewName = findViewById(R.id.textview_name);

    }

    @Override
    public void setProfileDetailThree(String detailThree) {
        super.setProfileDetailThree(detailThree);
        if(!TextUtils.isEmpty(detailThree)) {
            detailThree = detailThree.replace(Constants.IDENTIFIER.FAMILY_SUFFIX,"")
                    .replace(HnppConstants.IDENTIFIER.FAMILY_TEXT,"");
            detailThree = detailThree.substring(detailThree.length() - MEMBER_ID_SUFFIX);
            textViewDetails3.setText("ID: " + detailThree);
        }
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        BaseFamilyProfileDueFragment profileMemberFragment = HnppMemberProfileDueFragment.newInstance(this.getIntent().getExtras());
        BaseFamilyProfileDueFragment otherServiceFragment = HnppMemberProfileOtherServiceFragment.newInstance(this.getIntent().getExtras());
        BaseFamilyProfileActivityFragment activityFragment = MemberProfileActivityFragment.newInstance(this.getIntent().getExtras());

        adapter.addFragment(profileMemberFragment, this.getString(R.string.due).toUpperCase());
        adapter.addFragment(otherServiceFragment, this.getString(R.string.other_service).toUpperCase());
        adapter.addFragment(activityFragment, this.getString(R.string.activity).toUpperCase());

        viewPager.setAdapter(adapter);

        return viewPager;
    }

    @Override
    protected void startEditMemberJsonForm(Integer title_resource, CommonPersonObjectClient client) {


        try {
            JSONObject form = HnppJsonFormUtils.getAutoPopulatedJsonEditFormString(CoreConstants.JSON_FORM.getFamilyMemberRegister(), this, client, Utils.metadata().familyMemberRegister.updateEventType);
            String moduleId = HnppChildUtils.getModuleId(familyHead);
            HnppJsonFormUtils.updateFormWithModuleId(form,moduleId,familyBaseEntityId);
            HnppJsonFormUtils.updateFormWithSimPrintsEnable(form);
            startFormActivity(form);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected BaseProfileContract.Presenter getFamilyOtherMemberActivityPresenter(
            String familyBaseEntityId, String baseEntityId, String familyHead, String primaryCaregiver, String villageTown, String familyName) {
        this.familyBaseEntityId = familyBaseEntityId;
        return new HnppFamilyOtherMemberActivityPresenter(this, new BaseFamilyOtherMemberProfileActivityModel(),
                null, familyBaseEntityId, baseEntityId, familyHead, primaryCaregiver, villageTown, familyName);
    }

    @Override
    protected CoreFamilyMemberFloatingMenu getFamilyMemberFloatingMenu() {
        if (familyFloatingMenu == null) {
            prepareFab();
        }
        return familyFloatingMenu;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_malaria_followup_visit) {
            startMalariaRegister();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected Context getFamilyOtherMemberProfileActivity() {
        return HnppFamilyOtherMemberProfileActivity.this;
    }

    @Override
    protected Class<? extends CoreFamilyProfileActivity> getFamilyProfileActivity() {
        return FamilyProfileActivity.class;
    }

    @Override
    public void updateHasPhone(boolean hasPhone) {
        super.updateHasPhone(hasPhone);
        if (!hasPhone) {
            familyFloatingMenu.hideFab();
        }

    }
    public void openFamilyDueTab() {
        Intent intent = new Intent(this, getFamilyProfileActivity());

        intent.putExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, familyBaseEntityId);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, familyHead);
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, primaryCaregiver);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_NAME, familyName);

        intent.putExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, true);
        startActivity(intent);
    }
    public void openRefereal() {
        Toast.makeText(this,"Open referel",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void initializePresenter() {
        super.initializePresenter();
        onClickFloatingMenu = viewId -> {
            if (viewId == R.id.call_layout) {
                FamilyCallDialogFragment.launchDialog(this, familyBaseEntityId);
            }
        };
    }

    @Override
    protected BaseFamilyOtherMemberProfileFragment getFamilyOtherMemberProfileFragment() {
        return FamilyOtherMemberProfileFragment.newInstance(getIntent().getExtras());
    }

    private void prepareFab() {
        familyFloatingMenu = new FamilyMemberFloatingMenu(this);
        familyFloatingMenu.fab.setOnClickListener(v -> FamilyCallDialogFragment.launchDialog(this, familyBaseEntityId));
    }
    public static void startMe(Activity activity, MemberObject memberObject, String familyHeadName, String familyHeadPhoneNumber, CommonPersonObjectClient patient) {

    }

    private void setupMenuOptions(Menu menu) {

        menu.findItem(R.id.action_malaria_registration).setVisible(false);
        menu.findItem(R.id.action_malaria_followup_visit).setVisible(true);
        menu.findItem(R.id.action_sick_child_follow_up).setVisible(false);
        menu.findItem(R.id.action_malaria_diagnosis).setVisible(false);
        menu.findItem(R.id.action_remove_member).setVisible(false);
        if (HnppUtils.isWomanOfReproductiveAge(commonPersonObject)) {
            menu.findItem(R.id.action_anc_registration).setVisible(true);
        } else {
            menu.findItem(R.id.action_anc_registration).setVisible(false);
        }

    }
}
