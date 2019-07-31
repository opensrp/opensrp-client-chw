package org.smartregister.chw.presenter;

import com.opensrp.chw.core.contract.CoreChildRegisterFragmentContract;
import com.opensrp.chw.core.presenter.CoreChildRegisterFragmentPresenter;

public class ChildRegisterFragmentPresenter extends CoreChildRegisterFragmentPresenter {

    public ChildRegisterFragmentPresenter(CoreChildRegisterFragmentContract.View view, CoreChildRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }
}
