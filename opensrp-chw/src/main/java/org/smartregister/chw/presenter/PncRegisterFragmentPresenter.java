package org.smartregister.chw.presenter;

import org.smartregister.chw.anc.contract.BaseAncRegisterFragmentContract;
import org.smartregister.chw.pnc.presenter.BasePncRegisterFragmentPresenter;

public class PncRegisterFragmentPresenter extends BasePncRegisterFragmentPresenter {
    public PncRegisterFragmentPresenter(BaseAncRegisterFragmentContract.View view, BaseAncRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getDefaultSortQuery() {
        return " MAX(ec_pregnancy_outcome.last_interacted_with , ifnull(VISIT_SUMMARY.visit_date,0)) DESC ";
    }
}
