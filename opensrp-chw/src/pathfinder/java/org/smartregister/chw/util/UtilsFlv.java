package org.smartregister.chw.util;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.Menu;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.smartregister.chw.R;
import org.smartregister.chw.core.rule.MalariaFollowUpRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.MalariaVisitUtil;
import org.smartregister.chw.fp.dao.FpDao;
import org.smartregister.chw.malaria.dao.MalariaDao;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.Utils;

import java.util.Date;

import timber.log.Timber;

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
            Date malariaTestDate = MalariaDao.getMalariaTestDate(baseEntityId);
            Date followUpDate = MalariaDao.getMalariaFollowUpVisitDate(baseEntityId);
            malariaFollowUpRule = MalariaVisitUtil.getMalariaStatus(malariaTestDate,followUpDate);
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
            menu.findItem(R.id.action_malaria_registration).setVisible(false);
        }
    }

    public static void updateFpMenuItems(String baseEntityId, Menu menu) {
        if (FpDao.isRegisteredForFp(baseEntityId)) {
            menu.findItem(R.id.action_fp_change).setVisible(true);
        } else {
            menu.findItem(R.id.action_fp_initiation).setVisible(true);
        }
    }

    public static boolean isClientOfReproductiveAge(CommonPersonObjectClient commonPersonObject, int fromAge, int toAge) {
        if (commonPersonObject == null) {
            Timber.e("Common is null");
            return false;
        }

        // check age
        String dobString = org.smartregister.util.Utils.getValue(commonPersonObject.getColumnmaps(), "dob", false);
        if (!TextUtils.isEmpty(dobString)) {
            Period period = new Period(new DateTime(dobString), new DateTime());
            int age = period.getYears();
            return age >= fromAge && age <= toAge;
        }

        return false;
    }

}
