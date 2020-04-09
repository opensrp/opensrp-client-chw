package org.smartregister.chw.fragment;

import android.view.View;

import org.smartregister.chw.core.fragment.BaseReferralNotificationFragment;
import org.smartregister.chw.presenter.UpdatesFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.HashMap;

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

    }

    @Override
    public void showNotFoundPopup(String opensrpId) {
        // Overridden not required
    }
}