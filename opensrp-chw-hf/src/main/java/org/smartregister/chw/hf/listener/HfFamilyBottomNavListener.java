package org.smartregister.chw.hf.listener;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import org.smartregister.view.activity.BaseRegisterActivity;

public class HfFamilyBottomNavListener extends org.smartregister.family.listener.FamilyBottomNavigationListener {
    private Activity context;
    private BottomNavigationView bottomNavigationView;

    public HfFamilyBottomNavListener(Activity context, BottomNavigationView bottomNavigationView) {
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

        } else {
            super.onNavigationItemSelected(item);
        }

        return true;
    }
}
