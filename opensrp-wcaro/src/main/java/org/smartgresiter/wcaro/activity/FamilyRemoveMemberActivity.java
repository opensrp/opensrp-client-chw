package org.smartgresiter.wcaro.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.fragment.FamilyRemoveMemberFragment;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.view.activity.SecuredActivity;

public class FamilyRemoveMemberActivity extends SecuredActivity implements View.OnClickListener {

    public static final String TAG = FamilyRemoveMemberActivity.class.getName();
    FamilyRemoveMemberFragment removeMemberFragment;

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_family_remove_member);

        // set up views
        findViewById(R.id.close).setOnClickListener(this);

        // initialize removeMemberFragment
        removeMemberFragment = FamilyRemoveMemberFragment.newInstance(getIntent().getExtras());
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.flFrame, removeMemberFragment)
                .commit();
    }

    @Override
    protected void onResumption() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case JsonFormUtils.REQUEST_CODE_GET_JSON : {
                if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
                    try {
                        String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                        Log.d("JSONResult", jsonString);

                        JSONObject form = new JSONObject(jsonString);
                        removeMemberFragment.getPresenter().processRemoveForm(form);
                    } catch (Exception e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                }
            }
            break;
            case org.smartgresiter.wcaro.util.Constants.ProfileActivityResults.CHANGE_COMPLETED: {
                if (resultCode == Activity.RESULT_OK) {
                    try {

                        String careGiverID = data.getStringExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
                        String familyHeadID = data.getStringExtra(Constants.INTENT_KEY.FAMILY_HEAD);

                        if(StringUtils.isNotBlank(careGiverID)){
                            removeMemberFragment.setPrimaryCaregiver(careGiverID);
                        }
                        if(StringUtils.isNotBlank(familyHeadID)){
                            removeMemberFragment.setFamilyHead(familyHeadID);
                        }
                        removeMemberFragment.refreshMemberList(FetchStatus.fetched);
                    } catch (Exception e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }
}
