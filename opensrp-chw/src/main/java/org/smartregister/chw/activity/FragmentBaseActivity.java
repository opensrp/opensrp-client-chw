package org.smartregister.chw.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import org.smartregister.chw.R;
import org.smartregister.chw.fragment.ReportsFragment;
import org.smartregister.view.activity.SecuredActivity;

import timber.log.Timber;

public class FragmentBaseActivity extends SecuredActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        if (savedInstanceState == null) {
            switchToFragment(new ReportsFragment());
        }

        onCreation();
    }

    private void switchToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, fragment)
                .commit();
    }

    @Override
    protected void onCreation() {
        Timber.v("Empty onCreation");
    }

    @Override
    protected void onResumption() {
        Timber.v("Empty onResumption");
    }
}
