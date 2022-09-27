package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.agyw.activity.BaseAGYWServicesActivity;
import org.smartregister.chw.agyw.handlers.BaseServiceActionHandler;
import org.smartregister.chw.agyw.util.Constants;
import org.smartregister.chw.listener.AgywServiceActionHandler;

public class AGYWServicesActivity extends BaseAGYWServicesActivity {
    public static void startMe(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, AGYWServicesActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        activity.startActivity(intent);
    }

    @Override
    public BaseServiceActionHandler getServiceHandler() {
        return new AgywServiceActionHandler();
    }
}
