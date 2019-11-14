package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.jeasy.rules.api.Rules;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.FamilyOtherMemberProfileExtendedContract;
import org.smartregister.chw.core.contract.FamilyProfileExtendedContract;
import org.smartregister.chw.core.dao.AncDao;
import org.smartregister.chw.core.dao.ChildDao;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.core.interactor.CoreChildProfileInteractor;
import org.smartregister.chw.core.rule.PncVisitAlertRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.HomeVisitUtil;
import org.smartregister.chw.core.utils.VisitSummary;
import org.smartregister.chw.interactor.MalariaProfileInteractor;
import org.smartregister.chw.malaria.activity.BaseMalariaProfileActivity;
import org.smartregister.chw.malaria.domain.MemberObject;
import org.smartregister.chw.malaria.presenter.BaseMalariaProfilePresenter;
import org.smartregister.chw.malaria.util.Constants;
import org.smartregister.chw.pnc.PncLibrary;
import org.smartregister.chw.presenter.FamilyOtherMemberActivityPresenter;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.model.BaseFamilyOtherMemberProfileActivityModel;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.smartregister.chw.anc.AncLibrary.getInstance;

public class MalariaProfileActivity extends BaseMalariaProfileActivity implements FamilyOtherMemberProfileExtendedContract.View, FamilyProfileExtendedContract.PresenterCallBack {
    private static final String CLIENT = "client";
    private static final String ANC = "anc";
    private static final String PNC = "pnc";

    public static void startMalariaActivity(Activity activity, MemberObject memberObject, CommonPersonObjectClient client) {
        Intent intent = new Intent(activity, MalariaProfileActivity.class);
        intent.putExtra(Constants.MALARIA_MEMBER_OBJECT.MEMBER_OBJECT, memberObject);
        intent.putExtra(CLIENT, client);
        activity.startActivity(intent);
    }

    @Override
    protected void initializePresenter() {
        showProgressBar(true);
        profilePresenter = new BaseMalariaProfilePresenter(this, new MalariaProfileInteractor(this), MEMBER_OBJECT);
        fetchProfileData();
        profilePresenter.refreshProfileBottom();
    }

