package org.smartregister.brac.hnpp.presenter;

import org.smartregister.brac.hnpp.model.ReferralModel;
import org.smartregister.chw.core.contract.BaseReferralRegisterFragmentContract;
import org.smartregister.chw.core.presenter.BaseReferralFragmentPresenter;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.HashSet;

public class ReferralFragmentPresenter extends BaseReferralFragmentPresenter {

    public ReferralFragmentPresenter(BaseReferralRegisterFragmentContract.View view) {
        super(view);
        model = new ReferralModel();
    }

    @Override
    public void initializeQueries(String mainCondition) {
        String countSelect = model.countSelect(CoreConstants.TABLE_NAME.TASK, mainCondition);
        String mainSelect = model.mainSelect(CoreConstants.TABLE_NAME.TASK, CoreConstants.TABLE_NAME.CHILD, mainCondition);

        view.initializeQueryParams(CoreConstants.TABLE_NAME.CHILD, countSelect, mainSelect);
        view.initializeAdapter(new HashSet<>(), CoreConstants.TABLE_NAME.TASK);

        view.countExecute();
        view.filterandSortInInitializeQueries();
    }
}
