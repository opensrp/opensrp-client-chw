package org.smartregister.chw.core.presenter;

import org.smartregister.chw.core.contract.BaseReferralRegisterFragmentContract;
import org.smartregister.chw.core.model.BaseReferralModel;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.configurableviews.model.View;

import java.util.HashSet;

public class BaseReferralFragmentPresenter implements BaseReferralRegisterFragmentContract.Presenter {
    protected BaseReferralRegisterFragmentContract.View view;
    protected BaseReferralRegisterFragmentContract.Model model;

    public BaseReferralFragmentPresenter(BaseReferralRegisterFragmentContract.View view) {
        this.view = view;
        model = new BaseReferralModel();
    }

    @Override
    public void processViewConfigurations() {
        //// TODO: 15/08/19
    }

    @Override
    public void initializeQueries(String mainCondition) {
        String countSelect = model.countSelect(CoreConstants.TABLE_NAME.TASK, mainCondition);
        String mainSelect = model.mainSelect(CoreConstants.TABLE_NAME.TASK, mainCondition);

        view.initializeQueryParams(CoreConstants.TABLE_NAME.CHILD, countSelect, mainSelect);
        view.initializeAdapter(new HashSet<View>(), CoreConstants.TABLE_NAME.TASK);

        view.countExecute();
        view.filterandSortInInitializeQueries();
    }

    @Override
    public void startSync() {
        //// TODO: 15/08/19
    }

    @Override
    public void searchGlobally(String s) {
        //// TODO: 15/08/19
    }
}
