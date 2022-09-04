package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.cdp.activity.BaseOrderDetailsActivity;
import org.smartregister.chw.cdp.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class OrderDetailsActivity extends BaseOrderDetailsActivity {

    public static void startMe(Activity activity, CommonPersonObjectClient pc){
        Intent intent = new Intent(activity, OrderDetailsActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.CLIENT, pc);
        activity.startActivity(intent);
    }
}
