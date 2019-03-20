package org.smartregister.chw.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.fragment.FamilyProfileChangeHead;
import org.smartregister.chw.fragment.FamilyProfileChangePrimaryCG;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.view.activity.SecuredActivity;

public class FamilyProfileMenuActivity extends SecuredActivity {

    public static final String TAG = FamilyProfileMenuActivity.class.getName();
    public static final String MENU = "MENU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_profile_menu);

        Intent intent = getIntent();
        String menuOption = intent.getStringExtra(FamilyProfileMenuActivity.MENU);
        String familyBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);

        Fragment fragment;
        switch (menuOption) {
            case org.smartregister.chw.util.Constants.MenuType.ChangeHead:
                fragment = FamilyProfileChangeHead.newInstance(familyBaseEntityId);
                break;
            case org.smartregister.chw.util.Constants.MenuType.ChangePrimaryCare:
                fragment = FamilyProfileChangePrimaryCG.newInstance(familyBaseEntityId);
                break;
            default:
                fragment = FamilyProfileChangeHead.newInstance(familyBaseEntityId);
                break;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, fragment);
        ft.commit();
    }

    @Override
    protected void onCreation() {

    }

    @Override
    protected void onResumption() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                Log.d("JSONResult", jsonString);

                JSONObject form = new JSONObject(jsonString);
                if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyRegister.updateEventType)) {
                    //presenter().updateFamilyRegister(jsonString);
                } else if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyMemberRegister.registerEventType)) {
                    //presenter().saveFamilyMember(jsonString);
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
    }
}
