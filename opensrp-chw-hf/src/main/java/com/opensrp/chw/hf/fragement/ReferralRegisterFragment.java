package com.opensrp.chw.hf.fragement;

import com.opensrp.chw.core.fragment.BaseReferralRegisterFragment;
import com.opensrp.chw.hf.presenter.ReferralFragmentPresenter;

public class ReferralRegisterFragment extends BaseReferralRegisterFragment {
    @Override
    protected void initializePresenter() {
        presenter = new ReferralFragmentPresenter(this);
    }
}
