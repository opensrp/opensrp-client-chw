package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.core.activity.CoreFpRegisterActivity;
import org.smartregister.chw.fragment.MalariaRegisterFragment;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

import static org.smartregister.chw.core.utils.CoreConstants.JSON_FORM.getFpRegistration;

public class FpRegisterActivity extends CoreFpRegisterActivity {

    public static void startFpRegistrationActivity(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, FpRegisterActivity.class);
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        intent.putExtra(org.smartregister.chw.malaria.util.Constants.ACTIVITY_PAYLOAD.MALARIA_FORM_NAME, getFpRegistration());
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD.ACTION, org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD_TYPE.REGISTRATION);
        activity.startActivity(intent);
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
        FamilyRegisterActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new MalariaRegisterFragment();
    }

}