    @Override
    protected void onCreation() {
        super.onCreation();
//        isAnc(m);
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
                IndividualProfileRemoveActivity.startIndividualProfileActivity(MalariaProfileActivity.this, getClientDetailsByBaseEntityID(MEMBER_OBJECT.getBaseEntityId()), MEMBER_OBJECT.getFamilyBaseEntityId(), MEMBER_OBJECT.getFamilyHead(), MEMBER_OBJECT.getPrimaryCareGiver(), MalariaRegisterActivity.class.getCanonicalName());
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.malaria_profile_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case org.smartregister.chw.util.Constants.ProfileActivityResults.CHANGE_COMPLETED:
                if (resultCode == Activity.RESULT_OK) {
                    Intent intent = new Intent(this, FamilyProfileActivity.class);
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
                        if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyMemberRegister.updateEventType)) {
                            presenter().updateFamilyMember(jsonString);
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                break;
            default:
                break;
        }
    }

    @NonNull
    @Override
    public FamilyOtherMemberActivityPresenter presenter() {
        MemberObject memberObject = (MemberObject) getIntent().getSerializableExtra(Constants.MALARIA_MEMBER_OBJECT.MEMBER_OBJECT);
        return new FamilyOtherMemberActivityPresenter(this, new BaseFamilyOtherMemberProfileActivityModel(), null, memberObject.getRelationalId(), memberObject.getBaseEntityId(), memberObject.getFamilyHead(), memberObject.getPrimaryCareGiver(), memberObject.getAddress(), memberObject.getLastName());
    }

    @Override
    public void setProfileImage(String s, String s1) {
        //implement
    }

    @Override
    public void setProfileName(@NonNull String s) {
        TextView textView = findViewById(org.smartregister.malaria.R.id.textview_name);
        textView.setText(s);
    }

    @Override
    public void setProfileDetailOne(@NonNull String s) {
        TextView textView = findViewById(org.smartregister.malaria.R.id.textview_gender);
        textView.setText(s);
    }

    @Override
    public void setProfileDetailTwo(@NonNull String s) {
        TextView textView = findViewById(org.smartregister.malaria.R.id.textview_address);
        textView.setText(s);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.textview_record_malaria) {
            MalariaFollowUpVisitActivity.startMalariaRegistrationActivity(this, MEMBER_OBJECT.getBaseEntityId());
        } else if (id == org.smartregister.malaria.R.id.textview_record_anc) {
            if (view.getTag() == ANC) {
                AncHomeVisitActivity.startMe(this, MEMBER_OBJECT.getBaseEntityId(), false);
            } else if (view.getTag() == PNC) {
                PncHomeVisitActivity.startMe(this, PNCDao.getMember(MEMBER_OBJECT.getBaseEntityId()), false);
            }
        } else if (id == org.smartregister.malaria.R.id.textview_record_anc_not_done) {
            textViewRecordAncNotDone.setVisibility(View.GONE);
            textViewRecordAnc.setVisibility(View.GONE);
            visitStatus.setVisibility(View.VISIBLE);
        } else if (id == org.smartregister.malaria.R.id.textview_undo) {
            textViewRecordAnc.setVisibility(View.VISIBLE);
            textViewRecordAncNotDone.setVisibility(View.VISIBLE);
            visitStatus.setVisibility(View.GONE);
        } else if (id == org.smartregister.malaria.R.id.textview_edit) {
            if (view.getTag() == "EDIT_PNC") {
                PncHomeVisitActivity.startMe(this, PNCDao.getMember(MEMBER_OBJECT.getBaseEntityId()), true);
            } else if (view.getTag() == "EDIT_ANC") {
                AncHomeVisitActivity.startMe(this, MEMBER_OBJECT.getBaseEntityId(), true);
            }
        }
    }

    @Override
    public void setProfileDetailThree(String s) {
        //implement
    }

    @Override
    public void toggleFamilyHead(boolean b) {
        //implement
    }

    @Override
    public void togglePrimaryCaregiver(boolean b) {
        //implement
    }

    public void startFormForEdit(Integer title_resource, String formName) {

        JSONObject form = null;
        CommonPersonObjectClient client = org.smartregister.chw.core.utils.Utils.clientForEdit(MEMBER_OBJECT.getBaseEntityId());

        if (formName.equals(org.smartregister.chw.util.Constants.JSON_FORM.getFamilyMemberRegister())) {
            form = org.smartregister.chw.util.JsonFormUtils.getAutoPopulatedJsonEditMemberFormString(
                    (title_resource != null) ? getResources().getString(title_resource) : null,
                    org.smartregister.chw.util.Constants.JSON_FORM.getFamilyMemberRegister(),
                    this, client,
                    org.smartregister.chw.util.Utils.metadata().familyMemberRegister.updateEventType, MEMBER_OBJECT.getLastName(), false);
        } else if (formName.equals(org.smartregister.chw.util.Constants.JSON_FORM.getAncRegistration())) {
            form = org.smartregister.chw.util.JsonFormUtils.getAutoJsonEditAncFormString(
                    MEMBER_OBJECT.getBaseEntityId(), this, formName, org.smartregister.chw.util.Constants.EventType.UPDATE_ANC_REGISTRATION, getResources().getString(title_resource));
        }

        try {
            assert form != null;
            startFormActivity(form, MEMBER_OBJECT);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static CommonPersonObjectClient getClientDetailsByBaseEntityID(@NonNull String baseEntityId) {
        CommonRepository commonRepository = org.smartregister.chw.util.Utils.context().commonrepository(org.smartregister.chw.util.Utils.metadata().familyMemberRegister.tableName);

        final CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(baseEntityId);
        final CommonPersonObjectClient client =
                new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
        client.setColumnmaps(commonPersonObject.getColumnmaps());
        return client;

    }

    private void startFormActivity(JSONObject jsonForm, MemberObject memberObject) {
        Intent intent = org.smartregister.chw.core.utils.Utils.formActivityIntent(this, jsonForm.toString());
        intent.putExtra(Constants.MALARIA_MEMBER_OBJECT.MEMBER_OBJECT, memberObject);
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void refreshList() {
        //implement
    }

    @Override
    public void updateHasPhone(boolean hasPhone) {
        //implement
    }

    @Override
    public void setFamilyServiceStatus(String status) {
        //implement
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void verifyHasPhone() {
        //implement
    }

    @Override
    public void notifyHasPhone(boolean hasPhone) {
        //implement
    }

    @Override
    public void openMedicalHistory() {
        onMemberTypeLoadedListener listener = memberType -> {

            switch (memberType.memberType) {
                case CoreConstants.TABLE_NAME.ANC_MEMBER:
                    AncMedicalHistoryActivity.startMe(MalariaProfileActivity.this, memberType.memberObject);
                    break;
                case CoreConstants.TABLE_NAME.PNC_MEMBER:
                    PncMedicalHistoryActivity.startMe(MalariaProfileActivity.this, memberType.memberObject);
                    break;
                case CoreConstants.TABLE_NAME.CHILD:
                    ChildMedicalHistoryActivity.startMe(MalariaProfileActivity.this, memberType.memberObject);
                    break;
                default:
                    Timber.v("Member info undefined");
                    break;
            }
        };
        executeOnLoaded(listener);
    }

    @Override
    public void openUpcomingService() {
        executeOnLoaded(memberType -> MalariaUpcomingServicesActivity.startMe(MalariaProfileActivity.this, memberType.memberObject));
    }

    @Override
    public void openFamilyDueServices() {
        Intent intent = new Intent(this, FamilyProfileActivity.class);

        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, MEMBER_OBJECT.getFamilyBaseEntityId());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_HEAD, MEMBER_OBJECT.getFamilyHead());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER, MEMBER_OBJECT.getPrimaryCareGiver());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_NAME, MEMBER_OBJECT.getFamilyName());

        intent.putExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, true);
        startActivity(intent);
    }

    private Observable<MemberType> getMemberType() {
        return Observable.create(e -> {
            org.smartregister.chw.anc.domain.MemberObject memberObject = PNCDao.getMember(MEMBER_OBJECT.getBaseEntityId());
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

    private void executeOnLoaded(onMemberTypeLoadedListener listener) {
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

    @Override
    public void recordAnc(MemberObject memberObject) {
        if (AncDao.isANCMember(memberObject.getBaseEntityId())) {
            org.smartregister.chw.anc.domain.MemberObject ancMemberObject = AncDao.getMember(memberObject.getBaseEntityId());

            Rules rules = CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.ANC_HOME_VISIT);

            VisitSummary visitSummary = HomeVisitUtil.getAncVisitStatus(this, rules, ancMemberObject.getLastContactVisit(), null, new DateTime(ancMemberObject.getDateCreated()).toLocalDate());
            String visitSummaryStatus = visitSummary.getVisitStatus();


            if (visitSummaryStatus.equalsIgnoreCase(CoreConstants.VISIT_STATE.DUE) ||
                    visitSummaryStatus.equalsIgnoreCase(CoreChildProfileInteractor.VisitType.OVERDUE.name())) {
                textViewRecordAnc.setVisibility(View.VISIBLE);
                textViewRecordAnc.setTag(ANC);

                textViewRecordAncNotDone.setVisibility(View.VISIBLE);
            }


            if (visitSummaryStatus.equalsIgnoreCase(CoreConstants.VISIT_STATE.OVERDUE)) {
                textViewRecordAnc.setBackgroundResource(org.smartregister.chw.core.R.drawable.record_btn_selector_overdue);
            }

            if (visitSummaryStatus.equalsIgnoreCase(CoreConstants.VISIT_STATE.VISIT_NOT_DONE)) {
//                textViewRecordAnc.setVisibility(View.VISIBLE);
                textViewRecordAncNotDone.setVisibility(View.VISIBLE);
                visitStatus.setVisibility(View.GONE);

            }

            //when visit is done
            Visit visit = getVisit(CoreConstants.EventType.ANC_HOME_VISIT);
            if (visit != null) {
                textViewRecordAncNotDone.setVisibility(View.GONE);
                textViewRecordAnc.setVisibility(View.GONE);

                visitDone.setVisibility(View.VISIBLE);
                textViewVisitDoneEdit.setTag("EDIT_ANC");
                textViewVisitDone.setTextColor(getResources().getColor(R.color.alert_complete_green));
                visitStatus.setVisibility(View.GONE);

            }
        }
    }


    public Visit getVisit(String eventType) {
        return getInstance().visitRepository().getLatestVisit(MEMBER_OBJECT.getBaseEntityId(), eventType);
    }

    @Override
    public void recordPnc(MemberObject memberObject) {
        if (PNCDao.isPNCMember(memberObject.getBaseEntityId())) {
            org.smartregister.chw.anc.domain.MemberObject ancMemberObject = AncDao.getMember(memberObject.getBaseEntityId());

            Rules rules = CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.PNC_HOME_VISIT);

            PncVisitAlertRule pncVisitAlertRule = HomeVisitUtil.getPncVisitStatus(rules, getLastVisitDate(ancMemberObject.getBaseEntityId()), getDeliveryDate(ancMemberObject.getBaseEntityId()));

            //check last visit
            Visit lastVisit = getVisit(org.smartregister.chw.anc.util.Constants.EVENT_TYPE.ANC_HOME_VISIT);
            if (lastVisit != null) {
                setUpEditViews(true, VisitUtils.isVisitWithin24Hours(lastVisit), lastVisit.getDate().getTime());
            } else {

                if (pncVisitAlertRule.getButtonStatus().toUpperCase().equals("DUE") || pncVisitAlertRule.getButtonStatus().toUpperCase().equals("OVERDUE")) {
                    textViewRecordAnc.setText(R.string.record_pnc_visit);
                    textViewRecordAnc.setTag(PNC);
                    textViewRecordAnc.setVisibility(View.VISIBLE);
                }

                if (pncVisitAlertRule.getButtonStatus().toUpperCase().equals("OVERDUE")) {
                    textViewRecordAnc.setBackgroundResource(org.smartregister.chw.core.R.drawable.record_btn_selector_overdue);

                }

                if (pncVisitAlertRule.getButtonStatus().toUpperCase().equals("VISIT_DONE")) {
                    visitDone.setVisibility(View.VISIBLE);
                    textViewVisitDoneEdit.setTag("EDIT_PNC");
                    textViewVisitDone.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }
            }


        }
    }


    protected void setUpEditViews(boolean enable, boolean within24Hours, Long longDate) {
//        openVisitMonthView();
        if (enable) {
            if (within24Hours) {
                Calendar cal = Calendar.getInstance();
                int offset = cal.getTimeZone().getOffset(cal.getTimeInMillis());
                Date date = new Date(longDate - (long) offset);
                String monthString = (String) DateFormat.format("MMMM", date);
                textViewRecordAnc.setText("Visit has been done today");
            } else {
                textViewRecordAnc.setText("Visit has been done");
            }
            textViewUndo.setVisibility(View.GONE);
        }
    }


    private Date getLastVisitDate(String baseId) {
        Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(baseId, org.smartregister.chw.anc.util.Constants.EVENT_TYPE.PNC_HOME_VISIT);
        if (lastVisit != null) {
            return lastVisit.getDate();
        } else {
            return getDeliveryDate(baseId);
        }

    }

    private Date getDeliveryDate(String baseId) {
        Date deliveryDate = null;
        try {
            String deliveryDateString = PncLibrary.getInstance().profileRepository().getDeliveryDate(baseId);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            deliveryDate = sdf.parse(deliveryDateString);
        } catch (Exception e) {
            Timber.e(e);
        }
        return deliveryDate;
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
        void onMemberTypeLoaded(MemberType memberType);
    }
}