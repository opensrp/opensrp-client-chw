package org.smartgresiter.wcaro.activity;

import android.os.Bundle;

import com.vijay.jsonwizard.activities.JsonWizardFormActivity;

import org.smartgresiter.wcaro.R;

public class CustomJsonFromActivity extends JsonWizardFormActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setConfirmCloseMessage(getString(R.string.any_changes_you_make));
    }
}
