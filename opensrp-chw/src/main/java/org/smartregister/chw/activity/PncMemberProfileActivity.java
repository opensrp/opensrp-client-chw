package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.presenter.BaseAncMemberProfilePresenter;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.contract.ChildProfileContract;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.chw.interactor.FamilyProfileInteractor;
import org.smartregister.chw.interactor.PncMemberProfileInteractor;
import org.smartregister.chw.model.ChildRegisterModel;
import org.smartregister.chw.model.FamilyProfileModel;
import org.smartregister.chw.pnc.activity.BasePncMemberProfileActivity;
import org.smartregister.chw.presenter.PncMemberProfilePresenter;
import org.smartregister.chw.util.ChildService;
import org.smartregister.chw.util.ChildVisit;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.List;

import timber.log.Timber;

import static org.smartregister.chw.util.ChildDBConstants.KEY.MOTHER_ENTITY_ID;

public class PncMemberProfileActivity extends BasePncMemberProfileActivity {


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
                PncMemberProfileInteractor basePncMemberProfileInteractor = new PncMemberProfileInteractor(this);
                List<CommonPersonObjectClient> children = basePncMemberProfileInteractor.pncChildrenUnder29Days(MEMBER_OBJECT.getBaseEntityId());
                if (!children.isEmpty()) {
                    try {
                        JSONObject childEnrollmentForm = childProfileInteractor.getAutoPopulatedJsonEditFormString(org.smartregister.chw.util.Constants.JSON_FORM.getChildRegister(), getString(R.string.edit_child_form_title), this, children.get(0));
                        childEnrollmentForm.put(DBConstants.KEY.RELATIONAL_ID, MEMBER_OBJECT.getFamilyBaseEntityId());
                        childEnrollmentForm.put(MOTHER_ENTITY_ID, MEMBER_OBJECT.getBaseEntityId());
                        startFormForEdit(childEnrollmentForm);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return true;

            case R.id.action__pnc_remove_member:
                CommonRepository commonRepository = org.smartregister.chw.util.Utils.context().commonrepository(org.smartregister.chw.util.Utils.metadata().familyMemberRegister.tableName);

                final CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(MEMBER_OBJECT.getBaseEntityId());
                final CommonPersonObjectClient client =
                        new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
                client.setColumnmaps(commonPersonObject.getColumnmaps());

                IndividualProfileRemoveActivity.startIndividualProfileActivity(PncMemberProfileActivity.this, client, MEMBER_OBJECT.getFamilyBaseEntityId(), MEMBER_OBJECT.getFamilyHead(), MEMBER_OBJECT.getPrimaryCareGiver());
                return true;

            case R.id.action_pregnancy_out_come:
                AncRegisterActivity.startAncRegistrationActivity(PncMemberProfileActivity.this, MEMBER_OBJECT.getBaseEntityId(), null,
                        org.smartregister.chw.util.Constants.JSON_FORM.getPregnancyOutcome(), AncLibrary.getInstance().getUniqueIdRepository().getNextUniqueId().getOpenmrsId(), MEMBER_OBJECT.getFamilyBaseEntityId());
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO clean up library side
    @Override
    protected void setupViews() {
        super.setupViews();
        textViewAncVisitNot.setOnClickListener(null);
        recordRecurringVisit.setVisibility(View.GONE);
    }

    @Override
    protected void registerPresenter() {
        presenter = new BaseAncMemberProfilePresenter(this, new PncMemberProfileInteractor(this), MEMBER_OBJECT);
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

                            PncMemberProfileInteractor basePncMemberProfileInteractor = new PncMemberProfileInteractor(this);
                            basePncMemberProfileInteractor.updateChilda(pair, jsonString, callBack());
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
//    TODO a better way to do this?
    private ChildProfileContract.InteractorCallBack callBack() {
        return new ChildProfileContract.InteractorCallBack() {
            @Override
            public void updateChildVisit(ChildVisit childVisit) {
//                Implement
            }

            @Override
            public void updateChildService(ChildService childService) {
//                Implement
            }

            @Override
            public void updateFamilyMemberServiceDue(String serviceDueStatus) {
//                Implement
            }

            @Override
            public void startFormForEdit(String title, CommonPersonObjectClient client) {
//                Implement
            }

            @Override
            public void refreshProfileTopSection(CommonPersonObjectClient client) {
//                Implement
            }

            @Override
            public void hideProgressBar() {
//                Implement
            }

            @Override
            public void onRegistrationSaved(boolean isEditMode) {
//                Implement
            }

            @Override
            public void setFamilyID(String familyID) {

            }

            @Override
            public void setFamilyName(String familyName) {
//                Implement
            }

            @Override
            public void setFamilyHeadID(String familyHeadID) {
//                Implement
            }

            @Override
            public void setPrimaryCareGiverID(String primaryCareGiverID) {
//                Implement
            }

            @Override
            public void updateVisitNotDone() {
//                Implement
            }

            @Override
            public void undoVisitNotDone() {
//                Implement
            }

            @Override
            public void updateAfterBackGroundProcessed() {
//                Implement
            }
        };
    }
}
