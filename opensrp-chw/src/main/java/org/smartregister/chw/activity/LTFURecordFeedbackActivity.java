package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.referral.activity.BaseIssueReferralActivity;
import org.smartregister.chw.referral.util.Constants;
import org.smartregister.chw.util.JsonFormUtils;

public class LTFURecordFeedbackActivity extends BaseIssueReferralActivity {

    // private static String BASE_ENTITY_ID;

    public static void startFeedbackFormActivityForResults(Activity activity, String baseEntityId, JSONObject formJsonObject, boolean useCustomLayout) {
        //  BASE_ENTITY_ID = baseEntityId;
        Intent intent = new Intent(activity, LTFURecordFeedbackActivity.class);
        intent.putExtra(Constants.ActivityPayload.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(Constants.ActivityPayload.JSON_FORM, formJsonObject.toString());
        intent.putExtra(Constants.ActivityPayload.ACTION, Constants.ActivityPayloadType.REGISTRATION);
        intent.putExtra(Constants.ActivityPayload.USE_CUSTOM_LAYOUT, useCustomLayout);
        activity.startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
    }


}
