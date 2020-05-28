package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.contract.AllClientsMemberContract;
import org.smartregister.chw.core.activity.CoreFamilyOtherMemberProfileActivity;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.form_data.NativeFormsDataBinder;
import org.smartregister.chw.core.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.custom_view.FamilyMemberFloatingMenu;
import org.smartregister.chw.dataloader.FamilyMemberDataLoader;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.chw.fragment.FamilyOtherMemberProfileFragment;
import org.smartregister.chw.presenter.AllClientsMemberPresenter;
import org.smartregister.chw.presenter.FamilyOtherMemberActivityPresenter;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.Utils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyOtherMemberProfileFragment;
import org.smartregister.family.model.BaseFamilyOtherMemberProfileActivityModel;
import org.smartregister.family.util.Constants.JSON_FORM_EXTRA;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.opd.activity.BaseOpdFormActivity;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.view.contract.BaseProfileContract;
import org.smartregister.view.customcontrols.CustomFontTextView;

import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.COUNT;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static org.smartregister.chw.core.utils.CoreReferralUtils.getCommonRepository;
import static org.smartregister.chw.core.utils.FormUtils.getFormUtils;
import static org.smartregister.family.util.JsonFormUtils.STEP2;

public class AllClientsMemberProfileActivity extends CoreFamilyOtherMemberProfileActivity implements OnClickFloatingMenu, AllClientsMemberContract.View {

    private FamilyMemberFloatingMenu familyFloatingMenu;
    private RelativeLayout layoutFamilyHasRow;
    private CustomFontTextView familyHeadTextView;
    private CustomFontTextView careGiverTextView;
    private AllClientsMemberContract.Presenter allClientsMemberPresenter;

