package org.smartregister.brac.hnpp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.vijay.jsonwizard.R.id;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.brac.hnpp.fragment.HNPPMemberJsonFormFragment;
import org.smartregister.family.activity.FamilyWizardFormActivity;
import org.smartregister.simprint.SimprintsConstant;
import org.smartregister.simprint.SimprintsRegistration;

import static com.vijay.jsonwizard.constants.JsonFormConstants.ACTIVITY_REQUEST_CODE.REQUEST_CODE_REGISTER;
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
        Log.v("SIMPRINT_SDK","HNPPMemberJsonFormActivity >>requestCode:"+requestCode+":resultCode:"+resultCode+":intent:"+data);
        if(resultCode == RESULT_OK && data !=null) {

            SimprintsRegistration registration = (SimprintsRegistration) data.getSerializableExtra(SimprintsConstant.INTENT_DATA);

//            switch (requestCode) {
//                case REQUEST_CODE_REGISTER:
                    if(registration!=null){
                        String uniqueId = registration.getGuid();
                        JSONObject guIdField = null;
                        try {
                            guIdField = getFieldJSONObject(getStep("step1").getJSONArray("fields"), "gu_id");
                            guIdField.put("value",uniqueId);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(this,"GUID not found",Toast.LENGTH_SHORT).show();
                    }
//                    break;
//            }
        }
     }
}
