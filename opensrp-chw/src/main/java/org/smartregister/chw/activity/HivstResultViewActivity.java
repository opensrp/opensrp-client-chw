package org.smartregister.chw.activity;

import android.content.Context;
import android.content.Intent;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.smartregister.chw.R;
import org.smartregister.chw.fragment.HivstResultsViewFragment;
import org.smartregister.chw.hivst.activity.BaseHivstResultViewActivity;
import org.smartregister.chw.hivst.fragment.BaseHivstResultViewFragment;
import org.smartregister.chw.hivst.util.Constants;
import org.smartregister.family.util.Utils;

public class HivstResultViewActivity extends BaseHivstResultViewActivity {
    private static String baseEntityId;

    public static void startResultViewActivity(Context context, String baseEntityId) {
        Intent intent = new Intent(context, HivstResultViewActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        HivstResultViewActivity.baseEntityId = baseEntityId;
        context.startActivity(intent);
    }

    public static void startResultsForm(Context context, String jsonString, String baseEntityId, String entityId) {
        Intent intent = new Intent(context, HivstResultViewActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.HIVST_FORM_NAME, jsonString);
        intent.putExtra(Constants.JSON_FORM_EXTRA.ENTITY_ID, entityId);
        context.startActivity(intent);
    }

    @Override
    public void startFormActivity(String jsonString) {
        Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonString);

        Form form = new Form();
        form.setName(getString(R.string.hivst_result));
        form.setActionBarBackground(R.color.family_actionbar);
        form.setNavigationBackground(R.color.family_navigation);
        form.setHomeAsUpIndicator(R.mipmap.ic_cross_white);
        form.setPreviousLabel(getResources().getString(R.string.back));
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

        startActivityForResult(intent, Constants.REQUEST_CODE_GET_JSON);
    }


    @Override
    public BaseHivstResultViewFragment getBaseFragment() {
        return HivstResultsViewFragment.newInstance(baseEntityId);
    }
}
