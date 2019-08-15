package org.smartregister.chw.core.activity;

import android.content.Intent;
import android.os.Bundle;

import org.smartregister.chw.core.R;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.view.activity.SecuredActivity;

import timber.log.Timber;

public abstract class CoreFamilyProfileMenuActivity extends SecuredActivity {

    public static final String TAG = CoreFamilyProfileMenuActivity.class.getName();
    public static final String MENU = "MENU";
    protected String menuOption;
    protected String familyBaseEntityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_profile_menu);

        Intent intent = getIntent();
        menuOption = intent.getStringExtra(CoreFamilyProfileMenuActivity.MENU);
        familyBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
    }

    @Override
    protected void onCreation() {
        Timber.v("onCreation");
    }

    @Override
    protected void onResumption() {
        Timber.v("onResumption");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                Timber.d("JSONResult : %s", jsonString);
               /* JSONObject form = new JSONObject(jsonString);
                if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyRegister.updateEventType)) {
//                    presenter().updateFamilyRegister(jsonString);
                } else if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyMemberRegister.registerEventType)) {
                    //presenter().saveFamilyMember(jsonString);
                }*/
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }
}
