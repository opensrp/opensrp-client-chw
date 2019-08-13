package org.smartregister.chw.core.contract;

import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.Set;

public interface BaseReferralRegisterFragmentContract {

    interface View extends BaseRegisterFragmentContract.View {

        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns, String tableName);
    }

    interface Presenter extends BaseRegisterFragmentContract.Presenter {

    }

    interface Model {
        String countSelect(String childTable, String mainCondition);

        String mainSelect(String taskTable, String mainCondition);
    }
}
