package org.smartregister.brac.hnpp.activity;

import org.smartregister.chw.core.activity.BaseReferralRegister;
import org.smartregister.chw.core.presenter.BaseRefererralPresenter;
import org.smartregister.brac.hnpp.fragment.ReferralRegisterFragment;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class ReferralRegisterActivity extends BaseReferralRegister {

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initializePresenter() {
        presenter = new BaseRefererralPresenter();
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new ReferralRegisterFragment();
    }
}
