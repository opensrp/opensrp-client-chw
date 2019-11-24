package org.smartregister.brac.hnpp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;

import org.json.JSONObject;
import org.smartregister.brac.hnpp.BuildConfig;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.fragment.AncRegisterFragment;
import org.smartregister.brac.hnpp.fragment.HnppAncRegisterFragment;
import org.smartregister.brac.hnpp.listener.HnppBottomNavigationListener;
import org.smartregister.brac.hnpp.listener.HnppFamilyBottomNavListener;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.activity.CoreAncRegisterActivity;
import org.smartregister.chw.core.listener.CoreBottomNavigationListener;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.view.fragment.BaseRegisterFragment;


import timber.log.Timber;


public class HnppAncRegisterActivity extends CoreAncRegisterActivity {

    public static void startHnppAncRegisterActivity(Activity activity, String memberBaseEntityID, String phoneNumber, String formName,
                                                    String uniqueId, String familyBaseID, String family_name) {
        Intent intent = new Intent(activity, org.smartregister.brac.hnpp.activity.HnppAncRegisterActivity.class);
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, memberBaseEntityID);
        phone_number = phoneNumber;
        familyBaseEntityId = familyBaseID;
        form_name = formName;
        familyName = family_name;
        unique_id = uniqueId;
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD.ACTION, org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD_TYPE.REGISTRATION);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.TABLE_NAME, getFormTable());
        activity.startActivity(intent);
    }

//    @Override
//    public Class getRegisterActivity(String register) {
//        if (register.equals(ANC_REGISTRATION))
//            return HnppAncRegisterActivity.class;
//        else
//            return PncRegisterActivity.class;
//    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setMessage(getString(R.string.exit_app_message))
                .setTitle(getString(R.string.exit_app_title)).setCancelable(false)
                .setPositiveButton(R.string.yes_button_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                }).setNegativeButton(R.string.no_button_label, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).show();
    }
    public static void registerBottomNavigation(BottomNavigationHelper bottomNavigationHelper,
                                                BottomNavigationView bottomNavigationView, Activity activity) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_family_menu);
            bottomNavigationHelper.disableShiftMode(bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(new HnppBottomNavigationListener(activity));
        }

        if (!BuildConfig.SUPPORT_QR) {
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_scan_qr);
        }
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();

        if (!BuildConfig.SUPPORT_QR) {
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_scan_qr);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new HnppFamilyBottomNavListener(this, bottomNavigationView));
        org.smartregister.brac.hnpp.activity.FamilyRegisterActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);

    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new HnppAncRegisterFragment();
    }

    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, FamilyRegisterActivity.class);
        startActivity(intent);
        this.finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_GET_JSON) {
//            process the form
            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                JSONObject form = new JSONObject(jsonString);
                String baseEnityId = form.optString(Constants.JSON_FORM_EXTRA.ENTITY_TYPE);
                String encounter_type = form.optString(Constants.JSON_FORM_EXTRA.ENCOUNTER_TYPE);

                if (encounter_type.equalsIgnoreCase(CoreConstants.EventType.PNC_HOME_VISIT)) {
                    //ChwScheduleTaskExecutor.getInstance().execute(baseEnityId, CoreConstants.EventType.PNC_HOME_VISIT, new Date());
                } else if (encounter_type.equalsIgnoreCase(CoreConstants.EventType.ANC_HOME_VISIT)) {
                    //ChwScheduleTaskExecutor.getInstance().execute(baseEnityId, CoreConstants.EventType.ANC_HOME_VISIT, new Date());
                }
                SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }
}
