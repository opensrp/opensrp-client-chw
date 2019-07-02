package org.smartregister.chw.listener;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import org.smartregister.chw.activity.JobAidsActivity;
import org.smartregister.view.activity.BaseRegisterActivity;

public class FamilyBottomNavigationListener extends org.smartregister.family.listener.FamilyBottomNavigationListener {
    private Activity context;
    private BottomNavigationView view;

    public FamilyBottomNavigationListener(Activity context, BottomNavigationView view) {
        super(context);
        this.context = context;
        this.view = view;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) this.context;

        if (item.getItemId() == org.smartregister.family.R.id.action_register) {
            view.setSelectedItemId(org.smartregister.family.R.id.action_family);
            baseRegisterActivity.startRegistration();
            return false;
        } else if (item.getItemId() == org.smartregister.family.R.id.action_job_aids) {
            view.setSelectedItemId(org.smartregister.family.R.id.action_family);
            Intent intent = new Intent(context, JobAidsActivity.class);
            context.startActivity(intent);
            return false;
        } else {
            super.onNavigationItemSelected(item);
        }

        return true;
    }
}
