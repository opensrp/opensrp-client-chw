package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.agyw.util.Constants;
import org.smartregister.chw.core.activity.CoreAgywServicesFormsActivity;

public class AgywServicesFormActivity extends CoreAgywServicesFormsActivity {
    public static void startMe(Activity activity, String formName, String baseEntityId, int age) {
        Intent intent = new Intent(activity, AgywServicesFormActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.AGYW_FORM_NAME, formName);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.AGE, age);

        activity.startActivity(intent);
    }
}
