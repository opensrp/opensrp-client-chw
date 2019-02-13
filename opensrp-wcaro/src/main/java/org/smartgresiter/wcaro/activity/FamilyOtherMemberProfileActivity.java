package org.smartgresiter.wcaro.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.custom_view.FamilyMemberFloatingMenu;
import org.smartgresiter.wcaro.fragment.FamilyCallDialogFragment;
import org.smartgresiter.wcaro.fragment.FamilyOtherMemberProfileFragment;
import org.smartgresiter.wcaro.listener.OnClickFloatingMenu;
import org.smartgresiter.wcaro.presenter.FamilyOtherMemberActivityPresenter;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.activity.BaseFamilyOtherMemberProfileActivity;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.fragment.BaseFamilyOtherMemberProfileFragment;
import org.smartregister.family.model.BaseFamilyOtherMemberProfileActivityModel;
import org.smartregister.family.interactor.FamilyProfileInteractor;
import org.smartregister.family.model.BaseFamilyProfileModel;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

public class FamilyOtherMemberProfileActivity extends BaseFamilyOtherMemberProfileActivity {

    private OnClickFloatingMenu onClickFloatingMenu = new OnClickFloatingMenu() {
        @Override
        public void onClickMenu(int viewId) {
            switch (viewId) {
                case R.id.call_layout:
                    FamilyCallDialogFragment.launchDialog(FamilyOtherMemberProfileActivity.this, familyBaseEntityId);
                    break;
                case R.id.registration_layout:
                    startFormForEdit();
                    break;
                case R.id.remove_member_layout:

                    IndividualProfileRemoveActivity.startIndividualProfileActivity(FamilyOtherMemberProfileActivity.this,commonPersonObject,familyBaseEntityId,familyHead,primaryCaregiver);

                    break;
            }

        }
    };
    String familyBaseEntityId,familyHead,primaryCaregiver;
    String familyName;
    CommonPersonObjectClient commonPersonObject;
    @Override
    protected void initializePresenter() {
        commonPersonObject=(CommonPersonObjectClient)getIntent().getSerializableExtra(org.smartgresiter.wcaro.util.Constants.INTENT_KEY.CHILD_COMMON_PERSON);
         familyBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        String baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
         familyHead = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_HEAD);
         primaryCaregiver = getIntent().getStringExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
        String villageTown = getIntent().getStringExtra(Constants.INTENT_KEY.VILLAGE_TOWN);
        String familyName = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_NAME);
        presenter = new FamilyOtherMemberActivityPresenter(this, new BaseFamilyOtherMemberProfileActivityModel(), null, familyBaseEntityId, baseEntityId, familyHead, primaryCaregiver, villageTown, familyName);
    }

    @Override
    protected void setupViews() {
        super.setupViews();

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(String.format(getString(R.string.return_to_family_name), presenter().getFamilyName()));

        // add floating menu
        FamilyMemberFloatingMenu familyFloatingMenu = new FamilyMemberFloatingMenu(this);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        familyFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        addContentView(familyFloatingMenu, linearLayoutParams);
        familyFloatingMenu.setClickListener(onClickFloatingMenu);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem addMember = menu.findItem(R.id.add_member);
        if (addMember != null) {
            addMember.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public FamilyOtherMemberActivityPresenter presenter() {
        return (FamilyOtherMemberActivityPresenter) presenter;
    }
    public void startFormForEdit() {

        CommonRepository commonRepository = org.smartgresiter.wcaro.util.Utils.context().commonrepository(org.smartgresiter.wcaro.util.Utils.metadata().familyMemberRegister.tableName);

        final CommonPersonObject personObject = commonRepository.findByBaseEntityId(commonPersonObject.getCaseId());
        final CommonPersonObjectClient client =
                new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
        client.setColumnmaps(personObject.getColumnmaps());

        JSONObject form = org.smartgresiter.wcaro.util.JsonFormUtils.getAutoPopulatedJsonEditMemberFormString(org.smartgresiter.wcaro.util.Constants.JSON_FORM.FAMILY_MEMBER_REGISTER,
                this, client, org.smartgresiter.wcaro.util.Utils.metadata().familyMemberRegister.registerEventType,commonPersonObject.getCaseId().equalsIgnoreCase(primaryCaregiver));
        try {
            startFormActivity(form);
        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
        }
    }
    public void startFormActivity(JSONObject jsonForm) {

        Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());


        Form form = new Form();
        form.setActionBarBackground(R.color.family_actionbar);
        form.setWizard(false);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);


        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }
    public void updateFamilyMember(String jsonString) {

        try {
            showProgressDialog(org.smartregister.family.R.string.saving_dialog_title);

            FamilyEventClient familyEventClient = new BaseFamilyProfileModel(familyName).processMemberRegistration(jsonString, familyBaseEntityId);
            if (familyEventClient == null) {
                return;
            }

            new FamilyProfileInteractor().saveRegistration(familyEventClient, jsonString, true, new FamilyProfileContract.InteractorCallBack() {
                @Override
                public void startFormForEdit(CommonPersonObjectClient client) {

                }

                @Override
                public void refreshProfileTopSection(CommonPersonObjectClient client) {

                }

                @Override
                public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {

                }

                @Override
                public void onNoUniqueId() {

                }

                @Override
                public void onRegistrationSaved(boolean isEditMode) {
                    //TODO need to refresh with new value
                    Intent intent = new Intent(FamilyOtherMemberProfileActivity.this, FamilyRegisterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case org.smartgresiter.wcaro.util.Constants.ProfileActivityResults.CHANGE_COMPLETED:
                if (resultCode == Activity.RESULT_OK) {
                    //TODO need to refresh FamilyProfileActivity
//                    Intent intent = getIntent();
//                    setResult(RESULT_OK, intent);
                    Intent intent = new Intent(FamilyOtherMemberProfileActivity.this, FamilyRegisterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                break;
            case JsonFormUtils.REQUEST_CODE_GET_JSON:
                if (resultCode == RESULT_OK) {
                    try {
                        String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);

                        JSONObject form = new JSONObject(jsonString);
                        if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(org.smartgresiter.wcaro.util.Constants.EventType.FAMILY_MEMBER_REGISTRATION)) {
                            updateFamilyMember(jsonString);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;


        }
    }
}
