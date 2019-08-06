package com.opensrp.chw.core.presenter;

import com.opensrp.chw.core.contract.BaseReferralRegisterFragmentContract;
import com.opensrp.chw.core.model.BaseReferralModel;
import com.opensrp.chw.core.utils.CoreConstants;

public class BaseRefererralFragmentPresenter implements BaseReferralRegisterFragmentContract.Presenter {


    protected BaseReferralRegisterFragmentContract.View view;

    protected BaseReferralRegisterFragmentContract.Model model;

    public BaseRefererralFragmentPresenter(BaseReferralRegisterFragmentContract.View view) {
        this.view = view;
        model = new BaseReferralModel();
    }

    @Override
    public void processViewConfigurations() {

    }

    @Override
    public void initializeQueries(String mainCondition) {
        String countSelect = model.countSelect(CoreConstants.TABLE_NAME.TASK, mainCondition);
        String mainSelect = model.mainSelect(CoreConstants.TABLE_NAME.TASK, mainCondition);

        view.initializeQueryParams(CoreConstants.TABLE_NAME.CHILD, countSelect, mainSelect);
        view.initializeAdapter(null, CoreConstants.TABLE_NAME.TASK);

        view.countExecute();
        view.filterandSortInInitializeQueries();
    }

    @Override
    public void startSync() {

    }

    @Override
    public void searchGlobally(String s) {

    }
}
