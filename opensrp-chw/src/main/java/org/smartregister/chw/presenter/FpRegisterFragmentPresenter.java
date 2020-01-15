package org.smartregister.chw.presenter;

import org.smartregister.chw.fp.contract.BaseFpRegisterFragmentContract;
import org.smartregister.chw.fp.presenter.BaseFpRegisterFragmentPresenter;

public class FpRegisterFragmentPresenter extends BaseFpRegisterFragmentPresenter {

    public FpRegisterFragmentPresenter(BaseFpRegisterFragmentContract.View view,
                                       BaseFpRegisterFragmentContract.Model model) {
        super(view, model);
    }

    @Override
    public String getDefaultSortQuery() {
        return " MAX(ec_family_planning.last_interacted_with , ifnull(VISIT_SUMMARY.visit_date,0)) DESC ";
    }
}
