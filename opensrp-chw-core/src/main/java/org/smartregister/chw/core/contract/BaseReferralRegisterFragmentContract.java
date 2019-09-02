package org.smartregister.chw.core.contract;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.Set;

public interface BaseReferralRegisterFragmentContract {

    interface View extends BaseRegisterFragmentContract.View {
        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns, String tableName);

        void setClient(CommonPersonObjectClient commonPersonObjectClient);
    }

    interface Presenter extends BaseRegisterFragmentContract.Presenter {
        void fetchClient();

        void setTasksFocus(String taskFocus);
    }

    interface Interactor {
        void getClientDetails(String baseEntityId, BaseReferralRegisterFragmentContract.InteractorCallBack callback, String taskFocus);
    }

    interface InteractorCallBack {
        void clientDetails(CommonPersonObjectClient client);
    }

    interface Model {
        String countSelect(String childTable, String mainCondition);

        String mainSelect(String taskTable, String entityTable, String mainCondition);
    }
}
