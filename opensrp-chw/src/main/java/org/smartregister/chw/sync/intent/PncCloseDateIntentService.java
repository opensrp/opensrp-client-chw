package org.smartregister.chw.sync.intent;

import android.app.IntentService;
import android.content.Intent;

import org.smartregister.chw.activity.PncMemberProfileActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.dao.ChwPNCDao;

import java.util.List;

public class PncCloseDateIntentService extends IntentService {

    private static final String TAG = PncCloseDateIntentService.class.getSimpleName();


    public PncCloseDateIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<MemberObject> memberObjectList = ChwPNCDao.getPncMembersWithMoreThan42Days();
        if (memberObjectList != null && memberObjectList.size() > 0) {
            for (MemberObject memberObject : memberObjectList) {
                PncMemberProfileActivity.closePncMemberVisits(memberObject.getBaseEntityId());
            }
        }
    }

}
