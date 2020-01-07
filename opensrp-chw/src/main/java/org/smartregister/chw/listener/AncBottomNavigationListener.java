package org.smartregister.chw.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.smartregister.chw.activity.JobAidsActivity;
import org.smartregister.chw.core.contract.ChwBottomNavigator;

public class AncBottomNavigationListener extends FamilyBottomNavigationListener {
    private Activity context;

    public AncBottomNavigationListener(Activity context, BottomNavigationView view) {
        super(context, view);
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        ChwBottomNavigator ancRegisterActivity = (ChwBottomNavigator) this.context;

        if (item.getItemId() == org.smartregister.family.R.id.action_family) {
            ancRegisterActivity.switchToBaseFragment();
        } else if (item.getItemId() == org.smartregister.family.R.id.action_scan_qr) {
            ancRegisterActivity.startQrCodeScanner();
            return false;
        } else if (item.getItemId() == org.smartregister.family.R.id.action_register) {
            ancRegisterActivity.startFamilyRegistration();
            return false;
        } else if (item.getItemId() == org.smartregister.family.R.id.action_job_aids) {
            //view.setSelectedItemId(R.id.action_family);
            Intent intent = new Intent(context, JobAidsActivity.class);
            context.startActivity(intent);
            return false;
        }

        return true;
    }
}
