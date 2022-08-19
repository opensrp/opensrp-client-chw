package org.smartregister.chw.activity;

import android.content.Context;
import android.content.Intent;

import org.smartregister.chw.fragment.HivstResultsViewFragment;
import org.smartregister.chw.hivst.activity.BaseHivstResultViewActivity;
import org.smartregister.chw.hivst.fragment.BaseHivstResultViewFragment;
import org.smartregister.chw.hivst.util.Constants;

public class HivstResultViewActivity extends BaseHivstResultViewActivity {
    private static String baseEntityId;
    public static void startResultViewActivity(Context context, String baseEntityId){
        Intent intent = new Intent(context, HivstResultViewActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        HivstResultViewActivity.baseEntityId = baseEntityId;
        context.startActivity(intent);
    }

    @Override
    public BaseHivstResultViewFragment getBaseFragment() {
        return HivstResultsViewFragment.newInstance(baseEntityId);
    }
}
