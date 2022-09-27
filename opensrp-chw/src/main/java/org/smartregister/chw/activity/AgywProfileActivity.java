package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

import org.smartregister.chw.agyw.activity.BaseAGYWProfileActivity;
import org.smartregister.chw.agyw.util.Constants;

public class AgywProfileActivity extends BaseAGYWProfileActivity {

    public static void startProfile(Activity activity, String baseEntityId) {
        Intent intent = new Intent(activity, AgywProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        activity.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO: update options menu with required details
        return false;
    }
}
