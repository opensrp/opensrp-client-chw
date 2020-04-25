package org.smartregister.chw.presenter;

import org.smartregister.chw.fp_pathfinder.contract.BaseFpRegisterFragmentContract;
import org.smartregister.chw.fp_pathfinder.presenter.BaseFpRegisterFragmentPresenter;

public class PathfinderFamilyPlanningRegisterFragmentPresenter extends BaseFpRegisterFragmentPresenter {

    public PathfinderFamilyPlanningRegisterFragmentPresenter(BaseFpRegisterFragmentContract.View view,
                                                             BaseFpRegisterFragmentContract.Model model) {
        super(view, model);
    }

    @Override
    public String getDefaultSortQuery() {
        return " MAX(ec_family_planning.last_interacted_with , ifnull(VISIT_SUMMARY.visit_date,0)) DESC ";
    }
}
