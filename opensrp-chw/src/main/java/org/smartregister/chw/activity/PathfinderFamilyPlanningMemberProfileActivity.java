package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Rules;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.contract.PathfinderFamilyPlanningMemberProfileContract;
import org.smartregister.chw.core.contract.FamilyProfileExtendedContract;
import org.smartregister.chw.core.dao.AncDao;
import org.smartregister.chw.core.dao.ChildDao;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.core.domain.MemberType;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.FpUtil;
import org.smartregister.chw.custom_view.PathfinderFamilyPlanningFloatingMenu;
import org.smartregister.chw.fp_pathfinder.activity.BaseFpProfileActivity;
import org.smartregister.chw.fp_pathfinder.dao.FpDao;
import org.smartregister.chw.fp_pathfinder.domain.FpMemberObject;
import org.smartregister.chw.fp_pathfinder.util.FamilyPlanningConstants;
import org.smartregister.chw.interactor.PathfinderFamilyPlanningProfileInteractor;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.presenter.PathfinderFamilyPlanningMemberProfilePresenter;
import org.smartregister.chw.rules.FpAlertRule;
import org.smartregister.chw.util.PathfinderFamilyPlanningConstants;
import org.smartregister.chw.util.PathfinderFamilyPlanningUtil;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.smartregister.chw.fp_pathfinder.util.FamilyPlanningConstants.EventType.FP_FOLLOW_UP_VISIT;

