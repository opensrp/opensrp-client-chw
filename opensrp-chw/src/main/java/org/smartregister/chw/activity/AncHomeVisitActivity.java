package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.anc.activity.BaseAncHomeVisitActivity;

public class AncHomeVisitActivity extends BaseAncHomeVisitActivity {

    public static void startMe(Activity activity, String memberBaseEntityID) {
        Intent intent = new Intent(activity, AncHomeVisitActivity.class);
        intent.putExtra("BASE_ENTITY_ID", memberBaseEntityID);
        activity.startActivity(intent);
    }
}
