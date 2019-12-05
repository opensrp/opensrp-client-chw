package org.smartregister.chw.activity;

import android.view.Menu;

import org.smartregister.chw.R;
import org.smartregister.chw.fp.dao.FpDao;

public class PncMemberProfileActivityFlv implements PncMemberProfileActivity.Flavor {

    @Override
    public Boolean onCreateOptionsMenu(Menu menu, String baseEntityId) {
        menu.findItem(R.id.action_malaria_confirmation).setVisible(true);
        if (!FpDao.isRegisteredForFp(baseEntityId)) {
            menu.findItem(R.id.action_fp_initiation).setVisible(true);
        }
        return true;
    }

}
