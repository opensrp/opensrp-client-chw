package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.common.Helper;
import org.smartregister.chw.malaria.activity.BaseMalariaProfileActivity;
import org.smartregister.chw.malaria.domain.MemberObject;
import org.smartregister.chw.malaria.util.Constants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import timber.log.Timber;

public class MalariaProfileActivity extends BaseMalariaProfileActivity {
    private static final String CLIENT = "client";
    private MemberObject memberObject;

    public static void startMalariaActivity(Activity activity, MemberObject memberObject, CommonPersonObjectClient client) {
        Intent intent = new Intent(activity, MalariaProfileActivity.class);
        intent.putExtra(Constants.MALARIA_MEMBER_OBJECT.MEMBER_OBJECT, memberObject);
        intent.putExtra(CLIENT, client);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        memberObject = (MemberObject) getIntent().getSerializableExtra(Constants.MALARIA_MEMBER_OBJECT.MEMBER_OBJECT);
    }

    @Override
    public void onClick(View view) {
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.other_member_menu, menu);
        menu.findItem(R.id.action_malaria_registration).setVisible(false);
        menu.findItem(R.id.action_malaria_followup_visit).setVisible(true);
        menu.findItem(R.id.action_anc_registration).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_registration:
                startFormForEdit(memberObject.getBaseEntityId(), org.smartregister.chw.util.Constants.JSON_FORM.FAMILY_MEMBER_REGISTER, R.string.registration_info, memberObject.getLastName());
                return true;

            case R.id.action_malaria_followup_visit:
                Toast.makeText(getApplicationContext(), R.string.malaria_follow_up, Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_remove_member:
                IndividualProfileRemoveActivity.startIndividualProfileActivity(MalariaProfileActivity.this, Helper.getClientDetailsByBaseEntityID(memberObject.getBaseEntityId()), memberObject.getFamilyBaseEntityId(), memberObject.getFamilyHead(), memberObject.getPrimaryCareGiver());
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startFormForEdit(String baseEntityId, String formName, Integer title_resource, String familyName) {
        CommonRepository commonRepository = org.smartregister.chw.util.Utils.context().commonrepository(org.smartregister.chw.util.Utils.metadata().familyMemberRegister.tableName);

        final CommonPersonObject personObject = commonRepository.findByBaseEntityId(baseEntityId);
        final CommonPersonObjectClient client =
                new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
        client.setColumnmaps(personObject.getColumnmaps());

        JSONObject form = null;

        if (formName.equals(org.smartregister.chw.util.Constants.JSON_FORM.FAMILY_MEMBER_REGISTER)) {
            form = org.smartregister.chw.util.JsonFormUtils.getAutoPopulatedJsonEditMemberFormString(
                    (title_resource != null) ? getResources().getString(title_resource) : null,
                    org.smartregister.chw.util.Constants.JSON_FORM.FAMILY_MEMBER_REGISTER,
                    this, client, org.smartregister.chw.util.Utils.metadata().familyMemberRegister.updateEventType, familyName, false);
        } else if (formName.equals(org.smartregister.chw.util.Constants.JSON_FORM.MALARIA_CONFIRMATION)) {
            form = org.smartregister.chw.util.JsonFormUtils.getAutoJsonEditAncFormString(
                    baseEntityId, this, formName, Constants.EVENT_TYPE.UPDATE_MALARIA_CONFIRMATION, getResources().getString(title_resource));
        }

        try {
            startFormActivity(form);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void startFormActivity(JSONObject jsonForm) {

        Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);
        intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());


        Form form = new Form();
        form.setActionBarBackground(R.color.family_actionbar);
        form.setWizard(false);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);


        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }


}
