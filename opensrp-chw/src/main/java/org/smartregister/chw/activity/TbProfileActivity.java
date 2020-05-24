package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import org.json.JSONObject;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.core.activity.CoreTbProfileActivity;
import org.smartregister.chw.core.activity.CoreTbUpcomingServicesActivity;
import org.smartregister.chw.core.contract.FamilyProfileExtendedContract;
import org.smartregister.chw.core.interactor.CoreTbProfileInteractor;
import org.smartregister.chw.core.presenter.CoreFamilyPlanningProfilePresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.presenter.TbProfilePresenter;
import org.smartregister.chw.tb.activity.BaseTbRegistrationFormsActivity;
import org.smartregister.chw.tb.domain.TbMemberObject;
import org.smartregister.chw.tb.util.Constants;
import org.smartregister.chw.tb.util.TbUtil;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.chw.core.utils.FormUtils.getFormUtils;

public class TbProfileActivity extends CoreTbProfileActivity
        implements FamilyProfileExtendedContract.PresenterCallBack {

    private List<ReferralTypeModel> referralTypeModels = new ArrayList<>();

    public static void startTbProfileActivity(Activity activity, TbMemberObject memberObject) {
        Intent intent = new Intent(activity, TbProfileActivity.class);
        intent.putExtra(Constants.ActivityPayload.MEMBER_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        addFpReferralTypes();
    }

    @Override
    protected void removeMember() {
        IndividualProfileRemoveActivity.startIndividualProfileActivity(TbProfileActivity.this,
                getClientDetailsByBaseEntityID(getTbMemberObject().getBaseEntityId()),
                getTbMemberObject().getFamilyBaseEntityId(), getTbMemberObject().getFamilyHead(),
                getTbMemberObject().getPrimaryCareGiver(), FpRegisterActivity.class.getCanonicalName());
    }

    @Override
    protected void startTbRegistrationActivity() {
        TbRegisterActivity.startTbRegistrationActivity(this, getTbMemberObject().getBaseEntityId());
    }

    @Override
    protected void initializePresenter() {
        showProgressBar(true);
        setTbProfilePresenter(new TbProfilePresenter(this, new CoreTbProfileInteractor(this), getTbMemberObject()));
        fetchProfileData();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.record_tb_followup_visit) {
            openFollowUpVisitForm(false);
        }
    }

//    private void checkPhoneNumberProvided() {
//        boolean phoneNumberAvailable = (StringUtils.isNotBlank(getTbMemberObject().getPhoneNumber())
//                || StringUtils.isNotBlank(getTbMemberObject().getFamilyHeadPhoneNumber()));
//
////        ((FamilyPlanningFloatingMenu) fpFloatingMenu).redraw(phoneNumberAvailable);
//    }

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
                    ((CoreFamilyPlanningProfilePresenter) getTbProfilePresenter()).createReferralEvent(Utils.getAllSharedPreferences(), jsonString);
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
                    AncMedicalHistoryActivity.startMe(TbProfileActivity.this, memberType.getMemberObject());
                    break;
                case CoreConstants.TABLE_NAME.PNC_MEMBER:
                    PncMedicalHistoryActivity.startMe(TbProfileActivity.this, memberType.getMemberObject());
                    break;
                case CoreConstants.TABLE_NAME.CHILD:
                    ChildMedicalHistoryActivity.startMe(TbProfileActivity.this, memberType.getMemberObject());
                    break;
                default:
                    Timber.v("Member info undefined");
                    break;
            }
        };
        executeOnLoaded(onMemberTypeLoadedListener);
    }

    @Override
    public void openTbRegistrationForm() {
        TbRegisterActivity.startTbRegistrationActivity(this, getTbMemberObject().getBaseEntityId());

    }

    @Override
    public void openUpcomingServices() {
        CoreTbUpcomingServicesActivity.startMe(this, TbUtil.toMember(getTbMemberObject()));
    }

    @Override
    public void openFamilyDueServices() {
        Intent intent = new Intent(this, FamilyProfileActivity.class);

        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, getTbMemberObject().getFamilyBaseEntityId());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_HEAD, getTbMemberObject().getFamilyHead());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER, getTbMemberObject().getPrimaryCareGiver());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_NAME, getTbMemberObject().getFamilyName());

        intent.putExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, true);
        startActivity(intent);
    }

    @Override
    public void openFollowUpVisitForm(boolean isEdit) {
        if (!isEdit)
            startTbFollowupActivity(this, getTbMemberObject().getBaseEntityId());
    }

    private void addFpReferralTypes() {
        if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH) {
            referralTypeModels.add(new ReferralTypeModel(getString(R.string.hiv_referral),
                    CoreConstants.JSON_FORM.getHivReferralForm()));

            referralTypeModels.add(new ReferralTypeModel(getString(R.string.tb_referral),
                    CoreConstants.JSON_FORM.getTbReferralForm()));

            referralTypeModels.add(new ReferralTypeModel(getString(R.string.gbv_referral),
                    CoreConstants.JSON_FORM.getGbvReferralForm()));
        }

    }

    public List<ReferralTypeModel> getReferralTypeModels() {
        return referralTypeModels;
    }

    public void startTbFollowupActivity(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, BaseTbRegistrationFormsActivity.class);
        intent.putExtra(org.smartregister.chw.tb.util.Constants.ActivityPayload.BASE_ENTITY_ID, baseEntityID);
        intent.putExtra(org.smartregister.chw.tb.util.Constants.ActivityPayload.JSON_FORM, getFormUtils().getFormJsonFromRepositoryOrAssets(org.smartregister.chw.util.Constants.JSON_FORM.getTbFollowupVisit()).toString());
        intent.putExtra(org.smartregister.chw.tb.util.Constants.ActivityPayload.ACTION, Constants.ActivityPayloadType.FOLLOW_UP_VISIT);
        intent.putExtra(org.smartregister.chw.tb.util.Constants.ActivityPayload.USE_DEFAULT_NEAT_FORM_LAYOUT, false);

        activity.startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }
}

