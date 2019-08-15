package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.R;
import org.smartregister.chw.core.activity.CoreAncRegisterActivity;
import org.smartregister.chw.fragment.AncRegisterFragment;
import org.smartregister.chw.util.Constants;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

import static org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD.TABLE_NAME;

public class AncRegisterActivity extends CoreAncRegisterActivity {
    private static String form_name;

    public static void startAncRegistrationActivity(Activity activity, String memberBaseEntityID, String formName,
                                                    String uniqueId, String familyBaseID, String family_name) {
        Intent intent = new Intent(activity, AncRegisterActivity.class);
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, memberBaseEntityID);
        form_name = formName;
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD.ACTION, org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD_TYPE.REGISTRATION);
        intent.putExtra(TABLE_NAME, getFormTable());
        activity.startActivity(intent);
    }

    private static String getFormTable() {
        if (form_name != null && form_name.equals(Constants.JSON_FORM.getAncRegistration())) {
            return Constants.TABLE_NAME.ANC_MEMBER;
        }
        return Constants.TABLE_NAME.ANC_PREGNANCY_OUTCOME;
    }

    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, FamilyRegisterActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new AncRegisterFragment();
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
        FamilyRegisterActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
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

    private void startRegisterActivity(Class registerClass) {
        Intent intent = new Intent(this, registerClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        this.finish();
    }
}
