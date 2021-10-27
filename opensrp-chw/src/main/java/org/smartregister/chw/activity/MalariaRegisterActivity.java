package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import org.smartregister.chw.core.activity.CoreMalariaRegisterActivity;
import org.smartregister.chw.fragment.MalariaRegisterFragment;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

import static org.smartregister.chw.core.utils.CoreConstants.JSON_FORM.getMalariaConfirmation;

public class MalariaRegisterActivity extends CoreMalariaRegisterActivity {
    private Trace myTrace = FirebasePerformance.getInstance().newTrace("malaria_register_trace");

    public static void startMalariaRegistrationActivity(Activity activity, String baseEntityID, @Nullable String familyBaseEntityID) {
        Intent intent = new Intent(activity, MalariaRegisterActivity.class);
        intent.putExtra(org.smartregister.chw.malaria.util.Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        intent.putExtra(org.smartregister.chw.malaria.util.Constants.ACTIVITY_PAYLOAD.FAMILY_BASE_ENTITY_ID, familyBaseEntityID);
        intent.putExtra(org.smartregister.chw.malaria.util.Constants.ACTIVITY_PAYLOAD.MALARIA_FORM_NAME, getMalariaConfirmation());
        intent.putExtra(org.smartregister.chw.malaria.util.Constants.ACTIVITY_PAYLOAD.ACTION, org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD_TYPE.REGISTRATION);
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

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        super.startFormActivity(formName, entityId, metaData);
        myTrace.start();
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        super.onActivityResultExtended(requestCode, resultCode, data);
        myTrace.stop();
    }
}