package com.opensrp.chw.hf.presenter;

import com.opensrp.chw.core.contract.BaseReferralRegisterFragmentContract;
import com.opensrp.chw.core.presenter.BaseRefererralFragmentPresenter;
import com.opensrp.chw.hf.model.ReferralModel;

public class ReferralFragmentPresenter extends BaseRefererralFragmentPresenter {

    public ReferralFragmentPresenter(BaseReferralRegisterFragmentContract.View view) {
        super(view);
        model = new ReferralModel();
    }
}
