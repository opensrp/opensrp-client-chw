package org.smartregister.chw.presenter;

import org.smartregister.chw.core.contract.CoreChildRegisterContract;
import org.smartregister.chw.core.presenter.CoreChildRegisterPresenter;

public class ChildRegisterPresenter extends CoreChildRegisterPresenter {

    public ChildRegisterPresenter(CoreChildRegisterContract.View view, CoreChildRegisterContract.Model model) {
        super(view, model);
    }
}
