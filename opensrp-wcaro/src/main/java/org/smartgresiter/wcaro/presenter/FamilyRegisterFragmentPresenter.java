package org.smartgresiter.wcaro.presenter;

import org.smartgresiter.wcaro.contract.RegisterFragmentContract;
import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartregister.family.contract.FamilyRegisterFragmentContract;
import org.smartregister.family.presenter.BaseFamilyRegisterFragmentPresenter;
import org.smartregister.family.util.DBConstants;

public class FamilyRegisterFragmentPresenter extends BaseFamilyRegisterFragmentPresenter implements RegisterFragmentContract.Presenter {

    public FamilyRegisterFragmentPresenter(FamilyRegisterFragmentContract.View view, FamilyRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getDefaultSortQuery() {
        return DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ";
    }

    @Override
    public String getDueFilterCondition() {
        return getMainCondition() + " AND ((" + ChildDBConstants.KEY.LAST_HOME_VISIT + " is null OR ((" + ChildDBConstants.KEY.LAST_HOME_VISIT + "/1000) > strftime('%s',datetime('now','start of month')))) AND (" + ChildDBConstants.KEY.VISIT_NOT_DONE + " is null OR ((" + ChildDBConstants.KEY.VISIT_NOT_DONE + "/1000) > strftime('%s',datetime('now','start of month'))))) ";
    }

}
