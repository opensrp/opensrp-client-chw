package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.rule.PncVisitAlertRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.chw.interactor.FamilyProfileInteractor;
import org.smartregister.chw.interactor.PncMemberProfileInteractor;
import org.smartregister.chw.model.ChildRegisterModel;
import org.smartregister.chw.model.FamilyProfileModel;
import org.smartregister.chw.pnc.activity.BasePncMemberProfileActivity;
import org.smartregister.chw.presenter.PncMemberProfilePresenter;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.text.MessageFormat;
import java.util.List;

import timber.log.Timber;

public class PncMemberProfileActivity extends BasePncMemberProfileActivity {
    private ImageView imageViewCross;
    private PncMemberProfileInteractor basePncMemberProfileInteractor = new PncMemberProfileInteractor(this);

    public static void startMe(Activity activity, MemberObject memberObject, String familyHeadName, String familyHeadPhoneNumber) {
        Intent intent = new Intent(activity, PncMemberProfileActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_NAME, familyHeadName);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_PHONE, familyHeadPhoneNumber);
        activity.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_pnc_member_registration:
                JSONObject form = org.smartregister.chw.util.JsonFormUtils.getAncPncForm(R.string.edit_member_form_title, org.smartregister.chw.util.Constants.JSON_FORM.getFamilyMemberRegister(), MEMBER_OBJECT, this);
                startFormForEdit(form);
                return true;

            case R.id.action_pnc_registration:
                ChildProfileInteractor childProfileInteractor = new ChildProfileInteractor();

                List<CommonPersonObjectClient> children = basePncMemberProfileInteractor.pncChildrenUnder29Days(MEMBER_OBJECT.getBaseEntityId());
                if (!children.isEmpty()) {
                    CommonPersonObjectClient client = children.get(0);
                    JSONObject childEnrollmentForm = childProfileInteractor.getAutoPopulatedJsonEditFormString(org.smartregister.chw.util.Constants.JSON_FORM.getChildRegister(), getString(R.string.edit_child_form_title), this, client);

                    startFormForEdit(org.smartregister.chw.anc.util.JsonFormUtils.setRequiredFieldsToFalseForPncChild(childEnrollmentForm, MEMBER_OBJECT.getFamilyBaseEntityId(),
                            MEMBER_OBJECT.getBaseEntityId()));
                }
                return true;

            case R.id.action__pnc_remove_member:
                IndividualProfileRemoveActivity.startIndividualProfileActivity(PncMemberProfileActivity.this, clientObject(), MEMBER_OBJECT.getFamilyBaseEntityId(), MEMBER_OBJECT.getFamilyHead(), MEMBER_OBJECT.getPrimaryCareGiver(), PncRegisterActivity.class.getCanonicalName());
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pnc_member_profile_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case org.smartregister.chw.util.Constants.ProfileActivityResults.CHANGE_COMPLETED:
                if (resultCode == Activity.RESULT_OK) {
                    Intent intent = new Intent(PncMemberProfileActivity.this, PncRegisterActivity.class);
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
                            new FamilyProfileInteractor().saveRegistration(familyEventClient, jsonString, true, pncMemberProfilePresenter());
                        }

