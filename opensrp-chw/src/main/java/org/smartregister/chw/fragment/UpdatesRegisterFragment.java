package org.smartregister.chw.fragment;

import android.view.View;

import org.smartregister.chw.core.fragment.BaseReferralNotificationFragment;
import org.smartregister.chw.presenter.UpdatesFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.Utils;

import java.util.HashMap;

import static org.smartregister.chw.core.utils.CoreConstants.DB_CONSTANTS.NOTIFICATION_TYPE;
import static org.smartregister.chw.core.utils.CoreConstants.DB_CONSTANTS.REFERRAL_TASK_ID;

public class UpdatesRegisterFragment extends BaseReferralNotificationFragment {

    @Override
    protected void startRegistration() {
        // Overridden not required
    }

    @Override
    protected void initializePresenter() {
        presenter = new UpdatesFragmentPresenter(this);
    }

    @Override
    public void setUniqueID(String qrCode) {
        // Overridden not required
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> advancedSearchFormData) {
        // Overridden not required
    }

    @Override
    protected void onViewClicked(View view) {
        CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
        String notificationType = Utils.getValue(client.getColumnmaps(), NOTIFICATION_TYPE, true);
        String referralTaskId = Utils.getValue(client.getColumnmaps(), REFERRAL_TASK_ID, true);
        getFragmentPresenter().displayDetailsActivity(referralTaskId, notificationType);
    }

    @Override
    public void showNotFoundPopup(String opensrpId) {
        // Overridden not required
    }

    private UpdatesFragmentPresenter getFragmentPresenter() {
        return (UpdatesFragmentPresenter) presenter;
    }
}