package org.smartregister.chw.activity;

import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.chw.fragment.FamilyRegisterJsonFormFragment;

public class JsonWizardFormExtendedActivity extends JsonWizardFormActivity {
    FamilyRegisterJsonFormFragment familyRegisterJsonFormFragment;

    @Override
    public void initializeFormFragment() {
        familyRegisterJsonFormFragment = FamilyRegisterJsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction()
                .add(com.vijay.jsonwizard.R.id.container, familyRegisterJsonFormFragment).commit();
    }
}
