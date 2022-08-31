package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.cdp.activity.BaseRestockingHistoryActivity;
import org.smartregister.chw.cdp.domain.OutletObject;
import org.smartregister.chw.cdp.util.Constants;

public class RestockingVisitHistoryActivity extends BaseRestockingHistoryActivity {

    public static void startMe(Activity activity, OutletObject outletObject){
        Intent intent = new Intent(activity, RestockingVisitHistoryActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.OUTLET_OBJECT, outletObject);
        activity.startActivity(intent);
    }
}
