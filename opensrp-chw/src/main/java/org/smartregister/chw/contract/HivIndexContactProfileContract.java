package org.smartregister.chw.contract;

import org.smartregister.chw.core.contract.CoreIndexContactProfileContract;

public interface HivIndexContactProfileContract extends CoreIndexContactProfileContract {
    interface Presenter {
        void referToFacility();
    }
}
