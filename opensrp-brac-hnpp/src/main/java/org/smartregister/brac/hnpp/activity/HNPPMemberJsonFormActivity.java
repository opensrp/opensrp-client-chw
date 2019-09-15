package org.smartregister.brac.hnpp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.simprints.libsimprints.Constants;
import com.simprints.libsimprints.Registration;
import com.vijay.jsonwizard.R.id;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.brac.hnpp.fragment.HNPPJsonFormFragment;
import org.smartregister.brac.hnpp.fragment.HNPPMemberJsonFormFragment;
import org.smartregister.family.activity.FamilyWizardFormActivity;

import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;

public class HNPPMemberJsonFormActivity extends FamilyWizardFormActivity {

    @Override
    public void initializeFormFragment() {
        HNPPMemberJsonFormFragment jsonWizardFormFragment = HNPPMemberJsonFormFragment.getFormFragment("step1");
        this.getSupportFragmentManager().beginTransaction().add(id.container, jsonWizardFormFragment).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("SIMPRINT_SDK","HNPPMemberJsonFormActivity >>requestCode:"+requestCode+":resultCode:"+resultCode+":intent:"+data);

        Registration registration = data.getParcelableExtra(Constants.SIMPRINTS_REGISTRATION);
        String uniqueId = registration.getGuid();
        JSONObject guIdField = null;
        try {
            guIdField = getFieldJSONObject(getStep("step1").getJSONArray("fields"), "gu_id");
            guIdField.put("value",uniqueId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
     }
}
