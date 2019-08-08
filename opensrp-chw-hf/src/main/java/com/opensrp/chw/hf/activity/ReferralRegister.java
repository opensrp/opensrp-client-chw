package com.opensrp.chw.hf.activity;

import com.opensrp.chw.core.activity.BaseReferralRegister;
import com.opensrp.chw.core.presenter.BaseRefererralPresenter;
import com.opensrp.chw.hf.fragement.ReferralRegisterFragment;
import com.opensrp.chw.hf.utils.TestDataUtils;

import org.smartregister.view.fragment.BaseRegisterFragment;

public class ReferralRegister extends BaseReferralRegister {

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new ReferralRegisterFragment();
    }

    @Override
    protected void initializePresenter() {
        presenter = new BaseRefererralPresenter();

        new TestDataUtils().populateTasks();
    }
}
