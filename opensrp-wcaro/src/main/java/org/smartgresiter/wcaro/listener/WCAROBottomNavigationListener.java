package org.smartgresiter.wcaro.listener;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import org.smartgresiter.wcaro.activity.JobAidsActivity;
import org.smartregister.family.R;
import org.smartregister.listener.BottomNavigationListener;
import org.smartregister.view.activity.BaseRegisterActivity;

public class WCAROBottomNavigationListener extends BottomNavigationListener {
    private Activity context;
    private BottomNavigationView view;

    public WCAROBottomNavigationListener(Activity context, BottomNavigationView view) {
        super(context);
        this.context = context;
        this.view = view;
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
            view.setSelectedItemId(R.id.action_family);
            Intent intent = new Intent(context, JobAidsActivity.class);
            context.startActivity(intent);
            return false;
        }

        return true;
    }
}
