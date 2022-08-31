package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.json.JSONObject;
import org.smartregister.chw.cdp.activity.BaseRestockingHistoryActivity;
import org.smartregister.chw.cdp.domain.OutletObject;
import org.smartregister.chw.cdp.util.Constants;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.family.util.JsonFormUtils;

public class RestockingVisitHistoryActivity extends BaseRestockingHistoryActivity {

    public static void startMe(Activity activity, OutletObject outletObject) {
        Intent intent = new Intent(activity, RestockingVisitHistoryActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.OUTLET_OBJECT, outletObject);
        activity.startActivity(intent);
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        startActivityForResult(FormUtils.getStartFormActivity(jsonForm, this.getString(org.smartregister.chw.core.R.string.outlet_registration), this), JsonFormUtils.REQUEST_CODE_GET_JSON);
    }
}