                        if (org.smartregister.chw.util.Constants.EventType.UPDATE_CHILD_REGISTRATION.equals(form.getString(JsonFormUtils.ENCOUNTER_TYPE))) {

                            Pair<Client, Event> pair = new ChildRegisterModel().processRegistration(jsonString);
                            if (pair != null) {
                                basePncMemberProfileInteractor.updateChild(pair, jsonString, null);
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

    public PncMemberProfilePresenter pncMemberProfilePresenter() {
        return new PncMemberProfilePresenter(this, new PncMemberProfileInteractor(this), MEMBER_OBJECT);
    }

    public void startFormForEdit(JSONObject form) {
        try {
            startActivityForResult(org.smartregister.chw.util.JsonFormUtils.getAncPncStartFormIntent(form, this), JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private CommonPersonObjectClient clientObject() {
        CommonRepository commonRepository = org.smartregister.chw.util.Utils.context().commonrepository(org.smartregister.chw.util.Utils.metadata().familyMemberRegister.tableName);
        final CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(MEMBER_OBJECT.getBaseEntityId());
        final CommonPersonObjectClient client =
                new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
        client.setColumnmaps(commonPersonObject.getColumnmaps());
        return client;
    }

    @Override
    public void setupViews() {
        super.setupViews();
        imageViewCross = findViewById(R.id.cross_image);
        imageViewCross.setOnClickListener(this);

        PncVisitAlertRule summaryVisit = getVisitDetails();
        String statusVisit = summaryVisit.getButtonStatus();
        if (statusVisit.equals("OVERDUE")) {
            textview_record_visit.setVisibility(View.VISIBLE);
            textview_record_visit.setBackgroundResource(R.drawable.rounded_red_btn);
        } else if (statusVisit.equals("DUE")) {
            textview_record_visit.setVisibility(View.VISIBLE);
            textview_record_visit.setBackgroundResource(R.drawable.rounded_blue_btn);
        } else if (ChildProfileInteractor.VisitType.VISIT_DONE.name().equals(statusVisit)) {
            Visit lastVisit = getVisit(Constants.EVENT_TYPE.PNC_HOME_VISIT);
            if (lastVisit != null) {
                boolean within24Hours;
                if ((Days.daysBetween(new DateTime(lastVisit.getCreatedAt()), new DateTime()).getDays() < 1) &&
                        (Days.daysBetween(new DateTime(lastVisit.getDate()), new DateTime()).getDays() <= 1)) {
                    within24Hours = true;
                    setUpEditViews(true, within24Hours, lastVisit.getDate().getTime());
                } else {
                    textview_record_visit.setVisibility(View.VISIBLE);
                    textview_record_visit.setBackgroundResource(R.drawable.rounded_white_btn);
                    textview_record_visit.setTextColor(getResources().getColor(R.color.scan_qr_code_bg_stk_grey));
                }

            } else {
                textview_record_visit.setVisibility(View.VISIBLE);
                textview_record_visit.setBackgroundResource(R.drawable.record_btn_anc_selector);
            }
        } else {
            textview_record_visit.setBackgroundResource(R.drawable.rounded_white_btn);
            textview_record_visit.setTextColor(getResources().getColor(R.color.scan_qr_code_bg_stk_grey));
        }

    }

    private PncVisitAlertRule getVisitDetails() {
        return basePncMemberProfileInteractor.getVisitSummary(MEMBER_OBJECT.getBaseEntityId());
    }

    private void setUpEditViews(boolean enable, boolean within24Hours, Long longDate) {
        if (enable) {
            if (within24Hours) {
                String pncDay = basePncMemberProfileInteractor.getPncDay(MEMBER_OBJECT.getBaseEntityId());
                layoutNotRecordView.setVisibility(View.VISIBLE);
                tvEdit.setVisibility(View.VISIBLE);
                textViewUndo.setVisibility(View.GONE);
                textViewNotVisitMonth.setVisibility(View.VISIBLE);
                textViewNotVisitMonth.setText(MessageFormat.format(getContext().getString(R.string.pnc_visit_done), pncDay));
                imageViewCross.setImageResource(R.drawable.activityrow_visited);
                textview_record_visit.setVisibility(View.GONE);
            } else {
                layoutNotRecordView.setVisibility(View.VISIBLE);

            }
        } else {
            layoutNotRecordView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void openMedicalHistory() {
        PncMedicalHistoryActivity.startMe(this, MEMBER_OBJECT);
    }

    @Override
    protected void registerPresenter() {
        presenter = new PncMemberProfilePresenter(this, new PncMemberProfileInteractor(this), MEMBER_OBJECT);
    }

    @Override
    public void updateVisitNotDone(long value) {
        return;
    }

    @Override
    public void openVisitMonthView() {
        return;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()) {
            case R.id.textview_record_visit:
            case R.id.textview_record_reccuring_visit:
                PncHomeVisitActivity.startMe(this, MEMBER_OBJECT, false);
                break;
            case R.id.textview_edit:
                PncHomeVisitActivity.startMe(this, MEMBER_OBJECT, true);
                break;
            default:
                break;

        }
    }

    @Override
    public void openUpcomingService() {
        PncUpcomingServicesActivity.startMe(this, MEMBER_OBJECT);
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

}