    @Override
    protected void onCreation() {
        setIndependentClient(true);
        setContentView(R.layout.activity_all_clients_member_profile);

        Toolbar toolbar = findViewById(org.smartregister.family.R.id.family_toolbar);
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


    @Override
    public void setFamilyServiceStatus(String status) {
        layoutFamilyHasRow.setVisibility(View.GONE);
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.return_to_all_client));
        layoutFamilyHasRow = findViewById(R.id.family_has_row);
        familyHeadTextView = findViewById(R.id.family_head);
        careGiverTextView = findViewById(R.id.primary_caregiver);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        layoutFamilyHasRow.setVisibility(View.GONE);
    }

    @Override
    public void toggleFamilyHead(boolean show) {
        familyHeadTextView.setVisibility(View.GONE);
    }

    @Override
    public void togglePrimaryCaregiver(boolean show) {
        careGiverTextView.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_location_info).setVisible(true);
        menu.findItem(R.id.action_hiv_registration).setVisible(true);
        menu.findItem(R.id.action_tb_registration).setVisible(true);
        menu.findItem(R.id.action_anc_registration).setVisible(false);
        menu.findItem(R.id.action_sick_child_follow_up).setVisible(false);
        menu.findItem(R.id.action_malaria_diagnosis).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int itemId = item.getItemId();
        if (itemId == R.id.action_location_info) {
            JSONObject preFilledForm = CoreJsonFormUtils.getAutoPopulatedJsonEditFormString(
                    CoreConstants.JSON_FORM.getFamilyDetailsRegister(), this,
                    getFamilyRegistrationDetails(), Utils.metadata().familyRegister.updateEventType);
            if (preFilledForm != null) startFormActivity(preFilledForm);
            return true;
        } else if (itemId == R.id.action_hiv_registration) {
            //TODO Start HIV registration form
            return true;
        } else if (itemId == R.id.action_tb_registration) {
            //TODO Start HIV registration form
            return true;
        }
        return true;
    }

    @Override
    public FamilyOtherMemberActivityPresenter presenter() {
        return (FamilyOtherMemberActivityPresenter) presenter;
    }

    @Override
    protected void startAncRegister() {
        AncRegisterActivity.startAncRegistrationActivity(AllClientsMemberProfileActivity.this, baseEntityId, PhoneNumber,
                Constants.JSON_FORM.getAncRegistration(), null, familyBaseEntityId, familyName);
    }

    @Override
    protected void startMalariaRegister() {
        MalariaRegisterActivity.startMalariaRegistrationActivity(AllClientsMemberProfileActivity.this, baseEntityId, familyBaseEntityId);
    }

    @Override
    protected void startHivRegister() {
        HivRegisterActivity.startHIVFormActivity(AllClientsMemberProfileActivity.this,baseEntityId,org.smartregister.chw.util.Constants.JSON_FORM.getHivRegistration(),getFormUtils().getFormJsonFromRepositoryOrAssets(org.smartregister.chw.util.Constants.JSON_FORM.getHivRegistration()).toString());
    }

    @Override
    protected void startTbRegister() {
        TbRegisterActivity.startTbRegistrationActivity(AllClientsMemberProfileActivity.this, baseEntityId);
    }

    @Override
    protected void startFpRegister() {
        String dob = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false);
        String gender = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.GENDER, false);

        FpRegisterActivity.startFpRegistrationActivity(this, baseEntityId, dob, CoreConstants.JSON_FORM.getFpRegistrationForm(gender),
                FamilyPlanningConstants.ActivityPayload.REGISTRATION_PAYLOAD_TYPE);
    }


    @Override
    protected void startFpChangeMethod() {
        String dob = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false);
        String gender = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.GENDER, false);

        FpRegisterActivity.startFpRegistrationActivity(this, baseEntityId, dob, CoreConstants.JSON_FORM.getFpChangeMethodForm(gender),
                FamilyPlanningConstants.ActivityPayload.CHANGE_METHOD_PAYLOAD_TYPE);
    }

    @Override
    protected void removeIndividualProfile() {
        IndividualProfileRemoveActivity.startIndividualProfileActivity(AllClientsMemberProfileActivity.this,
                commonPersonObject, familyBaseEntityId, familyHead, primaryCaregiver, AllClientsRegisterActivity.class.getCanonicalName());
    }

    @Override
    protected void startEditMemberJsonForm(Integer title_resource, CommonPersonObjectClient client) {
        String titleString = title_resource != null ? getResources().getString(title_resource) : null;
        CommonPersonObjectClient commonPersonObjectClient = getFamilyRegistrationDetails();
        String uniqueID = commonPersonObjectClient.getColumnmaps().get(DBConstants.KEY.UNIQUE_ID);
        boolean isPrimaryCareGiver = commonPersonObject.getCaseId().equalsIgnoreCase(primaryCaregiver);

        NativeFormsDataBinder binder = new NativeFormsDataBinder(getContext(), commonPersonObject.getCaseId());
        binder.setDataLoader(new FamilyMemberDataLoader(familyName, isPrimaryCareGiver, titleString,
                Utils.metadata().familyMemberRegister.updateEventType, uniqueID));
        JSONObject jsonObject = binder.getPrePopulatedForm(Constants.ALL_CLIENT_REGISTRATION_FORM);

        try {
            //Remove the first step and use the updated one
            if (jsonObject != null && jsonObject.has(STEP1)) {

                jsonObject.put(JsonFormUtils.ENTITY_ID, baseEntityId);
                jsonObject.put(COUNT, "1");
                jsonObject.remove(STEP1);
                jsonObject.put(STEP1, jsonObject.getJSONObject(STEP2));
                jsonObject.remove(STEP2);
                startFormActivity(jsonObject);
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @NotNull
    private CommonPersonObjectClient getFamilyRegistrationDetails() {
        //Update common person client object with all details from family register table
        final CommonPersonObject personObject = getCommonRepository(Utils.metadata().familyRegister.tableName)
                .findByBaseEntityId(familyBaseEntityId);
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient(personObject.getCaseId(),
                personObject.getDetails(), "");
        commonPersonObjectClient.setColumnmaps(personObject.getColumnmaps());
        commonPersonObjectClient.setDetails(personObject.getDetails());
        return commonPersonObjectClient;
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, BaseOpdFormActivity.class);
        intent.putExtra(OpdConstants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
        Form form = new Form();
        form.setName(getString(R.string.update_client_registration));
        form.setActionBarBackground(R.color.family_actionbar);
        form.setNavigationBackground(R.color.family_navigation);
        form.setHomeAsUpIndicator(R.mipmap.ic_cross_white);
        form.setPreviousLabel(getResources().getString(R.string.back));
        form.setWizard(false);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected BaseProfileContract.Presenter getFamilyOtherMemberActivityPresenter(
            String familyBaseEntityId, String baseEntityId, String familyHead, String primaryCaregiver, String villageTown, String familyName) {
        return new FamilyOtherMemberActivityPresenter(this, new BaseFamilyOtherMemberProfileActivityModel(),
                null, familyBaseEntityId, baseEntityId, familyHead, primaryCaregiver, villageTown, familyName);
    }

    @Override
    protected FamilyMemberFloatingMenu getFamilyMemberFloatingMenu() {
        if (familyFloatingMenu == null) {
            familyFloatingMenu = new FamilyMemberFloatingMenu(this);
        }
        return familyFloatingMenu;
    }

    @Override
    protected Context getFamilyOtherMemberProfileActivity() {
        return AllClientsMemberProfileActivity.this;
    }

    @Override
    protected Class<? extends CoreFamilyProfileActivity> getFamilyProfileActivity() {
        return FamilyProfileActivity.class;
    }

    @Override
    protected void initializePresenter() {
        super.initializePresenter();
        onClickFloatingMenu = this;
        allClientsMemberPresenter = new AllClientsMemberPresenter(this, baseEntityId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        try {
            String jsonString = data.getStringExtra(JSON_FORM_EXTRA.JSON);
            JSONObject form = new JSONObject(jsonString);
            if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyRegister.updateEventType)) {
                getAllClientsMemberPresenter().updateLocationInfo(jsonString, familyBaseEntityId);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        BaseFamilyOtherMemberProfileFragment profileOtherMemberFragment = FamilyOtherMemberProfileFragment.newInstance(this.getIntent().getExtras());
        adapter.addFragment(profileOtherMemberFragment, "");

        viewPager.setAdapter(adapter);

        return viewPager;
    }

    @Override
    protected BaseFamilyOtherMemberProfileFragment getFamilyOtherMemberProfileFragment() {
        return FamilyOtherMemberProfileFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected void startMalariaFollowUpVisit() {
        MalariaFollowUpVisitActivity.startMalariaFollowUpActivity(this, baseEntityId);
    }

    @Override
    protected void setIndependentClient(boolean isIndependentClient) {
        super.isIndependent = isIndependentClient;
    }

    @Override
    public void onClickMenu(int viewId) {
        switch (viewId) {
            case R.id.call_layout:
                FamilyCallDialogFragment.launchDialog(this, familyBaseEntityId);
                break;
            case R.id.refer_to_facility_layout:
                Utils.launchClientReferralActivity(this, Utils.getCommonReferralTypes(this), baseEntityId);
                break;
            default:
                break;
        }
    }

    @Override
    public AllClientsMemberContract.Presenter getAllClientsMemberPresenter() {
        return allClientsMemberPresenter;
    }
}
