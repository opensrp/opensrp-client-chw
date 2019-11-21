package org.smartregister.brac.hnpp.listener;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import org.smartregister.brac.hnpp.activity.DashBoardActivity;
import org.smartregister.family.R;
import org.smartregister.listener.BottomNavigationListener;
import org.smartregister.view.activity.BaseRegisterActivity;

public class HnppBottomNavigationListener extends BottomNavigationListener {
    private Activity context;

    public HnppBottomNavigationListener(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) context;

        if (item.getItemId() == R.id.action_family) {
            baseRegisterActivity.switchToBaseFragment();
        } else if (item.getItemId() == R.id.action_scan_qr) {
            baseRegisterActivity.startQrCodeScanner();
            return false;
        } else if (item.getItemId() == R.id.action_register) {
            baseRegisterActivity.startRegistration();
            return false;
        }
        else if (item.getItemId() == R.id.action_job_aids) {
            Intent intent = new Intent(baseRegisterActivity, DashBoardActivity.class);
            baseRegisterActivity.startActivity(intent);
            return false;
        }

        return true;
    }
}
