package org.smartgresiter.wcaro.listener;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import org.smartgresiter.wcaro.activity.JobAidsActivity;

public class FamilyBottomNavigationListener extends org.smartregister.family.listener.FamilyBottomNavigationListener {
    private Activity context;

    public FamilyBottomNavigationListener(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        super.onNavigationItemSelected(item);

        if (item.getItemId() == org.smartregister.family.R.id.action_job_aids) {
            Intent intent = new Intent(context, JobAidsActivity.class);
            context.startActivity(intent);
        }

        return true;
    }
}
