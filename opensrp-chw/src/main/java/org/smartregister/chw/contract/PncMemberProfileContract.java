package org.smartregister.chw.contract;

import org.json.JSONObject;
import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.chw.core.listener.OnRetrieveNotifications;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.pnc.contract.BasePncMemberProfileContract;
import org.smartregister.repository.AllSharedPreferences;

import java.util.List;

public interface PncMemberProfileContract {

    interface View extends BasePncMemberProfileContract.View, OnRetrieveNotifications {
        void startFormActivity(JSONObject formJson);

        List<ReferralTypeModel> getReferralTypeModels();
    }

    interface Presenter extends BasePncMemberProfileContract.Presenter {
        void startPncReferralForm();

        void referToFacility();

        void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception;
    }

    interface Interactor extends BasePncMemberProfileContract.Interactor, BaseAncMemberProfileContract.Interactor {
        void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityID) throws Exception;
    }

}
