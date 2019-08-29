package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.R;
import org.smartregister.chw.core.activity.CoreAncRegisterActivity;
import org.smartregister.chw.fragment.AncRegisterFragment;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

import static org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD.TABLE_NAME;

public class AncRegisterActivity extends CoreAncRegisterActivity {

    public static void startAncRegistrationActivity(Activity activity, String memberBaseEntityID, String phoneNumber, String formName,
                                                    String uniqueId, String familyBaseID, String family_name) {
        Intent intent = new Intent(activity, AncRegisterActivity.class);
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, memberBaseEntityID);
        phone_number = phoneNumber;
        familyBaseEntityId = familyBaseID;
        form_name = formName;
        familyName = family_name;
        unique_id = uniqueId;
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD.ACTION, org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD_TYPE.REGISTRATION);
        intent.putExtra(TABLE_NAME, getFormTable());
        activity.startActivity(intent);
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        if (hasChildRegistration) {
            startRegisterActivity(PncRegisterActivity.class);
        } else {
            startRegisterActivity(AncRegisterActivity.class);
        }
        finish();
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        FamilyRegisterActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new AncRegisterFragment();
    }

    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, FamilyRegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void startRegisterActivity(Class registerClass) {
        Intent intent = new Intent(this, registerClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        this.finish();
    }
}