public class PathfinderFamilyPlanningMemberProfileActivity extends BaseFpProfileActivity
        implements FamilyProfileExtendedContract.PresenterCallBack, PathfinderFamilyPlanningMemberProfileContract.View {

    private List<ReferralTypeModel> referralTypeModels = new ArrayList<>();

    public static void startFpMemberProfileActivity(Activity activity, FpMemberObject memberObject) {
        Intent intent = new Intent(activity, PathfinderFamilyPlanningMemberProfileActivity.class);
        intent.putExtra(FamilyPlanningConstants.FamilyPlanningMemberObject.MEMBER_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    protected static CommonPersonObjectClient getClientDetailsByBaseEntityID(@NonNull String baseEntityId) {
        CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);

        final CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(baseEntityId);
        final CommonPersonObjectClient client =
                new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
        client.setColumnmaps(commonPersonObject.getColumnmaps());
        return client;

    }

    @Override
    protected void onCreation() {
        super.onCreation();
        addFpReferralTypes();
    }

    protected void removeMember() {
        IndividualProfileRemoveActivity.startIndividualProfileActivity(PathfinderFamilyPlanningMemberProfileActivity.this,
                getClientDetailsByBaseEntityID(fpMemberObject.getBaseEntityId()),
                fpMemberObject.getFamilyBaseEntityId(), fpMemberObject.getFamilyHead(),
                fpMemberObject.getPrimaryCareGiver(), FpRegisterActivity.class.getCanonicalName());
    }

    protected void startFamilyPlanningRegistrationActivity() {
        //TODO change the form name
        FpRegisterActivity.startFpRegistrationActivity(this, fpMemberObject.getBaseEntityId(), fpMemberObject.getAge(), CoreConstants.JSON_FORM.getFpChangeMethodForm(fpMemberObject.getGender()), FamilyPlanningConstants.ActivityPayload.CHANGE_METHOD_PAYLOAD_TYPE);
    }

    @Override
    protected void initializePresenter() {
        showProgressBar(true);
        fpProfilePresenter = new PathfinderFamilyPlanningMemberProfilePresenter(this, new PathfinderFamilyPlanningProfileInteractor(this), fpMemberObject);
    }

    @Override
    public void initializeCallFAB() {
        FpMemberObject memberObject = FpDao.getMember(fpMemberObject.getBaseEntityId());
        fpFloatingMenu = new PathfinderFamilyPlanningFloatingMenu(this, memberObject);

        OnClickFloatingMenu onClickFloatingMenu = viewId -> {
            switch (viewId) {
                case R.id.family_planning_fab:
                    checkPhoneNumberProvided();
                    ((PathfinderFamilyPlanningFloatingMenu) fpFloatingMenu).animateFAB();
                    break;
                case R.id.call_layout:
                    ((PathfinderFamilyPlanningFloatingMenu) fpFloatingMenu).launchCallWidget();
                    ((PathfinderFamilyPlanningFloatingMenu) fpFloatingMenu).animateFAB();
                    break;
                case R.id.refer_to_facility_layout:
                    ((PathfinderFamilyPlanningMemberProfilePresenter) fpProfilePresenter).startFamilyPlanningReferral();
                    break;
                default:
                    Timber.d("Unknown fab action");
                    break;
            }

        };

        ((PathfinderFamilyPlanningFloatingMenu) fpFloatingMenu).setFloatingMenuOnClickListener(onClickFloatingMenu);
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

        ((PathfinderFamilyPlanningFloatingMenu) fpFloatingMenu).redraw(phoneNumberAvailable);
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
    public void openMedicalHistory() {
        OnMemberTypeLoadedListener onMemberTypeLoadedListener = memberType -> {

            switch (memberType.getMemberType()) {
                case CoreConstants.TABLE_NAME.ANC_MEMBER:
                    AncMedicalHistoryActivity.startMe(PathfinderFamilyPlanningMemberProfileActivity.this, memberType.getMemberObject());
                    break;
                case CoreConstants.TABLE_NAME.PNC_MEMBER:
                    PncMedicalHistoryActivity.startMe(PathfinderFamilyPlanningMemberProfileActivity.this, memberType.getMemberObject());
                    break;
                case CoreConstants.TABLE_NAME.CHILD:
                    ChildMedicalHistoryActivity.startMe(PathfinderFamilyPlanningMemberProfileActivity.this, memberType.getMemberObject());
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
        PathfinderFamilyPlanningRegisterActivity.startFpRegistrationActivity(this, fpMemberObject.getBaseEntityId(), fpMemberObject.getAge(), CoreConstants.JSON_FORM.getFpRegistrationForm(fpMemberObject.getGender()), FamilyPlanningConstants.ActivityPayload.UPDATE_REGISTRATION_PAYLOAD_TYPE);

    }

    @Override
    public void openFamilyPlanningIntroduction() {
        PathfinderFamilyPlanningRegisterActivity.startFpRegistrationActivity(this, fpMemberObject.getBaseEntityId(), fpMemberObject.getAge(), PathfinderFamilyPlanningConstants.JSON_FORM.getFamilyPlanningIntroduction(getApplicationContext().getResources().getConfiguration().locale, getAssets()), org.smartregister.chw.fp.util.FamilyPlanningConstants.ActivityPayload.CHANGE_METHOD_PAYLOAD_TYPE);
    }

    @Override
    public void openPregnancyScreening() {
        PathfinderFamilyPlanningRegisterActivity.startFpRegistrationActivity(this, fpMemberObject.getBaseEntityId(), fpMemberObject.getAge(), PathfinderFamilyPlanningConstants.JSON_FORM.getFamilyPlanningIntroduction(getApplicationContext().getResources().getConfiguration().locale, getAssets()), org.smartregister.chw.fp.util.FamilyPlanningConstants.ActivityPayload.CHANGE_METHOD_PAYLOAD_TYPE);
    }

    @Override
    public void openChooseFpMethod() {
        PathfinderFamilyPlanningRegisterActivity.startFpRegistrationActivity(this, fpMemberObject.getBaseEntityId(), fpMemberObject.getAge(), PathfinderFamilyPlanningConstants.JSON_FORM.getChooseFamilyPlanningMethod(getApplicationContext().getResources().getConfiguration().locale, getAssets()), org.smartregister.chw.fp.util.FamilyPlanningConstants.ActivityPayload.CHANGE_METHOD_PAYLOAD_TYPE);
    }

    @Override
    public void openGiveFpMethodButton() {
        PathfinderFamilyPlanningRegisterActivity.startFpRegistrationActivity(this, fpMemberObject.getBaseEntityId(), fpMemberObject.getAge(), PathfinderFamilyPlanningConstants.JSON_FORM.getGiveFamilyPlanningMethod(getApplicationContext().getResources().getConfiguration().locale, getAssets()), org.smartregister.chw.fp.util.FamilyPlanningConstants.ActivityPayload.CHANGE_METHOD_PAYLOAD_TYPE);
    }

    @Override
    public void openUpcomingServices() {
        PathfinderFamilyPlanningUpcomingServicesActivity.startMe(this, PathfinderFamilyPlanningUtil.toMember(fpMemberObject));
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
        PathfinderFamilyPlanningFollowUpVisitActivity.startMe(this, fpMemberObject, isEdit);
    }

    private void addFpReferralTypes() {
        //TODO change the form to pathfinder form
        referralTypeModels.add(new ReferralTypeModel(getString(R.string.family_planning_referral),
                org.smartregister.chw.util.Constants.JSON_FORM.getFamilyPlanningReferralForm(fpMemberObject.getGender())));
    }

    public List<ReferralTypeModel> getReferralTypeModels() {
        return referralTypeModels;
    }

    @Override
    public void setupViews() {
        super.setupViews();
        new PathfinderFamilyPlanningMemberProfileActivity.UpdateFollowUpVisitButtonTask(fpMemberObject).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == org.smartregister.chw.core.R.id.action_registration) {
            startFormForEdit(org.smartregister.chw.core.R.string.registration_info,
                    CoreConstants.JSON_FORM.FAMILY_MEMBER_REGISTER);
            return true;
        } else if (itemId == org.smartregister.chw.core.R.id.action_remove_member) {
            removeMember();
            return true;
        } else if (itemId == org.smartregister.chw.core.R.id.action_fp_change) {
            startFamilyPlanningRegistrationActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(org.smartregister.chw.core.R.menu.family_planning_member_profile_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CoreConstants.ProfileActivityResults.CHANGE_COMPLETED:
                if (resultCode == Activity.RESULT_OK) {
                    Intent intent = new Intent(this, PathfinderFamilyPlanningRegisterActivity.class);
                    intent.putExtras(getIntent().getExtras());
                    startActivity(intent);
                    finish();
                }
                break;
            case JsonFormUtils.REQUEST_CODE_GET_JSON:
                if (resultCode == RESULT_OK) {
                    try {
                        String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                        JSONObject form = new JSONObject(jsonString);
                        if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(CoreConstants.EventType.FAMILY_PLANNING_REFERRAL)) {
                            ((PathfinderFamilyPlanningMemberProfilePresenter) fpProfilePresenter).createReferralEvent(Utils.getAllSharedPreferences(), jsonString);
                            showToast(this.getString(R.string.referral_submitted));
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                break;
            case Constants.REQUEST_CODE_HOME_VISIT:
                refreshViewOnHomeVisitResult();
                break;
            default:
                break;
        }
    }

    protected Observable<MemberType> getMemberType() {
        return Observable.create(e -> {
            MemberObject memberObject = PNCDao.getMember(fpMemberObject.getBaseEntityId());
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

    protected void executeOnLoaded(PathfinderFamilyPlanningMemberProfileActivity.OnMemberTypeLoadedListener listener) {
        final Disposable[] disposable = new Disposable[1];
        getMemberType().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MemberType>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable[0] = d;
                    }

                    @Override
                    public void onNext(MemberType memberType) {
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

    private void refreshViewOnHomeVisitResult() {
        Observable<Visit> observable = Observable.create(visitObservableEmitter -> {
            Visit lastVisit = FpDao.getLatestVisit(fpMemberObject.getBaseEntityId(), FP_FOLLOW_UP_VISIT);
            visitObservableEmitter.onNext(lastVisit);
            visitObservableEmitter.onComplete();
        });

        final Disposable[] disposable = new Disposable[1];
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Visit>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable[0] = d;
                    }

                    @Override
                    public void onNext(Visit visit) {
                        updateLastVisitRow(visit.getDate());
                        onMemberDetailsReloaded(fpMemberObject);
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

    public void onMemberDetailsReloaded(FpMemberObject fpMemberObject) {
        super.onMemberDetailsReloaded(fpMemberObject);
    }

    public void startFormForEdit(Integer titleResource, String formName) {

        JSONObject form = null;
        CommonPersonObjectClient client = org.smartregister.chw.core.utils.Utils.clientForEdit(fpMemberObject.getBaseEntityId());

        if (formName.equals(CoreConstants.JSON_FORM.getFamilyMemberRegister())) {
            form = CoreJsonFormUtils.getAutoPopulatedJsonEditMemberFormString(
                    (titleResource != null) ? getResources().getString(titleResource) : null,
                    CoreConstants.JSON_FORM.getFamilyMemberRegister(),
                    this, client,
                    Utils.metadata().familyMemberRegister.updateEventType, fpMemberObject.getLastName(), false);
        } else if (formName.equals(CoreConstants.JSON_FORM.getAncRegistration())) {
            form = CoreJsonFormUtils.getAutoJsonEditAncFormString(
                    fpMemberObject.getBaseEntityId(), this, formName, FamilyPlanningConstants.EventType.FAMILY_PLANNING_REGISTRATION, getResources().getString(titleResource));
        }

        try {
            assert form != null;
            startFormActivity(form, fpMemberObject);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void startFormActivity(JSONObject formJson, FpMemberObject fpMemberObject) {
        Intent intent = org.smartregister.chw.core.utils.Utils.formActivityIntent(this, formJson.toString());
        intent.putExtra(FamilyPlanningConstants.FamilyPlanningMemberObject.MEMBER_OBJECT, fpMemberObject);
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    private void updateFollowUpVisitButton(String buttonStatus) {
        switch (buttonStatus) {
            case CoreConstants.VISIT_STATE.DUE:
                setFollowUpButtonDue();
                break;
            case CoreConstants.VISIT_STATE.OVERDUE:
                setFollowUpButtonOverdue();
                break;
            default:
                break;
        }
    }

    public void updateFollowUpVisitStatusRow(Visit lastVisit) {
        setupFollowupVisitEditViews(VisitUtils.isVisitWithin24Hours(lastVisit));
    }

    public interface OnMemberTypeLoadedListener {
        void onMemberTypeLoaded(MemberType memberType);
    }

    private class UpdateFollowUpVisitButtonTask extends AsyncTask<Void, Void, Void> {
        private FpMemberObject fpMemberObject;
        private FpAlertRule fpAlertRule;
        private Visit lastVisit;

        public UpdateFollowUpVisitButtonTask(FpMemberObject fpMemberObject) {
            this.fpMemberObject = fpMemberObject;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (fpMemberObject.getFpMethod().equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_INJECTABLE)) {
                lastVisit = FpDao.getLatestInjectionVisit(fpMemberObject.getBaseEntityId(), fpMemberObject.getFpMethod());
            } else {
                lastVisit = FpDao.getLatestFpVisit(fpMemberObject.getBaseEntityId(), FP_FOLLOW_UP_VISIT, fpMemberObject.getFpMethod());
            }

            if (!fpMemberObject.getFpMethod().equals("0")) {  //TODO coze update empty fp method to ""
                Date lastVisitDate;
                if (lastVisit != null) {
                    lastVisitDate = lastVisit.getDate();
                } else {
                    lastVisit = FpDao.getLatestFpVisit(fpMemberObject.getBaseEntityId(), FamilyPlanningConstants.EventType.FAMILY_PLANNING_REGISTRATION, fpMemberObject.getFpMethod());
                    lastVisitDate = lastVisit.getDate();
                }

                Rules rule = PathfinderFamilyPlanningUtil.getFpRules(fpMemberObject.getFpMethod());
                Integer pillCycles = FpDao.getLastPillCycle(fpMemberObject.getBaseEntityId(), fpMemberObject.getFpMethod());
                fpAlertRule = PathfinderFamilyPlanningUtil.getFpVisitStatus(rule, lastVisitDate, FpUtil.parseFpStartDate(fpMemberObject.getFpStartDate()), pillCycles, fpMemberObject.getFpMethod());
            } else { //Client does not have a fp Method
                lastVisit = FpDao.getLatestFpVisit(fpMemberObject.getBaseEntityId());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            if (fpAlertRule != null && (fpAlertRule.getButtonStatus().equalsIgnoreCase(CoreConstants.VISIT_STATE.OVERDUE) ||
                    fpAlertRule.getButtonStatus().equalsIgnoreCase(CoreConstants.VISIT_STATE.DUE))
            ) {
                updateFollowUpVisitButton(fpAlertRule.getButtonStatus());
            }
            if (fpAlertRule == null) {
                switch (lastVisit.getVisitType()) {
                    case FamilyPlanningConstants.EventType.FAMILY_PLANNING_REGISTRATION:
                        Timber.e("coze showing introduction to family planning");
                        showIntroductionToFpButton();
                        break;
                    case FamilyPlanningConstants.EventType.INTRODUCTION_TO_FAMILY_PLANNING:
                        Timber.e("coze showing pregnancy screening");
                        showFpPregnancyScreeningButton();
                        break;
                    case FamilyPlanningConstants.EventType.FAMILY_PLANNING_PREGNANCY_SCREENING:
                        Timber.e("coze showing choosing of fp method");
                        showChooseFpMethodButton();
                        break;
                    case FamilyPlanningConstants.EventType.CHOOSING_FAMILY_PLANNING_METHOD:
                        Timber.e("coze give fp method");
                        showGiveFpMethodButton();
                        break;
                    default:
                        break;
                }
            } else {
                updateFollowUpVisitStatusRow(lastVisit);
            }
        }
    }
}

