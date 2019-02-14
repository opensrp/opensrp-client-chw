package org.smartgresiter.wcaro.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.smartgresiter.wcaro.R;
import org.smartregister.family.activity.FamilyWizardFormActivity;

/**
 * Override to change custom close message and custom view design
 */
public class CustomJsonFromActivity extends FamilyWizardFormActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setConfirmCloseMessage(getString(R.string.any_changes_you_make));
        setContentView(R.layout.custom_native_form_activity_json_form);
        mToolbar = findViewById(com.vijay.jsonwizard.R.id.tb_top);
        setSupportActionBar(mToolbar);
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

}
