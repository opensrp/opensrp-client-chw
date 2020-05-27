package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.core.activity.CoreFamilyPlanningMemberProfileActivity;
import org.smartregister.chw.core.activity.CoreFpUpcomingServicesActivity;
import org.smartregister.chw.core.contract.FamilyProfileExtendedContract;
import org.smartregister.chw.core.interactor.CoreFamilyPlanningProfileInteractor;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.presenter.CoreFamilyPlanningProfilePresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.FpUtil;
import org.smartregister.chw.custom_view.FamilyPlanningFloatingMenu;
import org.smartregister.chw.fp.dao.FpDao;
import org.smartregister.chw.fp.domain.FpMemberObject;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.presenter.FamilyPlanningMemberProfilePresenter;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class FamilyPlanningMemberProfileActivity extends CoreFamilyPlanningMemberProfileActivity
        implements FamilyProfileExtendedContract.PresenterCallBack {

    private List<ReferralTypeModel> referralTypeModels = new ArrayList<>();

    public static void startFpMemberProfileActivity(Activity activity, FpMemberObject memberObject) {
        Intent intent = new Intent(activity, FamilyPlanningMemberProfileActivity.class);
        intent.putExtra(FamilyPlanningConstants.FamilyPlanningMemberObject.MEMBER_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        addFpReferralTypes();
    }

    @Override
    protected void removeMember() {
        IndividualProfileRemoveActivity.startIndividualProfileActivity(FamilyPlanningMemberProfileActivity.this,
                getClientDetailsByBaseEntityID(fpMemberObject.getBaseEntityId()),
                fpMemberObject.getFamilyBaseEntityId(), fpMemberObject.getFamilyHead(),
                fpMemberObject.getPrimaryCareGiver(), FpRegisterActivity.class.getCanonicalName());
    }

    @Override
    protected void startFamilyPlanningRegistrationActivity() {
        FpRegisterActivity.startFpRegistrationActivity(this, fpMemberObject.getBaseEntityId(), fpMemberObject.getAge(), CoreConstants.JSON_FORM.getFpChangeMethodForm(fpMemberObject.getGender()), FamilyPlanningConstants.ActivityPayload.CHANGE_METHOD_PAYLOAD_TYPE);
    }

    @Override
    protected void initializePresenter() {
        showProgressBar(true);
        fpProfilePresenter = new FamilyPlanningMemberProfilePresenter(this, new CoreFamilyPlanningProfileInteractor(this), fpMemberObject);
    }

    @Override
    public void initializeCallFAB() {
        FpMemberObject memberObject = FpDao.getMember(fpMemberObject.getBaseEntityId());
        fpFloatingMenu = new FamilyPlanningFloatingMenu(this, memberObject);

        OnClickFloatingMenu onClickFloatingMenu = viewId -> {
            switch (viewId) {
                case R.id.family_planning_fab:
                    checkPhoneNumberProvided();
                    ((FamilyPlanningFloatingMenu) fpFloatingMenu).animateFAB();
                    break;
                case R.id.call_layout:
                    ((FamilyPlanningFloatingMenu) fpFloatingMenu).launchCallWidget();
                    ((FamilyPlanningFloatingMenu) fpFloatingMenu).animateFAB();
                    break;
                case R.id.refer_to_facility_layout:
                    ((FamilyPlanningMemberProfilePresenter) fpProfilePresenter).referToFacility();
                    break;
                default:
                    Timber.d("Unknown fab action");
                    break;
            }

        };

        ((FamilyPlanningFloatingMenu) fpFloatingMenu).setFloatingMenuOnClickListener(onClickFloatingMenu);
        fpFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.END);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(fpFloatingMenu, linearLayoutParams);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.record_fp_followup_visit) {
            openFollowUpVisitForm(false);
        }
    }

    private void checkPhoneNumberProvided() {
        boolean phoneNumberAvailable = (StringUtils.isNotBlank(fpMemberObject.getPhoneNumber())
                || StringUtils.isNotBlank(fpMemberObject.getFamilyHeadPhoneNumber()));

        ((FamilyPlanningFloatingMenu) fpFloatingMenu).redraw(phoneNumberAvailable);
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void verifyHasPhone() {
        // Implement
    }

    @Override
    public void notifyHasPhone(boolean b) {
        // Implement
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON) {
            try {
                String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                JSONObject form = new JSONObject(jsonString);
                if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(CoreConstants.EventType.FAMILY_PLANNING_REFERRAL)) {
                    ((CoreFamilyPlanningProfilePresenter) fpProfilePresenter).createReferralEvent(Utils.getAllSharedPreferences(), jsonString);
                    showToast(this.getString(R.string.referral_submitted));
                }
            } catch (Exception ex) {
                Timber.e(ex);
            }
        }
    }

    @Override
    public void openMedicalHistory() {
        OnMemberTypeLoadedListener onMemberTypeLoadedListener = memberType -> {

            switch (memberType.getMemberType()) {
                case CoreConstants.TABLE_NAME.ANC_MEMBER:
                    AncMedicalHistoryActivity.startMe(FamilyPlanningMemberProfileActivity.this, memberType.getMemberObject());
                    break;
                case CoreConstants.TABLE_NAME.PNC_MEMBER:
                    PncMedicalHistoryActivity.startMe(FamilyPlanningMemberProfileActivity.this, memberType.getMemberObject());
                    break;
                case CoreConstants.TABLE_NAME.CHILD:
                    ChildMedicalHistoryActivity.startMe(FamilyPlanningMemberProfileActivity.this, memberType.getMemberObject());
                    break;
                default:
                    Timber.v("Member info undefined");
                    break;
            }
        };
        executeOnLoaded(onMemberTypeLoadedListener);
    }

    @Override
    public void openFamilyPlanningRegistration() {
        FpRegisterActivity.startFpRegistrationActivity(this, fpMemberObject.getBaseEntityId(), fpMemberObject.getAge(), CoreConstants.JSON_FORM.getFpRegistrationForm(fpMemberObject.getGender()), FamilyPlanningConstants.ActivityPayload.UPDATE_REGISTRATION_PAYLOAD_TYPE);

    }

    @Override
    public void openUpcomingServices() {
        CoreFpUpcomingServicesActivity.startMe(this, FpUtil.toMember(fpMemberObject));
    }

    @Override
    public void openFamilyDueServices() {
        Intent intent = new Intent(this, FamilyProfileActivity.class);

        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, fpMemberObject.getFamilyBaseEntityId());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_HEAD, fpMemberObject.getFamilyHead());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER, fpMemberObject.getPrimaryCareGiver());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_NAME, fpMemberObject.getFamilyName());

        intent.putExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, true);
        startActivity(intent);
    }

    @Override
    public void openFollowUpVisitForm(boolean isEdit) {
        FpFollowUpVisitActivity.startMe(this, fpMemberObject, isEdit);
    }

    private void addFpReferralTypes() {
        referralTypeModels.add(new ReferralTypeModel(getString(R.string.family_planning_referral),
                org.smartregister.chw.util.Constants.JSON_FORM.getFamilyPlanningReferralForm(fpMemberObject.getGender()),CoreConstants.TASKS_FOCUS.FP_SIDE_EFFECTS));
        if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH) {
            referralTypeModels.add(new ReferralTypeModel(getString(R.string.gbv_referral),
                    org.smartregister.chw.util.Constants.JSON_FORM.getGbvReferralForm(),CoreConstants.TASKS_FOCUS.SUSPECTED_GBV));
        }

    }

    public List<ReferralTypeModel> getReferralTypeModels() {
        return referralTypeModels;
    }
}

