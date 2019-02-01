package org.smartgresiter.wcaro.listener;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import org.smartgresiter.wcaro.activity.JobAidsActivity;

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
        super.onNavigationItemSelected(item);

        if (item.getItemId() == org.smartregister.family.R.id.action_job_aids) {
            view.setSelectedItemId(org.smartregister.family.R.id.action_family);
            Intent intent = new Intent(context, JobAidsActivity.class);
            context.startActivity(intent);
            return false;
        }

        return true;
    }
}
