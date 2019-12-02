package org.smartregister.chw.presenter;

import org.smartregister.chw.core.contract.CoreChildRegisterFragmentContract;
import org.smartregister.chw.core.presenter.CoreChildRegisterFragmentPresenter;

public class ChildRegisterFragmentPresenter extends CoreChildRegisterFragmentPresenter {
    public ChildRegisterFragmentPresenter(CoreChildRegisterFragmentContract.View view, CoreChildRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getDefaultSortQuery() {
        return " MAX(ec_child.last_interacted_with , ifnull(VISIT_SUMMARY.visit_date,0)) DESC ";
    }
}
