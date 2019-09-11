package org.smartregister.brac.hnpp.activity;

import android.os.Bundle;

import com.vijay.jsonwizard.R.id;

import org.smartregister.brac.hnpp.fragment.HNPPJsonFormFragment;
import org.smartregister.brac.hnpp.location.SSLocationForm;
import org.smartregister.family.activity.FamilyWizardFormActivity;

import java.util.ArrayList;

public class HNPPJsonFormActivity extends FamilyWizardFormActivity {

    public ArrayList<SSLocationForm> getSsLocationForms() {
        return ssLocationForms;
    }

    private ArrayList<SSLocationForm> ssLocationForms;

    @Override
    public void initializeFormFragment() {
        HNPPJsonFormFragment jsonWizardFormFragment = HNPPJsonFormFragment.getFormFragment("step1");
        this.getSupportFragmentManager().beginTransaction().add(id.container, jsonWizardFormFragment).commit();
    }

    public HNPPJsonFormActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ssLocationForms = (ArrayList<SSLocationForm>) getIntent().getSerializableExtra("SS_LOCATION");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializeFormFragmentCore() {
        super.initializeFormFragmentCore();
    }
}
