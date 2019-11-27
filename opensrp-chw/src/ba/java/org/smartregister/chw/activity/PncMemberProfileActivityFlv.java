package org.smartregister.chw.activity;

import android.view.Menu;

import org.smartregister.chw.R;

public class PncMemberProfileActivityFlv implements PncMemberProfileActivity.Flavor {

    @Override
    public Boolean onCreateOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_malaria_confirmation).setVisible(true);
        menu.findItem(R.id.action_fp_initiation).setVisible(true);
        return true;
    }

}
