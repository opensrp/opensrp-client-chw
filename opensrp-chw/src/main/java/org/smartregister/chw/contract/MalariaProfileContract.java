package org.smartregister.chw.contract;

import org.smartregister.chw.core.listener.OnRetrieveNotifications;

public interface MalariaProfileContract {
    interface View extends org.smartregister.chw.malaria.contract.MalariaProfileContract.View, OnRetrieveNotifications {
        void referToFacility();
    }
}
