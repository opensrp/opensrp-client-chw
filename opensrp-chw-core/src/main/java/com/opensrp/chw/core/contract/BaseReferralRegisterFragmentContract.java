package com.opensrp.chw.core.contract;

import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.Set;

public interface BaseReferralRegisterFragmentContract {

    interface View extends BaseRegisterFragmentContract.View {

        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns);
    }

    interface Presenter extends BaseRegisterFragmentContract.Presenter {

    }
}
