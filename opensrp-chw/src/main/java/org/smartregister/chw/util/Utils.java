package org.smartregister.chw.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.ClientReferralActivity;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.dao.MalariaDao;
import org.smartregister.chw.model.ReferralTypeModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils extends org.smartregister.chw.core.utils.Utils {

    public static void launchClientReferralActivity(Activity activity, List<ReferralTypeModel> referralTypeModels, String baseEntityId) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ENTITY_ID, baseEntityId);
        bundle.putParcelableArrayList(Constants.REFERRAL_TYPES, (ArrayList<ReferralTypeModel>) referralTypeModels);
        activity.startActivity(new Intent(activity, ClientReferralActivity.class).putExtras(bundle));
    }

    public static void malariaUpcomingServices(String baseEntityID, Context context, List<BaseUpcomingService> baseUpcomingServices) {
        Date malariaTestDate = MalariaDao.getMalariaTestDate(baseEntityID);
        DateTime dateTime = new DateTime(malariaTestDate);
        if (Days.daysBetween(dateTime, new DateTime()).getDays() <= 14) {
            BaseUpcomingService followUP = new BaseUpcomingService();
            followUP.setServiceName(context.getString(R.string.follow_up_visit));
            followUP.setServiceDate(dateTime.plusDays(7).toDate());
            followUP.setOverDueDate(dateTime.plusDays(10).toDate());
            baseUpcomingServices.add(followUP);
        }
    }
}
