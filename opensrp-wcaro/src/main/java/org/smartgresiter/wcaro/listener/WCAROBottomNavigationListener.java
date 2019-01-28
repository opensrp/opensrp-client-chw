package org.smartgresiter.wcaro.listener;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import org.smartgresiter.wcaro.activity.JobAidsActivity;
import org.smartregister.family.R;
import org.smartregister.listener.BottomNavigationListener;
import org.smartregister.view.activity.BaseRegisterActivity;

public class WCAROBottomNavigationListener extends BottomNavigationListener {
    private Activity context;

    public WCAROBottomNavigationListener(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        super.onNavigationItemSelected(item);

        BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) context;

        if (item.getItemId() == R.id.action_family) {
            baseRegisterActivity.switchToBaseFragment();
        } else if (item.getItemId() == R.id.action_scan_qr) {
            baseRegisterActivity.startQrCodeScanner();
        } else if (item.getItemId() == R.id.action_register) {
            baseRegisterActivity.startRegistration();
        } else if (item.getItemId() == R.id.action_job_aids) {
            Intent intent = new Intent(context, JobAidsActivity.class);
            context.startActivity(intent);
        }

        return true;
    }
}
