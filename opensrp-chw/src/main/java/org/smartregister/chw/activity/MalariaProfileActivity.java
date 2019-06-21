package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.malaria.activity.BaseMalariaProfileActivity;
import org.smartregister.chw.malaria.domain.MemberObject;
import org.smartregister.chw.malaria.util.Constants;

public class MalariaProfileActivity extends BaseMalariaProfileActivity {
    private View view;
    private ChildProfileActivityFlv flavor = new ChildProfileActivityFlv();

    public static void startMalariaActivity(Activity activity, MemberObject client) {
        Intent intent = new Intent(activity, MalariaProfileActivity.class);
        intent.putExtra(Constants.MALARIA_MEMBER_OBJECT.MEMBER_OBJECT, client);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreation() {
        super.onCreation();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == org.smartregister.malaria.R.id.toolbar_title) {
            onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.other_member_menu, menu);
        if (flavor.showMalariaConfirmationMenu()) {
            menu.findItem(R.id.action_malaria_registration).setVisible(false);
            menu.findItem(R.id.action_malaria_followup_visit).setVisible(true);
        }
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
                Toast.makeText(getApplicationContext(), "Registration", Toast.LENGTH_SHORT).show();
//                JSONObject form = jsonFo
//                AncRegisterActivity.startAncRegistrationActivity(FamilyOtherMemberProfileActivity.this, baseEntityId, PhoneNumber);
                return true;
            case R.id.action_malaria_followup_visit:
                Toast.makeText(getApplicationContext(), "Malaria Follow up", Toast.LENGTH_SHORT).show();
//                MalariaRegisterActivity.startMalariaRegistrationActivity(MalariaProfileActivity.this,
//                        ((ChildProfilePresenter) presenter()).getChildClient().getCaseId());
//                implement this logic in malariaprofileactivity
                return true;
            case R.id.action_remove_member:
                Toast.makeText(getApplicationContext(), "Remove Member", Toast.LENGTH_SHORT).show();
//                IndividualProfileRemoveActivity.startIndividualProfileActivity(FamilyOtherMemberProfileActivity.this, commonPersonObject, familyBaseEntityId, familyHead, primaryCaregiver);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void startFormActivity(JSONObject jsonForm) {

//        try {
//            JSONObject stepOne = jsonForm.getJSONObject(JsonFormUtils.STEP1);
//            JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                if (jsonObject.getString(org.smartregister.chw.util.JsonFormUtils.KEY).equalsIgnoreCase(Constants.JsonAssets.FAMILY_MEMBER.PHONE_NUMBER)) {
////                    jsonObject.put(JsonFormUtils.VALUE, phone_number);
//                }
//            }
//            Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyMemberFormActivity);
//            intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
//
//            Form form = new Form();
//            form.setActionBarBackground(R.color.family_actionbar);
//            form.setWizard(false);
//            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
//
//            startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


    }

}
