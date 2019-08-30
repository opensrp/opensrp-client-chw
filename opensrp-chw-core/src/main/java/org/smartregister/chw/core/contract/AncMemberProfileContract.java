package org.smartregister.chw.core.contract;

import org.json.JSONObject;
import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.domain.Task;
import org.smartregister.repository.AllSharedPreferences;

import java.util.Set;

public interface AncMemberProfileContract {

    interface View extends BaseAncMemberProfileContract.View {
        void setClientTasks(Set<Task> taskList);

        void startFormActivity(JSONObject formJson);
    }

    interface Presenter extends BaseAncMemberProfileContract.Presenter {
        void fetchTasks();

        void setEntityId(String entityId);

        void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception;

        void startAncReferralForm();
    }

    interface Interactor extends BaseAncMemberProfileContract.Interactor {
        void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityID) throws Exception;

        void getClientTasks(String planId, String baseEntityId, AncMemberProfileContract.InteractorCallBack callback);
    }

    interface InteractorCallBack extends BaseAncMemberProfileContract.InteractorCallBack {
        void setClientTasks(Set<Task> taskList);
    }
}
