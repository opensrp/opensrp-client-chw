package org.smartregister.brac.hnpp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.vijay.jsonwizard.R.id;

import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.fragment.HNPPJsonFormFragment;
import org.smartregister.family.activity.FamilyWizardFormActivity;

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
