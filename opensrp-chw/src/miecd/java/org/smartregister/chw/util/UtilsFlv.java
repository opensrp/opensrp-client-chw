package org.smartregister.chw.util;

import android.os.AsyncTask;
import android.view.Menu;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.core.rule.MalariaFollowUpRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.MalariaVisitUtil;
import org.smartregister.chw.dao.MalariaDao;
import org.smartregister.chw.fp.dao.FpDao;
import org.smartregister.util.Utils;

import java.util.Date;

public class UtilsFlv {
    private static class UpdateFollowUpMenuItem extends AsyncTask<Void, Void, Void> {
        private final String baseEntityId;
        private Menu menu;
        private MalariaFollowUpRule malariaFollowUpRule;

        public UpdateFollowUpMenuItem(String baseEntityId, Menu menu) {
            this.baseEntityId = baseEntityId;
            this.menu = menu;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Date date = MalariaDao.getMalariaTestDate(baseEntityId);
            malariaFollowUpRule = MalariaVisitUtil.getMalariaStatus(date);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            if (malariaFollowUpRule != null && StringUtils.isNotBlank(malariaFollowUpRule.getButtonStatus()) &&
                    !CoreConstants.VISIT_STATE.EXPIRED.equalsIgnoreCase(malariaFollowUpRule.getButtonStatus())) {
                menu.findItem(R.id.action_malaria_followup_visit).setVisible(true);
            }
        }
    }

    public static void updateMalariaMenuItems(String baseEntityId, Menu menu) {
        if (MalariaDao.isRegisteredForMalaria(baseEntityId)) {
            Utils.startAsyncTask(new UpdateFollowUpMenuItem(baseEntityId, menu), null);
        } else {
            menu.findItem(R.id.action_malaria_registration).setVisible(true);
        }
    }

    public static void updateFpMenuItems(String baseEntityId, Menu menu) {
        if (FpDao.isRegisteredForFp(baseEntityId)) {
            menu.findItem(R.id.action_fp_change).setVisible(true);
        } else {
            menu.findItem(R.id.action_fp_initiation).setVisible(true);
        }
    }

}
