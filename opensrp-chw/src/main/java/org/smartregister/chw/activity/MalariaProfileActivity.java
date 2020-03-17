package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Rules;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.MalariaProfileContract;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.activity.CoreMalariaProfileActivity;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.custom_views.CoreMalariaFloatingMenu;
import org.smartregister.chw.core.dao.AncDao;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.core.interactor.CoreChildProfileInteractor;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.rule.MalariaFollowUpRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.core.utils.HomeVisitUtil;
import org.smartregister.chw.core.utils.MalariaVisitUtil;
import org.smartregister.chw.core.utils.VisitSummary;
import org.smartregister.chw.custom_view.MalariaFloatingMenu;
import org.smartregister.chw.interactor.MalariaProfileInteractor;
import org.smartregister.chw.malaria.dao.MalariaDao;
import org.smartregister.chw.malaria.domain.MemberObject;
import org.smartregister.chw.malaria.presenter.BaseMalariaProfilePresenter;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.presenter.FamilyOtherMemberActivityPresenter;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.Utils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.model.BaseFamilyOtherMemberProfileActivityModel;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.util.FormUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.chw.anc.AncLibrary.getInstance;
import static org.smartregister.chw.malaria.util.Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID;

public class MalariaProfileActivity extends CoreMalariaProfileActivity implements MalariaProfileContract.View {

    private static String baseEntityId;
    private List<ReferralTypeModel> referralTypeModels = new ArrayList<>();
    private static final String ANC = "anc";
    private static final String PNC = "pnc";

    private FormUtils formUtils;

    public static void startMalariaActivity(Activity activity, String baseEntityId) {
        MalariaProfileActivity.baseEntityId = baseEntityId;
        Intent intent = new Intent(activity, MalariaProfileActivity.class);
        intent.putExtra(BASE_ENTITY_ID, baseEntityId);
        activity.startActivity(intent);
    }

    public List<ReferralTypeModel> getReferralTypeModels() {
        return referralTypeModels;
    }

    public FormUtils getFormUtils() throws Exception {
        if (formUtils == null){
            formUtils = FormUtils.getInstance(ChwApplication.getInstance());
        }
        return formUtils;
    }

    @Override
    protected void initializePresenter() {
        showProgressBar(true);
        String baseEntityId = getIntent().getStringExtra(BASE_ENTITY_ID);
        memberObject = MalariaDao.getMember(baseEntityId);
        profilePresenter = new BaseMalariaProfilePresenter(this, new MalariaProfileInteractor(this), memberObject);
        fetchProfileData();
        profilePresenter.refreshProfileBottom();
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        org.smartregister.util.Utils.startAsyncTask(new UpdateVisitDueTask(), null);
        this.setOnMemberTypeLoadedListener(memberType -> {
            switch (memberType.getMemberType()) {
                case CoreConstants.TABLE_NAME.ANC_MEMBER:
                    AncMedicalHistoryActivity.startMe(MalariaProfileActivity.this, memberType.getMemberObject());
                    break;
                case CoreConstants.TABLE_NAME.PNC_MEMBER:
                    PncMedicalHistoryActivity.startMe(MalariaProfileActivity.this, memberType.getMemberObject());
                    break;
                case CoreConstants.TABLE_NAME.CHILD:
                    ChildMedicalHistoryActivity.startMe(MalariaProfileActivity.this, memberType.getMemberObject());
                    break;
                default:
                    Timber.v("Member info undefined");
                    break;
            }
        });
        if (((ChwApplication) ChwApplication.getInstance()).hasReferrals()) {
            addMalariaReferralTypes();
        }
    }

    private void addMalariaReferralTypes() {
        getReferralTypeModels().add(new ReferralTypeModel(getString(R.string.suspected_malaria),
                Constants.MALARIA_REFERRAL_FORM));
    }

