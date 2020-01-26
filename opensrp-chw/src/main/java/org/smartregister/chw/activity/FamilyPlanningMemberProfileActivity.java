package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.activity.CoreFamilyPlanningMemberProfileActivity;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.contract.FamilyOtherMemberProfileExtendedContract;
import org.smartregister.chw.core.contract.FamilyProfileExtendedContract;
import org.smartregister.chw.core.dao.AncDao;
import org.smartregister.chw.core.dao.ChildDao;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.presenter.CoreFamilyOtherMemberActivityPresenter;
import org.smartregister.chw.core.presenter.CoreFamilyPlanningProfilePresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.custom_view.FamilyPlanningFloatingMenu;
import org.smartregister.chw.fp.dao.FpDao;
import org.smartregister.chw.fp.domain.FpMemberObject;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.chw.interactor.FamilyPlanningProfileInteractor;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.presenter.FamilyPlanningMemberProfilePresenter;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class FamilyPlanningMemberProfileActivity extends CoreFamilyPlanningMemberProfileActivity
        implements FamilyOtherMemberProfileExtendedContract.View, FamilyProfileExtendedContract.PresenterCallBack {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.family_planning_member_profile_menu, menu);
        return true;
    }

    @Override
    protected Class<? extends CoreFamilyProfileActivity> getFamilyProfileActivityClass() {
        return FamilyProfileActivity.class;
    }

    @Override
    protected void removeMember() {
        // TODO :: Implement
    }

    @Override
    protected void initializePresenter() {
        showProgressBar(true);
        fpProfilePresenter = new FamilyPlanningMemberProfilePresenter(this, new FamilyPlanningProfileInteractor(this), fpMemberObject);
        fetchProfileData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_registration:
                startFormForEdit(R.string.registration_info,
                        org.smartregister.chw.util.Constants.JSON_FORM.FAMILY_MEMBER_REGISTER);
                return true;

            case R.id.action_remove_member:
                IndividualProfileRemoveActivity.startIndividualProfileActivity(FamilyPlanningMemberProfileActivity.this,
                        getClientDetailsByBaseEntityID(fpMemberObject.getBaseEntityId()),
                        fpMemberObject.getFamilyBaseEntityId(), fpMemberObject.getFamilyHead(),
                        fpMemberObject.getPrimaryCareGiver(), FpRegisterActivity.class.getCanonicalName());
                return true;
            case R.id.action_fp_change:
                FpRegisterActivity.startFpRegistrationActivity(this, fpMemberObject.getBaseEntityId(), fpMemberObject.getAge(), CoreConstants.JSON_FORM.getFpChengeMethodForm(), FamilyPlanningConstants.ActivityPayload.CHANGE_METHOD_PAYLOAD_TYPE);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
                    ((CoreFamilyPlanningProfilePresenter) fpProfilePresenter).startFamilyPlanningReferral();
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
    public void refreshList() {
        // Implement
    }

    @Override
    public void updateHasPhone(boolean b) {
        // Implement
    }

    @Override
    public void setFamilyServiceStatus(String s) {
        // Implement
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

    @NonNull
    @Override
    public CoreFamilyOtherMemberActivityPresenter presenter() {
        return ((CoreFamilyOtherMemberActivityPresenter) presenter);
    }

    @Override
    public void setProfileImage(String s, String s1) {
        // Implement
    }

    @Override
    public void setProfileName(String s) {
        TextView textView = findViewById(org.smartregister.fp.R.id.textview_name);
        textView.setText(s);
    }

    @Override
    public void setProfileDetailOne(String s) {
        TextView textView = findViewById(org.smartregister.fp.R.id.textview_gender);
        textView.setText(s);
    }

    @Override
    public void setProfileDetailTwo(String s) {
        TextView textView = findViewById(org.smartregister.fp.R.id.textview_address);
        textView.setText(s);
    }

    @Override
    public void setProfileDetailThree(String s) {
        // Implement
    }

    @Override
    public void toggleFamilyHead(boolean b) {
        // Implement
    }

    @Override
    public void togglePrimaryCaregiver(boolean b) {
        // Implement
    }

    private static CommonPersonObjectClient getClientDetailsByBaseEntityID(@NonNull String baseEntityId) {
        CommonRepository commonRepository = org.smartregister.chw.util.Utils.context().commonrepository(org.smartregister.chw.util.Utils.metadata().familyMemberRegister.tableName);

        final CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(baseEntityId);
        final CommonPersonObjectClient client =
                new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
        client.setColumnmaps(commonPersonObject.getColumnmaps());
        return client;

    }

    public void startFormForEdit(Integer title_resource, String formName) {

        JSONObject form = null;
        CommonPersonObjectClient client = org.smartregister.chw.core.utils.Utils.clientForEdit(fpMemberObject.getBaseEntityId());

        if (formName.equals(org.smartregister.chw.util.Constants.JSON_FORM.getFamilyMemberRegister())) {
            form = org.smartregister.chw.util.JsonFormUtils.getAutoPopulatedJsonEditMemberFormString(
                    (title_resource != null) ? getResources().getString(title_resource) : null,
                    org.smartregister.chw.util.Constants.JSON_FORM.getFamilyMemberRegister(),
                    this, client,
                    org.smartregister.chw.util.Utils.metadata().familyMemberRegister.updateEventType, fpMemberObject.getLastName(), false);
        } else if (formName.equals(org.smartregister.chw.util.Constants.JSON_FORM.getAncRegistration())) {
            form = org.smartregister.chw.util.JsonFormUtils.getAutoJsonEditAncFormString(
                    fpMemberObject.getBaseEntityId(), this, formName, org.smartregister.chw.util.Constants.EventType.UPDATE_ANC_REGISTRATION, getResources().getString(title_resource));
        }

        try {
            assert form != null;
            startFormActivity(form, fpMemberObject);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void startFormActivity(JSONObject jsonForm, FpMemberObject memberObject) {
        Intent intent = org.smartregister.chw.core.utils.Utils.formActivityIntent(this, jsonForm.toString());
        intent.putExtra(FamilyPlanningConstants.FamilyPlanningMemberObject.MEMBER_OBJECT, memberObject);
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    private class MemberType {
        private org.smartregister.chw.anc.domain.MemberObject memberObject;
        private String memberType;

        private MemberType(org.smartregister.chw.anc.domain.MemberObject memberObject, String memberType) {
            this.memberObject = memberObject;
            this.memberType = memberType;
        }
    }

    interface onMemberTypeLoadedListener {
        void onMemberTypeLoaded(FamilyPlanningMemberProfileActivity.MemberType memberType);
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
        onMemberTypeLoadedListener listener = memberType -> {

            switch (memberType.memberType) {
                case CoreConstants.TABLE_NAME.ANC_MEMBER:
                    AncMedicalHistoryActivity.startMe(FamilyPlanningMemberProfileActivity.this, memberType.memberObject);
                    break;
                case CoreConstants.TABLE_NAME.PNC_MEMBER:
                    PncMedicalHistoryActivity.startMe(FamilyPlanningMemberProfileActivity.this, memberType.memberObject);
                    break;
                case CoreConstants.TABLE_NAME.CHILD:
                    ChildMedicalHistoryActivity.startMe(FamilyPlanningMemberProfileActivity.this, memberType.memberObject);
                    break;
                default:
                    Timber.v("Member info undefined");
                    break;
            }
        };
        executeOnLoaded(listener);
    }

    private Observable<MemberType> getMemberType() {
        return Observable.create(e -> {
            org.smartregister.chw.anc.domain.MemberObject memberObject = PNCDao.getMember(fpMemberObject.getBaseEntityId());
            String type = null;

            if (AncDao.isANCMember(memberObject.getBaseEntityId())) {
                type = CoreConstants.TABLE_NAME.ANC_MEMBER;
            } else if (PNCDao.isPNCMember(memberObject.getBaseEntityId())) {
                type = CoreConstants.TABLE_NAME.PNC_MEMBER;
            } else if (ChildDao.isChild(memberObject.getBaseEntityId())) {
                type = CoreConstants.TABLE_NAME.CHILD;
            }

            MemberType memberType = new MemberType(memberObject, type);
            e.onNext(memberType);
            e.onComplete();
        });
    }

    private void executeOnLoaded(FamilyPlanningMemberProfileActivity.onMemberTypeLoadedListener listener) {
        final Disposable[] disposable = new Disposable[1];
        getMemberType().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FamilyPlanningMemberProfileActivity.MemberType>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable[0] = d;
                    }

                    @Override
                    public void onNext(FamilyPlanningMemberProfileActivity.MemberType memberType) {
                        listener.onMemberTypeLoaded(memberType);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {
                        disposable[0].dispose();
                        disposable[0] = null;
                    }
                });
    }

    private static MemberObject toMember(FpMemberObject memberObject) {
        MemberObject res = new MemberObject();
        res.setBaseEntityId(memberObject.getBaseEntityId());
        res.setFirstName(memberObject.getFirstName());
        res.setLastName(memberObject.getLastName());
        res.setMiddleName(memberObject.getMiddleName());
        res.setDob(memberObject.getAge());
        return res;
    }

    @Override
    public void openFamilyPlanningRegistration() {
        FpRegisterActivity.startFpRegistrationActivity(this, fpMemberObject.getBaseEntityId(), fpMemberObject.getAge(), CoreConstants.JSON_FORM.getFpRegistrationForm(), FamilyPlanningConstants.ActivityPayload.UPDATE_REGISTRATION_PAYLOAD_TYPE);

    }

    @Override
    public void openUpcomingServices() {
        FpUpcomingServicesActivity.startMe(this, toMember(fpMemberObject));

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
                org.smartregister.chw.util.Constants.JSON_FORM.getFamilyPlanningReferralForm()));
    }

    public List<ReferralTypeModel> getReferralTypeModels() {
        return referralTypeModels;
    }
}

