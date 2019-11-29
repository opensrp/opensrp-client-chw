package org.smartregister.chw.presenter;

import org.smartregister.chw.core.presenter.CoreFpRegisterFragmentPresenter;
import org.smartregister.chw.fp.contract.BaseFpRegisterFragmentContract;

public class FpRegisterFragmentPresenter extends CoreFpRegisterFragmentPresenter {

    public FpRegisterFragmentPresenter(BaseFpRegisterFragmentContract.View view,
                                       BaseFpRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }
}
