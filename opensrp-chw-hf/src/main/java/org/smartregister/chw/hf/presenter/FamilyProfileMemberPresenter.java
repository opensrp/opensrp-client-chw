package org.smartregister.chw.hf.presenter;

import org.smartregister.chw.core.presenter.CoreFamilyProfileMemberPresenter;
import org.smartregister.family.contract.FamilyProfileMemberContract;

public class FamilyProfileMemberPresenter extends CoreFamilyProfileMemberPresenter {

    public FamilyProfileMemberPresenter(FamilyProfileMemberContract.View view, FamilyProfileMemberContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId, String familyHead, String primaryCaregiver) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId, familyHead, primaryCaregiver);
    }
}
