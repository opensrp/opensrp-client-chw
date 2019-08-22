package org.smartregister.chw.hf.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.activity.CoreAncMemberProfileActivity;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.hf.model.FamilyProfileModel;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.interactor.FamilyProfileInteractor;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;

import timber.log.Timber;

import static org.smartregister.util.JsonFormUtils.fields;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;
import static org.smartregister.util.Utils.getAllSharedPreferences;

public class AncMemberProfileActivity extends CoreAncMemberProfileActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int itemId = item.getItemId();
        if (itemId == org.smartregister.chw.core.R.id.action_remove_member) {
            CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);

            final CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(MEMBER_OBJECT.getBaseEntityId());
            final CommonPersonObjectClient client =
                    new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
            client.setColumnmaps(commonPersonObject.getColumnmaps());

            // IndividualProfileRemoveActivity.startIndividualProfileActivity(AncMemberProfileActivity.this, client, MEMBER_OBJECT.getFamilyBaseEntityId(), MEMBER_OBJECT.getFamilyHead(), MEMBER_OBJECT.getPrimaryCareGiver(), CoreAncRegisterActivity.class.getCanonicalName());
            return true;
        } else if (itemId == org.smartregister.chw.core.R.id.action_pregnancy_out_come) {
            // PncRegisterActivity.startAncRegistrationActivity(AncMemberProfileActivity.this, MEMBER_OBJECT.getBaseEntityId(), null, CoreConstants.JSON_FORM.getPregnancyOutcome(), AncLibrary.getInstance().getUniqueIdRepository().getNextUniqueId().getOpenmrsId(), MEMBER_OBJECT.getFamilyBaseEntityId(), MEMBER_OBJECT.getFamilyName());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // to chw
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case JsonFormUtils.REQUEST_CODE_GET_JSON:
                if (resultCode == RESULT_OK) {
                    try {
                        String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                        JSONObject form = new JSONObject(jsonString);
                        if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyMemberRegister.updateEventType)) {
                            FamilyEventClient familyEventClient =
                                    new FamilyProfileModel(MEMBER_OBJECT.getFamilyName()).processUpdateMemberRegistration(jsonString, MEMBER_OBJECT.getBaseEntityId());
                            new FamilyProfileInteractor().saveRegistration(familyEventClient, jsonString, true, ancMemberProfilePresenter());
                        } else if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(CoreConstants.EventType.UPDATE_ANC_REGISTRATION)) {
                            AllSharedPreferences allSharedPreferences = getAllSharedPreferences();
                            Event baseEvent = org.smartregister.chw.anc.util.JsonFormUtils.processJsonForm(allSharedPreferences, jsonString, Constants.TABLES.ANC_MEMBERS);
                            NCUtils.processEvent(baseEvent.getBaseEntityId(), new JSONObject(org.smartregister.chw.anc.util.JsonFormUtils.gson.toJson(baseEvent)));
                            AllCommonsRepository commonsRepository = CoreChwApplication.getInstance().getAllCommonsRepository(CoreConstants.TABLE_NAME.ANC_MEMBER);

                            JSONArray field = fields(form);
                            JSONObject phoneNumberObject = getFieldJSONObject(field, DBConstants.KEY.PHONE_NUMBER);
                            String phoneNumber = phoneNumberObject.getString(CoreJsonFormUtils.VALUE);
                            String baseEntityId = baseEvent.getBaseEntityId();
                            if (commonsRepository != null) {
                                ContentValues values = new ContentValues();
                                values.put(DBConstants.KEY.PHONE_NUMBER, phoneNumber);
                                CoreChwApplication.getInstance().getRepository().getWritableDatabase().update(CoreConstants.TABLE_NAME.ANC_MEMBER, values, DBConstants.KEY.BASE_ENTITY_ID + " = ?  ", new String[]{baseEntityId});
                            }

                        }
                    } catch (Exception e) {
                        Timber.e(e, "AncMemberProfileActivity -- > onActivityResult");
                    }
                }
                break;
            default:
                break;

        }
    }

    @Override
    public void startFormForEdit(Integer title_resource, String formName) {
      /*  try {
            JSONObject form = org.smartregister.chw.util.JsonFormUtils.getAncPncForm(title_resource, formName, MEMBER_OBJECT, this);
            startActivityForResult(org.smartregister.chw.util.JsonFormUtils.getAncPncStartFormIntent(form, this), JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            Timber.e(e);
        }*/
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
