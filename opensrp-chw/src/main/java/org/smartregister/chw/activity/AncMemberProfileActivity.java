package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.jeasy.rules.api.Rules;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.activity.BaseAncMemberProfileActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.presenter.BaseAncMemberProfilePresenter;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.anc.util.Util;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.interactor.AncMemberProfileInteractor;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.chw.interactor.FamilyProfileInteractor;
import org.smartregister.chw.model.FamilyProfileModel;
import org.smartregister.chw.presenter.AncMemberProfilePresenter;
import org.smartregister.chw.util.HomeVisitUtil;
import org.smartregister.chw.util.VisitSummary;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;

import timber.log.Timber;

import static org.smartregister.util.JsonFormUtils.fields;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;
import static org.smartregister.util.Utils.getAllSharedPreferences;

public class AncMemberProfileActivity extends BaseAncMemberProfileActivity {

    public static void startMe(Activity activity, MemberObject memberObject, String familyHeadName, String familyHeadPhoneNumber) {
        Intent intent = new Intent(activity, AncMemberProfileActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_NAME, familyHeadName);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_PHONE, familyHeadPhoneNumber);
        activity.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anc_member_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_anc_member_registration:
                startFormForEdit(R.string.edit_member_form_title,
                        org.smartregister.chw.util.Constants.JSON_FORM.getFamilyMemberRegister());
                return true;
            case R.id.action_anc_registration:
                startFormForEdit(R.string.edit_anc_registration_form_title,
                        org.smartregister.chw.util.Constants.JSON_FORM.getAncRegistration());
                return true;
            case R.id.action_remove_member:
                CommonRepository commonRepository = org.smartregister.chw.util.Utils.context().commonrepository(org.smartregister.chw.util.Utils.metadata().familyMemberRegister.tableName);

                final CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(MEMBER_OBJECT.getBaseEntityId());
                final CommonPersonObjectClient client =
                        new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
                client.setColumnmaps(commonPersonObject.getColumnmaps());

