package org.smartgresiter.wcaro.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.ChildRegisterContract;
import org.smartgresiter.wcaro.custom_view.FamilyFloatingMenu;
import org.smartgresiter.wcaro.fragment.FamilyProfileActivityFragment;
import org.smartgresiter.wcaro.fragment.FamilyProfileDueFragment;
import org.smartgresiter.wcaro.fragment.FamilyProfileMemberFragment;
import org.smartgresiter.wcaro.interactor.ChildRegisterInteractor;
import org.smartgresiter.wcaro.model.ChildRegisterModel;
import org.smartgresiter.wcaro.model.FamilyProfileModel;
import org.smartgresiter.wcaro.presenter.FamilyProfilePresenter;
import org.smartgresiter.wcaro.util.JsonFormUtils;
import org.smartgresiter.wcaro.listener.OnClickFloatingMenu;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.family.activity.BaseFamilyProfileActivity;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileActivityFragment;
import org.smartregister.family.fragment.BaseFamilyProfileDueFragment;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.Utils;
import org.smartregister.util.FormUtils;

public class FamilyProfileActivity extends BaseFamilyProfileActivity implements ChildRegisterContract.InteractorCallBack{
    private String familyBaseEntityId;
    //add floating menu at runtime.

    @Override
    protected void setupViews() {
        super.setupViews();
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


    ChildRegisterInteractor interactor;

    public void startForm(String formName, String entityId, String metadata, String currentLocationId,String familyId) throws Exception {
        interactor = new ChildRegisterInteractor();
        if (StringUtils.isBlank(entityId)) {
            Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);
            interactor.getNextUniqueId(triple, this,familyId);
            return;
        }

        JSONObject form = getFormAsJson(formName, entityId, currentLocationId,familyId);
        startFormActivity(form);
    }

    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId,String familyID) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        return JsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId,familyID);
    }

    FormUtils formUtils = null;
    private FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(Utils.context().applicationContext());
            } catch (Exception e) {
                Log.e(ChildRegisterModel.class.getCanonicalName(), e.getMessage(), e);
            }
        }
        return formUtils;
    }

    @Override
    public void startFormActivity(JSONObject form) {
        Intent intent = new Intent(this, Utils.metadata().nativeFormActivity);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, form.toString());
        startActivityForResult(intent, org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON);
//        startRegistration();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                Log.d("JSONResult", jsonString);

                JSONObject form = new JSONObject(jsonString);
                if (form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyRegister.registerEventType)
                        || form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals("Child Registration")
                        ) {
                    saveForm(jsonString, false);
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

        }
    }


    public void saveForm(String jsonString, boolean isEditMode) {
        ChildRegisterModel model = new ChildRegisterModel();
        try {

            showProgressDialog(R.string.saving_dialog_title);

            Pair<Client, Event> pair = model.processRegistration(jsonString);
            if (pair == null) {
                return;
            }

            interactor.saveRegistration(pair, jsonString, isEditMode, this);

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
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
                    //go to child add form activity
                    try {
                        startForm("child_enrollment","","","",familyBaseEntityId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
    public void onNoUniqueId() {
        displayShortToast(R.string.no_unique_id);
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId,String familyId) {
        try {
            startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight(),familyId);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            displayToast(R.string.error_unable_to_start_form);
        }
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        hideProgressDialog();
    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        NavigationHelper.getInstance(this, null, null);
//    }

}
