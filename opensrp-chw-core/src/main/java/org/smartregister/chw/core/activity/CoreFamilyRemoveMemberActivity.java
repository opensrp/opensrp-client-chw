package org.smartregister.chw.core.activity;

import android.content.Intent;
import android.view.View;

import org.json.JSONObject;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.fragment.CoreFamilyRemoveMemberFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.view.activity.SecuredActivity;

import timber.log.Timber;

public abstract class CoreFamilyRemoveMemberActivity extends SecuredActivity implements View.OnClickListener {

    protected CoreFamilyRemoveMemberFragment removeMemberFragment;

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_family_remove_member);
        // set up views
        findViewById(R.id.close).setOnClickListener(this);
        setRemoveMemberFragment();
        startFragment();
    }

    @Override
    protected void onResumption() {
        //Overridden
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                Timber.d("JSONResult : %s", jsonString);

                JSONObject form = new JSONObject(jsonString);
                removeMemberFragment.confirmRemove(form);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    protected abstract void setRemoveMemberFragment();

    private void startFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flFrame, removeMemberFragment)
                .commit();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close) {
            finish();
        }
    }
}
