package org.smartregister.chw.contract;

import org.smartregister.chw.core.contract.CoreFamilyPlanningMemberProfileContract;

public interface FamilyPlanningMemberProfileContract extends CoreFamilyPlanningMemberProfileContract {
    interface Presenter {
        void referToFacility();
    }
}