    @Override
    public void referToFacility(){
        if (getReferralTypeModels().size() == 1) {
            try {
                startFormActivity(getFormUtils().getFormJson(getReferralTypeModels().get(0).getFormName()));
            } catch (Exception e) {
                Timber.e(e);
            }
        } else {
            Utils.launchClientReferralActivity(this, getReferralTypeModels(), baseEntityId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
        try {
            JSONObject form = new JSONObject(jsonString);
            if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON) {
                if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(CoreConstants.EventType.MALARIA_REFERRAL)) {
                    CoreReferralUtils.createReferralEvent(Utils.getAllSharedPreferences(), jsonString, CoreConstants.TABLE_NAME.MALARIA_REFERRAL, baseEntityId);
                    showToast(this.getString(R.string.referral_submitted));
                }
            }
        }catch (JSONException jsonFormException){
            Timber.e(jsonFormException);
        } catch (Exception e) {
            Timber.e(e);
        }
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
                IndividualProfileRemoveActivity.startIndividualProfileActivity(MalariaProfileActivity.this, getClientDetailsByBaseEntityID(memberObject.getBaseEntityId()), memberObject.getFamilyBaseEntityId(), memberObject.getFamilyHead(), memberObject.getPrimaryCareGiver(), MalariaRegisterActivity.class.getCanonicalName());
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public FamilyOtherMemberActivityPresenter presenter() {
        return new FamilyOtherMemberActivityPresenter(this, new BaseFamilyOtherMemberProfileActivityModel(), null, memberObject.getRelationalId(), memberObject.getBaseEntityId(), memberObject.getFamilyHead(), memberObject.getPrimaryCareGiver(), memberObject.getAddress(), memberObject.getLastName());
    }

    @Override
    protected void removeMember() {
        IndividualProfileRemoveActivity.startIndividualProfileActivity(this,
                getClientDetailsByBaseEntityID(memberObject.getBaseEntityId()),
                memberObject.getFamilyBaseEntityId(), memberObject.getFamilyHead(),
                memberObject.getPrimaryCareGiver(), FpRegisterActivity.class.getCanonicalName());
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
            MalariaFollowUpVisitActivity.startMalariaFollowUpActivity(this, memberObject.getBaseEntityId());
        } else if (id == org.smartregister.malaria.R.id.textview_record_anc) {
            if (view.getTag() == ANC) {
                AncHomeVisitActivity.startMe(this, memberObject.getBaseEntityId(), false);
            } else if (view.getTag() == PNC) {
                PncHomeVisitActivity.startMe(this, PNCDao.getMember(memberObject.getBaseEntityId()), false);
            }
        } else if (id == org.smartregister.malaria.R.id.textview_record_anc_not_done) {
            saveVisit(org.smartregister.chw.anc.util.Constants.EVENT_TYPE.ANC_HOME_VISIT_NOT_DONE);
            textViewRecordAncNotDone.setVisibility(View.GONE);
            textViewRecordAnc.setVisibility(View.GONE);
            visitStatus.setVisibility(View.VISIBLE);
        } else if (id == org.smartregister.malaria.R.id.textview_undo) {
            textViewRecordAnc.setVisibility(View.VISIBLE);
            textViewRecordAncNotDone.setVisibility(View.VISIBLE);
            visitStatus.setVisibility(View.GONE);
            saveVisit(org.smartregister.chw.anc.util.Constants.EVENT_TYPE.ANC_HOME_VISIT_NOT_DONE_UNDO);

        } else if (id == org.smartregister.malaria.R.id.textview_edit) {
            if (view.getTag() == "EDIT_PNC") {
                PncHomeVisitActivity.startMe(this, PNCDao.getMember(memberObject.getBaseEntityId()), true);
            } else if (view.getTag() == "EDIT_ANC") {
                AncHomeVisitActivity.startMe(this, memberObject.getBaseEntityId(), true);
            }
        }
    }

