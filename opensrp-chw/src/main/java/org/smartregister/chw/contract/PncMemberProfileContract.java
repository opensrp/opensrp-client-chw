package org.smartregister.chw.contract;

import org.json.JSONObject;
import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.chw.pnc.contract.BasePncMemberProfileContract;
import org.smartregister.repository.AllSharedPreferences;

public interface PncMemberProfileContract {

    interface View extends BasePncMemberProfileContract.View {
        void startFormActivity(JSONObject formJson);
    }

    interface Presenter extends BasePncMemberProfileContract.Presenter {
        void startPncReferralForm();

        void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception;
    }

    interface Interactor extends BasePncMemberProfileContract.Interactor, BaseAncMemberProfileContract.Interactor {
        void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityID) throws Exception;
    }

}
