package org.smartregister.chw.presenter;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.contract.CoreChildRegisterFragmentContract;
import org.smartregister.chw.core.presenter.CoreChildRegisterFragmentPresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.family.util.DBConstants;

public class ChildRegisterFragmentPresenter extends CoreChildRegisterFragmentPresenter {
    public ChildRegisterFragmentPresenter(CoreChildRegisterFragmentContract.View view, CoreChildRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getDefaultSortQuery() {
        return " MAX(ec_child.last_interacted_with , ifnull(VISIT_SUMMARY.visit_date,0)) DESC ";
    }

    @Override
    public String getMainCondition() {
        if (ChwApplication.getApplicationFlavor().dueVaccinesFilterInChildRegister())
            return String.format(" %s.%s is null AND %s", CoreConstants.TABLE_NAME.CHILD, DBConstants.KEY.DATE_REMOVED, ChildDBConstants.childDueVaccinesFilterForChildrenBelowTwoAndGirlsAgeNineToEleven());
        return super.getMainCondition();
    }

    @Override
    public String getMainCondition(String tableName) {
        if (ChwApplication.getApplicationFlavor().dueVaccinesFilterInChildRegister())
            return String.format(" %s is null AND %s", tableName + "." + DBConstants.KEY.DATE_REMOVED, ChildDBConstants.childDueVaccinesFilterForChildrenBelowTwoAndGirlsAgeNineToEleven(tableName));
        return super.getMainCondition(tableName);
    }
}
