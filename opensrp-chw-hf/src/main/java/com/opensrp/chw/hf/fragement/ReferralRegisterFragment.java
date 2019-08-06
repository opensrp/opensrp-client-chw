package com.opensrp.chw.hf.fragement;

import com.opensrp.chw.core.fragment.BaseReferralRegisterFragment;
import com.opensrp.chw.core.presenter.BaseRefererralFragmentPresenter;

public class ReferralRegisterFragment extends BaseReferralRegisterFragment {
    @Override
    protected void initializePresenter() {
        presenter = new BaseRefererralFragmentPresenter(this);
    }
}
