package org.smartregister.brac.hnpp.listener;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import org.smartregister.brac.hnpp.activity.DashBoardActivity;
import org.smartregister.view.activity.BaseRegisterActivity;

public class HnppFamilyBottomNavListener extends org.smartregister.family.listener.FamilyBottomNavigationListener {
    private Activity context;
    private BottomNavigationView bottomNavigationView;

    public HnppFamilyBottomNavListener(Activity context, BottomNavigationView bottomNavigationView) {
        super(context);
        this.context = context;
        this.bottomNavigationView = bottomNavigationView;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) this.context;

        if (item.getItemId() == org.smartregister.family.R.id.action_register) {
            bottomNavigationView.setSelectedItemId(org.smartregister.family.R.id.action_family);
            baseRegisterActivity.startRegistration();
            return false;

        }else if (item.getItemId() == org.smartregister.family.R.id.action_job_aids) {
            bottomNavigationView.setSelectedItemId(org.smartregister.family.R.id.action_family);
            Intent intent = new Intent(baseRegisterActivity, DashBoardActivity.class);
            baseRegisterActivity.startActivity(intent);
            return false;
        }
        else {
            super.onNavigationItemSelected(item);
        }

        return true;
    }
}
