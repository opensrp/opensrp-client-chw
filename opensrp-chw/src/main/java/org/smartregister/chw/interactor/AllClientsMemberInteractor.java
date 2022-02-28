package org.smartregister.chw.interactor;

import org.smartregister.chw.contract.AllClientsMemberContract;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.contract.FamilyOtherMemberContract;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.interactor.FamilyOtherMemberProfileInteractor;

public class AllClientsMemberInteractor extends FamilyOtherMemberProfileInteractor implements AllClientsMemberContract.Interactor {

    private FamilyProfileInteractor familyProfileInteractor;

    public AllClientsMemberInteractor() {
        familyProfileInteractor = new FamilyProfileInteractor();
    }

    @Override
    public void updateLocationInfo(String jsonString, FamilyEventClient familyEventClient, FamilyProfileContract.InteractorCallBack interactorCallback) {
        familyEventClient.getEvent().setEntityType(CoreConstants.TABLE_NAME.INDEPENDENT_CLIENT);
        familyProfileInteractor.saveRegistration(familyEventClient, jsonString, true, interactorCallback);
    }

    @Override
    public void updateProfileInfo(String baseEntityId, FamilyOtherMemberContract.InteractorCallBack callback) {
        refreshProfileView(baseEntityId, callback);
    }
}