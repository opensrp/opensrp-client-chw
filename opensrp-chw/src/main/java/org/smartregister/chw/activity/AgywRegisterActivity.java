package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.agyw.util.Constants;
import org.smartregister.chw.core.activity.CoreAgywRegisterActivity;
import org.smartregister.chw.fragment.AgywRegisterFragment;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class AgywRegisterActivity extends CoreAgywRegisterActivity {

    public static void startRegistration(Activity activity, String baseEntityId, int age) {
        Intent intent = new Intent(activity, AgywRegisterActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.ACTION, Constants.ACTIVITY_PAYLOAD_TYPE.REGISTRATION);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.AGE, age);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.AGYW_FORM_NAME, Constants.FORMS.AGYW_REGISTRATION);

        activity.startActivity(intent);
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new AgywRegisterFragment();
    }
}
