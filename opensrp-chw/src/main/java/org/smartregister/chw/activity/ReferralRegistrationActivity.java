package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.json.JSONObject;
import org.smartregister.chw.presenter.IssueReferralActivityPresenter;
import org.smartregister.chw.referral.ReferralLibrary;
import org.smartregister.chw.referral.activity.BaseIssueReferralActivity;
import org.smartregister.chw.referral.contract.BaseIssueReferralContract;
import org.smartregister.chw.referral.interactor.BaseIssueReferralInteractor;
import org.smartregister.chw.referral.model.BaseIssueReferralModel;
import org.smartregister.chw.referral.util.Constants;
import org.smartregister.family.util.JsonFormUtils;

import static org.smartregister.chw.core.utils.CoreConstants.JSON_FORM.getGeneralReferralForm;


public class ReferralRegistrationActivity extends BaseIssueReferralActivity {
    private static String BASE_ENTITY_ID;

    public static void startGeneralReferralFormActivityForResults(Activity activity, String baseEntityID, String serviceId) {

        //TODO Coze: the following line is used to seed services and indicators only for testing purposes and should not be available for production version of the app
        ReferralLibrary.getInstance().seedSampleReferralServicesAndIndicators();

        BASE_ENTITY_ID = baseEntityID;
        Intent intent = new Intent(activity, ReferralRegistrationActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.REFERRAL_FORM_NAME, getGeneralReferralForm());
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.INJECT_VALUES_FROM_DB, true);
        intent.putExtra(org.smartregister.chw.referral.util.Constants.ACTIVITY_PAYLOAD.ACTION, org.smartregister.chw.referral.util.Constants.ACTIVITY_PAYLOAD_TYPE.REGISTRATION);

        //TODO the following line is used for testing configuration of referral form with a specific service(s) by passing referral service id(s)
        if (serviceId != null) {
            intent.putExtra(org.smartregister.chw.referral.util.Constants.ACTIVITY_PAYLOAD.REFERRAL_SERVICE_IDS, serviceId);
        }

        intent.putExtra(org.smartregister.chw.referral.util.Constants.ACTIVITY_PAYLOAD.ACTION, org.smartregister.chw.referral.util.Constants.ACTIVITY_PAYLOAD_TYPE.REGISTRATION);

        activity.startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    public static void startGeneralReferralFormActivityForResults(Activity activity, String baseEntityID, JSONObject formJsonObject) {
        //TODO Coze: the following line is used to seed services and indicators only for testing purposes and should not be available for production version of the app
        ReferralLibrary.getInstance().seedSampleReferralServicesAndIndicators();

        BASE_ENTITY_ID = baseEntityID;
        Intent intent = new Intent(activity, ReferralRegistrationActivity.class);
        intent.putExtra(org.smartregister.chw.referral.util.Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        intent.putExtra(org.smartregister.chw.referral.util.Constants.ACTIVITY_PAYLOAD.JSON_FORM, formJsonObject.toString());
        intent.putExtra(org.smartregister.chw.referral.util.Constants.ACTIVITY_PAYLOAD.ACTION, org.smartregister.chw.referral.util.Constants.ACTIVITY_PAYLOAD_TYPE.REGISTRATION);

        activity.startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }


    @Override
    public BaseIssueReferralContract.Presenter presenter() {
        return new IssueReferralActivityPresenter(BASE_ENTITY_ID, this, BaseIssueReferralModel.class, new BaseIssueReferralInteractor());
    }

}