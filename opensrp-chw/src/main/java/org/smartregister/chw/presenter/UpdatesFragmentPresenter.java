package org.smartregister.chw.presenter;

import android.app.Activity;

import org.smartregister.chw.activity.UpdateRegisterDetailsActivity;
import org.smartregister.chw.core.contract.BaseReferralNotificationFragmentContract;
import org.smartregister.chw.core.presenter.BaseReferralNotificationFragmentPresenter;
import org.smartregister.chw.fragment.UpdatesRegisterFragment;
import org.smartregister.chw.model.UpdatesRegisterModel;

public class UpdatesFragmentPresenter extends BaseReferralNotificationFragmentPresenter {

    public UpdatesFragmentPresenter(BaseReferralNotificationFragmentContract.View view) {
        super(view, new UpdatesRegisterModel());
    }

    @Override
    public void displayDetailsActivity(String referralTaskId, String notificationType) {
        Activity activity = ((UpdatesRegisterFragment) getView()).getActivity();
        UpdateRegisterDetailsActivity.startActivity(activity, referralTaskId, notificationType);
    }
}
