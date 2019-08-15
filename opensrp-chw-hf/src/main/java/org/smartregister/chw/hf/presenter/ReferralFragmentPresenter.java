package org.smartregister.chw.hf.presenter;

import org.smartregister.chw.core.contract.BaseReferralRegisterFragmentContract;
import org.smartregister.chw.core.presenter.BaseRefererralFragmentPresenter;
import org.smartregister.chw.hf.model.ReferralModel;

public class ReferralFragmentPresenter extends BaseRefererralFragmentPresenter {

    public ReferralFragmentPresenter(BaseReferralRegisterFragmentContract.View view) {
        super(view);
        model = new ReferralModel();
    }
}
