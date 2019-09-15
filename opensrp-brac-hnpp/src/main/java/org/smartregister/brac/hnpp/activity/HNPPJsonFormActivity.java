package org.smartregister.brac.hnpp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.vijay.jsonwizard.R.id;

import org.smartregister.brac.hnpp.fragment.HNPPJsonFormFragment;
import org.smartregister.brac.hnpp.location.SSLocationForm;
import org.smartregister.family.activity.FamilyWizardFormActivity;

import java.util.ArrayList;

public class HNPPJsonFormActivity extends FamilyWizardFormActivity {

    @Override
    public void initializeFormFragment() {
        HNPPJsonFormFragment jsonWizardFormFragment = HNPPJsonFormFragment.getFormFragment("step1");
        this.getSupportFragmentManager().beginTransaction().add(id.container, jsonWizardFormFragment).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("SIMPRINT_SDK","requestCode:"+requestCode+":resultCode:"+resultCode+":intent:"+data);
    }
}
