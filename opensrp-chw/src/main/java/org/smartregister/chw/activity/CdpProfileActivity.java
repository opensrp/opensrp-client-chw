package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.cdp.util.Constants;
import org.smartregister.chw.core.activity.CoreCdpProfileActivity;

import static org.smartregister.chw.core.utils.Utils.passToolbarTitle;

public class CdpProfileActivity extends CoreCdpProfileActivity {

    public static void startProfile(Activity activity, String baseEntityId) {
        Intent intent = new Intent(activity, CdpProfileActivity.class);
        passToolbarTitle(activity, intent);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        activity.startActivity(intent);
    }
}
