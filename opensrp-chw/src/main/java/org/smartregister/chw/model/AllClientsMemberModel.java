package org.smartregister.chw.model;

import org.smartregister.chw.contract.AllClientsMemberContract;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.JsonFormUtils;

public class AllClientsMemberModel implements AllClientsMemberContract.Model {
    @Override
    public FamilyEventClient processJsonForm(String jsonString, String familyBaseEntityId) {
        return JsonFormUtils.processFamilyUpdateForm(FamilyLibrary.getInstance().context().allSharedPreferences(), jsonString, familyBaseEntityId);
    }
}
