package com.opensrp.chw.hf.activity;

import com.opensrp.chw.core.activity.BaseReferralRegister;
import com.opensrp.chw.hf.fragement.ReferralRegisterFragment;
import com.opensrp.chw.hf.presenter.RefererralPresenter;

import org.smartregister.view.fragment.BaseRegisterFragment;

public class ReferralRegister extends BaseReferralRegister {

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new ReferralRegisterFragment();
    }

    @Override
    protected void initializePresenter() {
        presenter = new RefererralPresenter();
    }
}
