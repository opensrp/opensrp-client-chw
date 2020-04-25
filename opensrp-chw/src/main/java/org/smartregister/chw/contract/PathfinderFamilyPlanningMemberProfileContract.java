package org.smartregister.chw.contract;

import org.json.JSONObject;
import org.smartregister.chw.fp_pathfinder.contract.BaseFpProfileContract;
import org.smartregister.chw.fp_pathfinder.domain.FpMemberObject;
import org.smartregister.repository.AllSharedPreferences;

public interface PathfinderFamilyPlanningMemberProfileContract {

    interface View extends BaseFpProfileContract.View {
        void startFormActivity(JSONObject formJson, FpMemberObject fpMemberObject);
    }

    interface Presenter extends BaseFpProfileContract.Presenter {
        void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception;

        void startFamilyPlanningReferral();
    }

    interface Interactor extends BaseFpProfileContract.Interactor {
        void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityID) throws Exception;
    }
}