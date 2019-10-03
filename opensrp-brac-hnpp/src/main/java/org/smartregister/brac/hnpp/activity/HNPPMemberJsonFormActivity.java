package org.smartregister.brac.hnpp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.vijay.jsonwizard.R.id;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.fragment.HNPPMemberJsonFormFragment;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.family.activity.FamilyWizardFormActivity;
import org.smartregister.simprint.SimPrintsConstantHelper;
import org.smartregister.simprint.SimPrintsRegistration;

import static com.vijay.jsonwizard.constants.JsonFormConstants.ACTIVITY_REQUEST_CODE.REQUEST_CODE_REGISTER;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;

public class HNPPMemberJsonFormActivity extends FamilyWizardFormActivity {
    HNPPMemberJsonFormFragment jsonWizardFormFragment;
    @Override
    public void initializeFormFragment() {
        jsonWizardFormFragment = HNPPMemberJsonFormFragment.getFormFragment("step1");
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

            SimPrintsRegistration registration = (SimPrintsRegistration) data.getSerializableExtra(SimPrintsConstantHelper.INTENT_DATA);

            switch (requestCode) {
                case REQUEST_CODE_REGISTER:
                    if(registration!=null){
                        String uniqueId = registration.getGuid();
                        jsonWizardFormFragment.updateGuid(uniqueId);
                    }
                    break;
            }
        }else if(resultCode == RESULT_CANCELED && requestCode == REQUEST_CODE_REGISTER){
            jsonWizardFormFragment.updateGuid(HnppConstants.TEST_GU_ID);
        }
     }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setMessage(R.string.form_back_confirm_dialog_message)
                .setTitle(R.string.form_back_confirm_dialog_title).setCancelable(false)
                .setPositiveButton(R.string.yes_button_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                }).setNegativeButton(R.string.no_button_label, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).show();
    }
}
