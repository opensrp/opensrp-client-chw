package org.smartregister.chw.presenter;

import org.smartregister.chw.anc.contract.BaseAncRegisterFragmentContract;
import org.smartregister.chw.core.presenter.AncRegisterFragmentPresenter;

public class ChwAncRegisterFragmentPresenter extends AncRegisterFragmentPresenter {

    public ChwAncRegisterFragmentPresenter(BaseAncRegisterFragmentContract.View view, BaseAncRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getDefaultSortQuery() {
        return " MAX(ec_anc_register.last_interacted_with , ifnull(VISIT_SUMMARY.visit_date,0)) DESC ";
    }
}
