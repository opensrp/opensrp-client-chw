package org.smartregister.chw.hf.contract;

import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.domain.Task;

import java.util.Set;

public interface AncMemberProfileContract {

    interface View extends BaseAncMemberProfileContract.View {
        void setClientTasks(Set<Task> taskList);
    }

    interface Presenter extends BaseAncMemberProfileContract.Presenter {
        void fetchTasks();

        void setEntityId(String entityId);
    }

    interface Interactor extends BaseAncMemberProfileContract.Interactor {
        void getClientTasks(String planId, String baseEntityId, AncMemberProfileContract.InteractorCallBack callback);
    }

    interface InteractorCallBack extends BaseAncMemberProfileContract.InteractorCallBack {
        void setClientTasks(Set<Task> taskList);
    }
}
