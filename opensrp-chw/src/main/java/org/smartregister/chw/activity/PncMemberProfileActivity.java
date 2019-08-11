package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.chw.interactor.FamilyProfileInteractor;
import org.smartregister.chw.interactor.PncMemberProfileInteractor;
import org.smartregister.chw.model.ChildRegisterModel;
import org.smartregister.chw.model.FamilyProfileModel;
import org.smartregister.chw.pnc.activity.BasePncMemberProfileActivity;
import org.smartregister.chw.presenter.PncMemberProfilePresenter;
import org.smartregister.chw.util.VisitSummary;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.List;

import timber.log.Timber;

import static org.smartregister.chw.anc.util.JsonFormUtils.setRequiredFieldsToFalseForPncChild;

public class PncMemberProfileActivity extends BasePncMemberProfileActivity {

    private PncMemberProfileInteractor basePncMemberProfileInteractor = new PncMemberProfileInteractor(this);

    public static void startMe(Activity activity, MemberObject memberObject, String familyHeadName, String familyHeadPhoneNumber) {
        Intent intent = new Intent(activity, PncMemberProfileActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_NAME, familyHeadName);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_PHONE, familyHeadPhoneNumber);
        activity.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pnc_member_profile_menu, menu);
        return true;
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

                    startFormForEdit(setRequiredFieldsToFalseForPncChild(childEnrollmentForm, MEMBER_OBJECT.getFamilyBaseEntityId(),
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
        VisitSummary visitSummary = basePncMemberProfileInteractor.visitSummary(clientObject());
        String visitStatus = visitSummary.getVisitStatus();

        if (ChildProfileInteractor.VisitType.OVERDUE.name().equals(visitStatus) || ChildProfileInteractor.VisitType.EXPIRY.name().equals(visitStatus)) {
            textview_record_anc_visit.setBackgroundResource(R.drawable.record_btn_selector_overdue);
        }
    }

    @Override
    protected void registerPresenter() {
        presenter = new PncMemberProfilePresenter(this, new PncMemberProfileInteractor(this), MEMBER_OBJECT);
    }

    @Override
    public void openMedicalHistory() {
        PncMedicalHistoryActivity.startMe(this, MEMBER_OBJECT);
    }

    @Override
    public void openUpcomingService() {
        PncUpcomingServicesActivity.startMe(this, MEMBER_OBJECT);
    }


    public void startFormForEdit(JSONObject form) {
        try {
            startActivityForResult(org.smartregister.chw.util.JsonFormUtils.getAncPncStartFormIntent(form, this), JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            Timber.e(e);
        }
    }


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
                if (resultCode == RESULT_OK) try {
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
                break;
            default:
                break;

        }
    }

    public PncMemberProfilePresenter pncMemberProfilePresenter() {
        return new PncMemberProfilePresenter(this, new PncMemberProfileInteractor(this), MEMBER_OBJECT);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()) {
            case R.id.textview_record_visit:
                PncHomeVisitActivity.startMe(this, MEMBER_OBJECT, false);
                break;
            default:
                break;
        }
    }

}
