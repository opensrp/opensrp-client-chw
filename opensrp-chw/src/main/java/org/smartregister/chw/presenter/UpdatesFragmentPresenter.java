package org.smartregister.chw.presenter;

import org.smartregister.chw.core.contract.BaseReferralNotificationFragmentContract;
import org.smartregister.chw.core.presenter.BaseReferralNotificationFragmentPresenter;
import org.smartregister.chw.model.UpdatesRegisterModel;

public class UpdatesFragmentPresenter extends BaseReferralNotificationFragmentPresenter {

    public UpdatesFragmentPresenter(BaseReferralNotificationFragmentContract.View view) {
        super(view, new UpdatesRegisterModel());
    }
}
