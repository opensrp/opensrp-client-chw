package org.smartregister.chw.core.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.View;

import org.json.JSONObject;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.fragment.CoreIndividualProfileRemoveFragment;
import org.smartregister.view.activity.SecuredActivity;

import timber.log.Timber;

public abstract class CoreIndividualProfileRemoveActivity extends SecuredActivity {

    protected CoreIndividualProfileRemoveFragment individualProfileRemoveFragment;

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_family_remove_member);
        findViewById(R.id.detail_toolbar).setVisibility(View.GONE);
        findViewById(R.id.close).setVisibility(View.GONE);
        findViewById(R.id.tvDetails).setVisibility(View.GONE);
        setRemoveMemberFragment();
        startFragment();
    }

    @Override
    protected void onResumption() {
        Timber.v("onResumption");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);

                JSONObject form = new JSONObject(jsonString);
                individualProfileRemoveFragment.confirmRemove(form);
            } catch (Exception e) {
                Timber.e(e);
            }
        } else {
            finish();
        }
    }

    protected abstract void setRemoveMemberFragment();

    private void startFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.flFrame, individualProfileRemoveFragment)
                .commit();
    }

    public void onRemoveMember() {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
