package org.smartregister.chw.hf.activity;

import org.smartregister.chw.core.activity.BaseReferralRegister;
import org.smartregister.chw.core.presenter.BaseRefererralPresenter;
import org.smartregister.chw.hf.fragement.ReferralRegisterFragment;

import org.smartregister.view.fragment.BaseRegisterFragment;

public class ReferralRegister extends BaseReferralRegister {

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new ReferralRegisterFragment();
    }

    @Override
    protected void initializePresenter() {
        presenter = new BaseRefererralPresenter();
    }
}
