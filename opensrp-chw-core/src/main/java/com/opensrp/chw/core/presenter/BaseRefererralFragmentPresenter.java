package com.opensrp.chw.core.presenter;

import com.opensrp.chw.core.contract.BaseReferralRegisterFragmentContract;

public class BaseRefererralFragmentPresenter implements BaseReferralRegisterFragmentContract.Presenter {


    protected BaseReferralRegisterFragmentContract.View view;

    public BaseRefererralFragmentPresenter(BaseReferralRegisterFragmentContract.View view) {
        this.view = view;
    }

    @Override
    public void processViewConfigurations() {

    }

    @Override
    public void initializeQueries(String mainCondition) {
        view.initializeAdapter(null);
    }

    @Override
    public void startSync() {

    }

    @Override
    public void searchGlobally(String s) {

    }
}
