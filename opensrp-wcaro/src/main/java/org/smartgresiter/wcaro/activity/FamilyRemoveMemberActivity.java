package org.smartgresiter.wcaro.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.fragment.FamilyRemoveMemberFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.view.activity.SecuredActivity;

public class FamilyRemoveMemberActivity extends SecuredActivity implements View.OnClickListener {

    public static final String TAG = FamilyRemoveMemberActivity.class.getName();
    FamilyRemoveMemberFragment removeMemberFragment;

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_family_remove_member);

        // set up views
        findViewById(R.id.close).setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        // initialize removeMemberFragment
        removeMemberFragment = FamilyRemoveMemberFragment.newInstance(bundle);
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
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
}