                IndividualProfileRemoveActivity.startIndividualProfileActivity(AncMemberProfileActivity.this, client, MEMBER_OBJECT.getFamilyBaseEntityId(), MEMBER_OBJECT.getFamilyHead(), MEMBER_OBJECT.getPrimaryCareGiver(), AncRegisterActivity.class.getCanonicalName());
                return true;
            case R.id.action_pregnancy_out_come:
                PncRegisterActivity.startAncRegistrationActivity(AncMemberProfileActivity.this, MEMBER_OBJECT.getBaseEntityId(), null, org.smartregister.chw.util.Constants.JSON_FORM.getPregnancyOutcome(), AncLibrary.getInstance().getUniqueIdRepository().getNextUniqueId().getOpenmrsId(), MEMBER_OBJECT.getFamilyBaseEntityId(), MEMBER_OBJECT.getFamilyName());
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startFormForEdit(Integer title_resource, String formName) {

        try {
            JSONObject form = org.smartregister.chw.util.JsonFormUtils.getAncPncForm(title_resource, formName, MEMBER_OBJECT, this);
            startActivityForResult(org.smartregister.chw.util.JsonFormUtils.getAncPncStartFormIntent(form, this), JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public AncMemberProfilePresenter ancMemberProfilePresenter() {
        return new AncMemberProfilePresenter(this, new AncMemberProfileInteractor(this), MEMBER_OBJECT);
    }

    @Override
    protected void registerPresenter() {
        presenter = new BaseAncMemberProfilePresenter(this, new AncMemberProfileInteractor(this), MEMBER_OBJECT);
    }

    @Override
    public void setupViews() {
        super.setupViews();
        Rules rules = ChwApplication.getInstance().getRulesEngineHelper().rules(org.smartregister.chw.util.Constants.RULE_FILE.ANC_HOME_VISIT);

        VisitSummary visitSummary = HomeVisitUtil.getAncVisitStatus(this, rules, MEMBER_OBJECT.getLastMenstrualPeriod(), MEMBER_OBJECT.getLastContactVisit(), null, new DateTime(MEMBER_OBJECT.getDateCreated()).toLocalDate());
        String visitStatus = visitSummary.getVisitStatus();

        if (!visitStatus.equalsIgnoreCase(ChildProfileInteractor.VisitType.DUE.name()) &&
                !visitStatus.equalsIgnoreCase(ChildProfileInteractor.VisitType.OVERDUE.name())) {
            textview_record_anc_visit.setVisibility(View.GONE);
            view_anc_record.setVisibility(View.GONE);
            textViewAncVisitNot.setVisibility(View.GONE);
        }
        if (visitStatus.equalsIgnoreCase(ChildProfileInteractor.VisitType.OVERDUE.name())) {
            textview_record_anc_visit.setBackgroundResource(R.drawable.record_btn_selector_overdue);
            layoutRecordView.setVisibility(View.VISIBLE);
            record_reccuringvisit_done_bar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()) {
            case R.id.textview_record_visit:
            case R.id.textview_record_reccuring_visit:
                AncHomeVisitActivity.startMe(this, MEMBER_OBJECT, false);
                break;
            case R.id.textview_edit:
                AncHomeVisitActivity.startMe(this, MEMBER_OBJECT, true);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case org.smartregister.chw.util.Constants.ProfileActivityResults.CHANGE_COMPLETED:
                if (resultCode == Activity.RESULT_OK) {
                    Intent intent = new Intent(AncMemberProfileActivity.this, AncRegisterActivity.class);
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

                            FamilyEventClient familyEventClient =
                                    new FamilyProfileModel(MEMBER_OBJECT.getFamilyName()).processUpdateMemberRegistration(jsonString, MEMBER_OBJECT.getBaseEntityId());
                            new FamilyProfileInteractor().saveRegistration(familyEventClient, jsonString, true, ancMemberProfilePresenter());
                        } else if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(org.smartregister.chw.util.Constants.EventType.UPDATE_ANC_REGISTRATION)) {
                            AllSharedPreferences allSharedPreferences = getAllSharedPreferences();
                            Event baseEvent = org.smartregister.chw.anc.util.JsonFormUtils.processJsonForm(allSharedPreferences, jsonString, Constants.TABLES.ANC_MEMBERS);
                            Util.processEvent(baseEvent.getBaseEntityId(), new JSONObject(org.smartregister.chw.anc.util.JsonFormUtils.gson.toJson(baseEvent)));
                            AllCommonsRepository commonsRepository = ChwApplication.getInstance().getAllCommonsRepository(org.smartregister.chw.util.Constants.TABLE_NAME.ANC_MEMBER);

                            JSONArray field = fields(form);
                            JSONObject phoneNumberObject = getFieldJSONObject(field, DBConstants.KEY.PHONE_NUMBER);
                            String phoneNumber = phoneNumberObject.getString(org.smartregister.chw.util.JsonFormUtils.VALUE);
                            String baseEntityId = baseEvent.getBaseEntityId();
                            if (commonsRepository != null) {
                                ContentValues values = new ContentValues();
                                values.put(DBConstants.KEY.PHONE_NUMBER, phoneNumber);
                                ChwApplication.getInstance().getRepository().getWritableDatabase().update(org.smartregister.chw.util.Constants.TABLE_NAME.ANC_MEMBER, values, DBConstants.KEY.BASE_ENTITY_ID + " = ?  ", new String[]{baseEntityId});
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;

        }
    }

    @Override
    public void openMedicalHistory() {
        AncMedicalHistoryActivity.startMe(this, MEMBER_OBJECT);
    }

    @Override
    public void openUpcomingService() {
        AncUpcomingServicesActivity.startMe(this, MEMBER_OBJECT);
    }

    @Override
    public void openFamilyDueServices() {
        Intent intent = new Intent(this, FamilyProfileActivity.class);

        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, MEMBER_OBJECT.getFamilyBaseEntityId());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_HEAD, MEMBER_OBJECT.getFamilyHead());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER, MEMBER_OBJECT.getPrimaryCareGiver());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_NAME, MEMBER_OBJECT.getFamilyName());

        intent.putExtra(org.smartregister.chw.util.Constants.INTENT_KEY.SERVICE_DUE, true);
        startActivity(intent);
    }

}
