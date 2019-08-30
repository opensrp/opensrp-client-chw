package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.malaria.domain.MemberObject;
import org.smartregister.view.activity.SecuredActivity;

public class CoreMalariaUpcomingServicesActivity extends SecuredActivity {

    public static void startMe(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, CoreMalariaUpcomingServicesActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreation() {

    }

    @Override
    protected void onResumption() {

    }
}
