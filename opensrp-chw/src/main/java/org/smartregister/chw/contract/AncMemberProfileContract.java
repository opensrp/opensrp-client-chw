package org.smartregister.chw.contract;

import org.smartregister.chw.core.listener.OnRetrieveNotifications;
import org.smartregister.chw.model.ReferralTypeModel;

import java.util.List;

public interface AncMemberProfileContract extends org.smartregister.chw.core.contract.AncMemberProfileContract {
    interface Presenter{
        void referToFacility();
    }

    interface View extends OnRetrieveNotifications {
        List<ReferralTypeModel> getReferralTypeModels();
    }
}
