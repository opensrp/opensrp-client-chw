package org.smartregister.chw.task;

import android.app.Activity;
import android.os.Bundle;

import org.smartregister.chw.activity.FamilyPlanningMemberProfileActivity;
import org.smartregister.chw.anc.activity.BaseAncMemberProfileActivity;
import org.smartregister.chw.core.activity.CoreAboveFiveChildProfileActivity;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.task.CoreChwNotificationGoToMemberProfileTask;
import org.smartregister.chw.fp.dao.FpDao;
import org.smartregister.chw.pnc.activity.BasePncMemberProfileActivity;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class ChwGoToMemberProfileBasedOnRegisterTask extends CoreChwNotificationGoToMemberProfileTask {


    public ChwGoToMemberProfileBasedOnRegisterTask(CommonPersonObjectClient commonPersonObjectClient, Bundle bundle, String notificationType, Activity activity) {
        super(commonPersonObjectClient, bundle, notificationType, activity);
    }

    @Override
    protected void goToFpProfile(String baseEntityId, Activity activity) {
        FamilyPlanningMemberProfileActivity.startFpMemberProfileActivity(activity, FpDao.getMember(baseEntityId));
    }

    @Override
    protected Class<? extends CoreAboveFiveChildProfileActivity> getAboveFiveChildProfileActivityClass() {
        return null;
    }

    @Override
    protected Class<? extends CoreChildProfileActivity> getChildProfileActivityClass() {
        return null;
    }

    @Override
    protected Class<? extends BaseAncMemberProfileActivity> getAncMemberProfileActivityClass() {
        return null;
    }

    @Override
    protected Class<? extends BasePncMemberProfileActivity> getPncMemberProfileActivityClass() {
        return null;
    }

    @Override
    protected Class<?> getFamilyOtherMemberProfileActivityClass() {
        return null;
    }
}
