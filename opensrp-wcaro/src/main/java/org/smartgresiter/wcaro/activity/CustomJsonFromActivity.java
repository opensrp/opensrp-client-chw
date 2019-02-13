package org.smartgresiter.wcaro.activity;

import android.os.Bundle;

import org.smartgresiter.wcaro.R;
import org.smartregister.family.activity.FamilyWizardFormActivity;

/**
 * Override to change custom close message and custom view design
 */
public class CustomJsonFromActivity extends FamilyWizardFormActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setConfirmCloseMessage(getString(R.string.any_changes_you_make));
    }
}
