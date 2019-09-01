package org.smartregister.brac.hnpp.presenter;

import org.smartregister.chw.core.contract.FamilyRemoveMemberContract;
import org.smartregister.chw.core.presenter.CoreFamilyRemoveMemberPresenter;
import org.smartregister.brac.hnpp.interactor.FamilyRemoveMemberInteractor;


public class FamilyRemoveMemberPresenter extends CoreFamilyRemoveMemberPresenter {
    public FamilyRemoveMemberPresenter(FamilyRemoveMemberContract.View view, FamilyRemoveMemberContract.Model model,
                                       String viewConfigurationIdentifier, String familyBaseEntityId, String familyHead, String primaryCaregiver) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId, familyHead, primaryCaregiver);
        setInteractor(FamilyRemoveMemberInteractor.getInstance());
    }
}