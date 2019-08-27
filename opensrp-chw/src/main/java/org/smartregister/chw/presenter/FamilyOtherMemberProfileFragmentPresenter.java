package org.smartregister.chw.presenter;

import org.smartregister.chw.core.presenter.CoreFamilyOtherMemberProfileFragmentPresenter;
import org.smartregister.family.contract.FamilyOtherMemberProfileFragmentContract;

public class FamilyOtherMemberProfileFragmentPresenter extends CoreFamilyOtherMemberProfileFragmentPresenter {
    public FamilyOtherMemberProfileFragmentPresenter(FamilyOtherMemberProfileFragmentContract.View view, FamilyOtherMemberProfileFragmentContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId, String baseEntityId) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId, baseEntityId);
    }
}