package org.smartregister.chw.contract;

public interface MalariaProfileContract {
    interface View extends org.smartregister.chw.malaria.contract.MalariaProfileContract.View {
        void referToFacility();
    }
}
