package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.presenter.AllClientsMemberReferralPresenter;
import org.smartregister.chw.referral.activity.BaseIssueReferralActivity;
import org.smartregister.chw.referral.interactor.BaseIssueReferralInteractor;
import org.smartregister.chw.referral.model.BaseIssueReferralModel;
import org.smartregister.chw.referral.presenter.BaseIssueReferralPresenter;
import org.smartregister.chw.referral.util.Constants;
import org.smartregister.family.util.JsonFormUtils;

public class AllClientMemberReferralActivity extends BaseIssueReferralActivity {
    public static String BASE_ENTITY_ID;

    public static void startReferralActivity(Activity activity, String baseEntityID, JSONObject formJsonObject, String referralServiceId) {
        BASE_ENTITY_ID = baseEntityID;
        Intent intent = new Intent(activity, AllClientMemberReferralActivity.class);
        intent.putExtra(Constants.ActivityPayload.BASE_ENTITY_ID, baseEntityID);
        intent.putExtra(Constants.ActivityPayload.REFERRAL_SERVICE_IDS, referralServiceId);
        intent.putExtra(Constants.ActivityPayload.JSON_FORM, formJsonObject.toString());
        intent.putExtra(Constants.ActivityPayload.ACTION, Constants.ActivityPayloadType.REGISTRATION);

        activity.startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
    }

    @NotNull
    @Override
    public BaseIssueReferralPresenter presenter() {
        return new AllClientsMemberReferralPresenter(BASE_ENTITY_ID, this,
                BaseIssueReferralModel.class, new BaseIssueReferralInteractor());
    }
}