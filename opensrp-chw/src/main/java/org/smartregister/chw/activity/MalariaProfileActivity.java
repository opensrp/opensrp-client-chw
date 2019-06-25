package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.malaria.activity.BaseMalariaProfileActivity;
import org.smartregister.chw.malaria.domain.MemberObject;
import org.smartregister.chw.malaria.util.Constants;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Photo;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.util.FormUtils;
import org.smartregister.util.ImageUtils;
import timber.log.Timber;

import java.util.Date;

import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;

public class MalariaProfileActivity extends BaseMalariaProfileActivity {
    private final String TAG = MalariaProfileActivity.class.getCanonicalName();
    private static final String CLIENT = "client";
    private View view;
    private MemberObject memberObject;
    private ChildProfileActivityFlv flavor = new ChildProfileActivityFlv();

    public static void startMalariaActivity(Activity activity, MemberObject memberObject, CommonPersonObjectClient client) {
        Intent intent = new Intent(activity, MalariaProfileActivity.class);
        intent.putExtra(Constants.MALARIA_MEMBER_OBJECT.MEMBER_OBJECT, memberObject);
        intent.putExtra(CLIENT, client);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreation() {
        super.onCreation();

    }

    @Override
    public void onClick(View view) {
        onBackPressed();
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
                try {
                    CommonPersonObjectClient client = (CommonPersonObjectClient) getIntent().getSerializableExtra(CLIENT);
                    JSONObject jsonForm = FormUtils.getInstance(getApplicationContext()).getFormJson(org.smartregister.chw.util.Constants.JSON_FORM.FAMILY_MEMBER_REGISTER);

                    JSONObject stepOne = jsonForm.getJSONObject(JsonFormUtils.STEP1);
                    JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        processPopulatableFields(client, jsonObject, jsonArray);

                    }
                    startFormForEdit(jsonForm);
                } catch (Exception e) {
                    e.printStackTrace();
                    Timber.e(e);
                }
                return true;

            case R.id.action_malaria_followup_visit:
                Toast.makeText(getApplicationContext(), "Malaria Follow up", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_remove_member:
                Toast.makeText(getApplicationContext(), "Remove Member", Toast.LENGTH_SHORT).show();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void startFormForEdit(JSONObject jsonForm) {

        Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
        Form form = new Form();
        form.setActionBarBackground(R.color.family_actionbar);
        form.setWizard(false);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    private void processPopulatableFields(CommonPersonObjectClient client, JSONObject jsonObject, JSONArray jsonArray) throws JSONException {

        switch (jsonObject.getString(JsonFormUtils.KEY).toLowerCase()) {
            case org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN:
                jsonObject.put(JsonFormUtils.READ_ONLY, false);
                JSONObject optionsObject = jsonObject.getJSONArray(org.smartregister.family.util.Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
                optionsObject.put(JsonFormUtils.VALUE, org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN, false));

                break;
            case "age": {

                String dobString = org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
                dobString = org.smartregister.family.util.Utils.getDuration(dobString);
                dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : "0";
                jsonObject.put(JsonFormUtils.VALUE, Integer.valueOf(dobString));
            }
            break;
            case DBConstants.KEY.DOB:

                String dobString = org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
                if (StringUtils.isNotBlank(dobString)) {
                    Date dob = org.smartregister.chw.util.Utils.dobStringToDate(dobString);
                    if (dob != null) {
                        jsonObject.put(JsonFormUtils.VALUE, org.smartregister.chw.util.JsonFormUtils.dd_MM_yyyy.format(dob));
                    }
                }

                break;

            case org.smartregister.family.util.Constants.KEY.PHOTO:

                Photo photo = ImageUtils.profilePhotoByClientID(client.getCaseId(), org.smartregister.chw.util.Utils.getProfileImageResourceIDentifier());
                if (StringUtils.isNotBlank(photo.getFilePath())) {
                    jsonObject.put(JsonFormUtils.VALUE, photo.getFilePath());
                }

                break;

            case DBConstants.KEY.UNIQUE_ID:

                String uniqueId = org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false);
                jsonObject.put(JsonFormUtils.VALUE, uniqueId.replace("-", ""));

                break;

            case org.smartregister.chw.util.Constants.JsonAssets.FAM_NAME:

                final String SAME_AS_FAM_NAME = "same_as_fam_name";
                final String SURNAME = "surname";

                String familyName = org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.FAMILY_FIRST_NAME, false);
                jsonObject.put(JsonFormUtils.VALUE, familyName);

                String lastName = org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, false);

                JSONObject sameAsFamName = getFieldJSONObject(jsonArray, SAME_AS_FAM_NAME);
                JSONObject sameOptions = sameAsFamName.getJSONArray(org.smartregister.family.util.Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);

                if (familyName.equals(lastName)) {
                    sameOptions.put(JsonFormUtils.VALUE, true);
                } else {
                    sameOptions.put(JsonFormUtils.VALUE, false);
                }

                JSONObject surname = getFieldJSONObject(jsonArray, SURNAME);
                if (!familyName.equals(lastName)) {
                    surname.put(JsonFormUtils.VALUE, lastName);
                } else {
                    surname.put(JsonFormUtils.VALUE, "");
                }

                break;

            case org.smartregister.chw.util.Constants.JsonAssets.INSURANCE_PROVIDER:
                jsonObject.put(JsonFormUtils.VALUE, org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.INSURANCE_PROVIDER, false));
                break;
            case org.smartregister.chw.util.Constants.JsonAssets.INSURANCE_PROVIDER_NUMBER:
                jsonObject.put(JsonFormUtils.VALUE, org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.INSURANCE_PROVIDER_NUMBER, false));
                break;
            case org.smartregister.chw.util.Constants.JsonAssets.INSURANCE_PROVIDER_OTHER:
                jsonObject.put(JsonFormUtils.VALUE, org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.INSURANCE_PROVIDER_OTHER, false));
                break;
            case org.smartregister.chw.util.Constants.JsonAssets.DISABILITIES:
                jsonObject.put(JsonFormUtils.VALUE, org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.CHILD_PHYSICAL_CHANGE, false));
                break;
            case org.smartregister.chw.util.Constants.JsonAssets.DISABILITY_TYPE:
                jsonObject.put(JsonFormUtils.VALUE, org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.TYPE_OF_DISABILITY, false));
                break;

            case org.smartregister.chw.util.Constants.JsonAssets.BIRTH_CERT_AVAILABLE:
                jsonObject.put(JsonFormUtils.VALUE, org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.BIRTH_CERT, false));
                break;
            case org.smartregister.chw.util.Constants.JsonAssets.BIRTH_REGIST_NUMBER:
                jsonObject.put(JsonFormUtils.VALUE, org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.BIRTH_CERT_NUMBER, false));
                break;
            case org.smartregister.chw.util.Constants.JsonAssets.RHC_CARD:
                jsonObject.put(JsonFormUtils.VALUE, org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.RHC_CARD, false));
                break;
            case org.smartregister.chw.util.Constants.JsonAssets.NUTRITION_STATUS:
                jsonObject.put(JsonFormUtils.VALUE, org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.NUTRITION_STATUS, false));
                break;

            case DBConstants.KEY.GPS:

                jsonObject.put(JsonFormUtils.VALUE, org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), DBConstants.KEY.GPS, false));

                break;

            default:
                jsonObject.put(JsonFormUtils.VALUE, org.smartregister.chw.util.Utils.getValue(client.getColumnmaps(), jsonObject.getString(JsonFormUtils.KEY), false));

                //Log.e(TAG, "ERROR:: Unprocessed Form Object Key " + jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY));

                break;

        }
    }

}