    private void saveVisit(String eventType) {
        try {
            Event event = org.smartregister.chw.anc.util.JsonFormUtils.createUntaggedEvent(memberObject.getBaseEntityId(), eventType, org.smartregister.chw.anc.util.Constants.TABLES.ANC_MEMBERS);
            Visit visit = NCUtils.eventToVisit(event, org.smartregister.chw.anc.util.JsonFormUtils.generateRandomUUIDString());
            visit.setPreProcessedJson(new Gson().toJson(event));
            getInstance().visitRepository().addVisit(visit);
        } catch (JSONException e) {
            Timber.e(e);
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
        CommonPersonObjectClient client = org.smartregister.chw.core.utils.Utils.clientForEdit(memberObject.getBaseEntityId());

        if (formName.equals(org.smartregister.chw.util.Constants.JSON_FORM.getFamilyMemberRegister())) {
            form = org.smartregister.chw.util.JsonFormUtils.getAutoPopulatedJsonEditMemberFormString(
                    (title_resource != null) ? getResources().getString(title_resource) : null,
                    org.smartregister.chw.util.Constants.JSON_FORM.getFamilyMemberRegister(),
                    this, client,
                    org.smartregister.chw.util.Utils.metadata().familyMemberRegister.updateEventType, memberObject.getLastName(), false);
        } else if (formName.equals(org.smartregister.chw.util.Constants.JSON_FORM.getAncRegistration())) {
            form = org.smartregister.chw.util.JsonFormUtils.getAutoJsonEditAncFormString(
                    memberObject.getBaseEntityId(), this, formName, org.smartregister.chw.util.Constants.EventType.UPDATE_ANC_REGISTRATION, getResources().getString(title_resource));
        }

        try {
            assert form != null;
            startFormActivity(form);
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

    private void startFormActivity(JSONObject jsonForm) {
        Intent intent = org.smartregister.chw.core.utils.Utils.formActivityIntent(this, jsonForm.toString());
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
    public void openUpcomingService() {
        executeOnLoaded(memberType -> MalariaUpcomingServicesActivity.startMe(MalariaProfileActivity.this, memberType.getMemberObject()));
    }

    @Override
    public void openFamilyDueServices() {
        Intent intent = new Intent(this, FamilyProfileActivity.class);
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, memberObject.getFamilyBaseEntityId());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_HEAD, memberObject.getFamilyHead());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER, memberObject.getPrimaryCareGiver());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_NAME, memberObject.getFamilyName());

        intent.putExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, true);
        startActivity(intent);
    }

    @Override
    protected Class<? extends CoreFamilyProfileActivity> getFamilyProfileActivityClass() {
        return FamilyProfileActivity.class;
    }

    @Override
    public void verifyHasPhone() {

    }

    @Override
    public void notifyHasPhone(boolean b) {

    }

    private void checkPhoneNumberProvided(boolean hasPhoneNumber) {
        ((CoreMalariaFloatingMenu) baseMalariaFloatingMenu).redraw(hasPhoneNumber);
    }

    @Override
    public void initializeFloatingMenu() {
        baseMalariaFloatingMenu = new MalariaFloatingMenu(this, memberObject);
        checkPhoneNumberProvided(StringUtils.isNotBlank(memberObject.getPhoneNumber()));
        OnClickFloatingMenu onClickFloatingMenu = viewId -> {
            switch (viewId) {
                case R.id.malaria_fab:
                    ((CoreMalariaFloatingMenu) baseMalariaFloatingMenu).animateFAB();
                    break;
                case R.id.call_layout:
                    ((CoreMalariaFloatingMenu) baseMalariaFloatingMenu).launchCallWidget();
                    ((CoreMalariaFloatingMenu) baseMalariaFloatingMenu).animateFAB();
                    break;
                case R.id.refer_to_facility_layout:
                      referToFacility();
                    ((CoreMalariaFloatingMenu) baseMalariaFloatingMenu).animateFAB();
                    break;
                default:
                    Timber.d("Unknown fab action");
                    break;
            }

        };

        ((CoreMalariaFloatingMenu) baseMalariaFloatingMenu).setFloatMenuClickListener(onClickFloatingMenu);
        baseMalariaFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.END);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(baseMalariaFloatingMenu, linearLayoutParams);
    }

    private class UpdateVisitDueTask extends AsyncTask<Void, Void, Void> {
        private MalariaFollowUpRule malariaFollowUpRule;

        @Override
        protected Void doInBackground(Void... voids) {
            Date malariaTestDate = MalariaDao.getMalariaTestDate(memberObject.getBaseEntityId());
            Date followUpDate = MalariaDao.getMalariaFollowUpVisitDate(memberObject.getBaseEntityId());
            malariaFollowUpRule = MalariaVisitUtil.getMalariaStatus(malariaTestDate, followUpDate);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            profilePresenter.recordMalariaButton(malariaFollowUpRule.getButtonStatus());
        }
    }

    public Visit getVisit(String eventType) {
        return AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), eventType);
    }

    protected boolean ancHomeVisitNotDoneEvent(Visit visit) {
        return visit != null && (new DateTime(visit.getDate()).getMonthOfYear() == new DateTime().getMonthOfYear())
                && (new DateTime(visit.getDate()).getYear() == new DateTime().getYear());
    }

    @Override
    public void recordAnc(MemberObject memberObject) {
        if (AncDao.isANCMember(memberObject.getBaseEntityId())) {
            Visit lastAncHomeVisitNotDoneEvent = getVisit(org.smartregister.chw.anc.util.Constants.EVENT_TYPE.ANC_HOME_VISIT_NOT_DONE);
            Visit lastAncHomeVisitNotDoneUndoEvent = getVisit(org.smartregister.chw.anc.util.Constants.EVENT_TYPE.ANC_HOME_VISIT_NOT_DONE_UNDO);
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

            if (lastAncHomeVisitNotDoneUndoEvent != null && lastAncHomeVisitNotDoneEvent != null
                    && lastAncHomeVisitNotDoneUndoEvent.getDate().before(lastAncHomeVisitNotDoneEvent.getDate())
                    && ancHomeVisitNotDoneEvent(lastAncHomeVisitNotDoneEvent)) {
                textViewRecordAncNotDone.setVisibility(View.GONE);
                textViewRecordAnc.setVisibility(View.GONE);
                visitStatus.setVisibility(View.VISIBLE);
            } else if (lastAncHomeVisitNotDoneUndoEvent == null && ancHomeVisitNotDoneEvent(lastAncHomeVisitNotDoneEvent)) {
                textViewRecordAncNotDone.setVisibility(View.GONE);
                textViewRecordAnc.setVisibility(View.GONE);
                visitStatus.setVisibility(View.VISIBLE);
            }

            //when visit is done
            Visit visit = getVisit(CoreConstants.EventType.ANC_HOME_VISIT);
            if (visit != null) {
                textViewRecordAncNotDone.setVisibility(View.GONE);
                textViewRecordAnc.setVisibility(View.GONE);

                visitDone.setVisibility(View.VISIBLE);
                textViewVisitDone.setTextColor(getResources().getColor(R.color.alert_complete_green));
                visitStatus.setVisibility(View.GONE);

                if (VisitUtils.isVisitWithin24Hours(visit)) {
                    textViewVisitDoneEdit.setTag("EDIT_ANC");
                    textViewVisitDoneEdit.setVisibility(View.VISIBLE);
                } else {
                    textViewVisitDoneEdit.setVisibility(View.GONE);
                }

            }
        }
    }
}