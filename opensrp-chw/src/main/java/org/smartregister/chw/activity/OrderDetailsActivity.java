package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.json.JSONObject;
import org.smartregister.chw.cdp.activity.BaseOrderDetailsActivity;
import org.smartregister.chw.cdp.util.Constants;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.JsonFormUtils;

public class OrderDetailsActivity extends BaseOrderDetailsActivity {

    public static void startMe(Activity activity, CommonPersonObjectClient pc) {
        Intent intent = new Intent(activity, OrderDetailsActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.CLIENT, pc);
        activity.startActivity(intent);
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        startActivityForResult(FormUtils.getStartFormActivity(jsonForm, null, this), JsonFormUtils.REQUEST_CODE_GET_JSON);
    }
}
