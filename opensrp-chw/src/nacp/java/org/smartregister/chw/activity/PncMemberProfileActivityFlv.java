package org.smartregister.chw.activity;

import android.view.Menu;

import org.smartregister.chw.R;
import org.smartregister.chw.fp.dao.FpDao;
import org.smartregister.chw.util.UtilsFlv;

public class PncMemberProfileActivityFlv implements PncMemberProfileActivity.Flavor {

    @Override
    public Boolean onCreateOptionsMenu(Menu menu, String baseEntityId) {
        menu.findItem(R.id.action_malaria_diagnosis).setVisible(false);
        menu.findItem(R.id.action_fp_initiation_pnc).setVisible(false);
        menu.findItem(R.id.action_fp_change).setVisible(false);
        menu.findItem(R.id.action_malaria_registration).setVisible(false);
        UtilsFlv.updateHivMenuItems(baseEntityId, menu);
        return true;
    }
}
