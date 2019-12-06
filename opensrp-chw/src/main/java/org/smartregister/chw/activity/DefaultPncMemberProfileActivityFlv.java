package org.smartregister.chw.activity;

import android.view.Menu;

import org.smartregister.chw.R;

public class DefaultPncMemberProfileActivityFlv implements PncMemberProfileActivity.Flavor {

    @Override
    public Boolean onCreateOptionsMenu(Menu menu, String baseEntityId) {
        menu.findItem(R.id.action_malaria_confirmation).setVisible(false);
        return true;
    }

}
