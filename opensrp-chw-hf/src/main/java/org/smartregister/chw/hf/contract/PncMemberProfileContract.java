package org.smartregister.chw.hf.contract;

import org.smartregister.chw.pnc.contract.BasePncMemberProfileContract;
import org.smartregister.domain.Task;

import java.util.Set;

public interface PncMemberProfileContract {

    interface View extends BasePncMemberProfileContract.View {
        void setReferralTasks(Set<Task> taskList);
    }

    interface Presenter extends BasePncMemberProfileContract.Presenter {
        void fetchReferralTasks();
    }

    interface Interactor {
        void getReferralTasks(String planId, String baseEntityId, PncMemberProfileContract.InteractorCallback callback);
    }

    interface InteractorCallback {
        void updateReferralTasks(Set<Task> taskList);
    }

}
