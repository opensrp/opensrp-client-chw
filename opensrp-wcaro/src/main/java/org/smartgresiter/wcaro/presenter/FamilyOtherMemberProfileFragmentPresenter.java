package org.smartgresiter.wcaro.presenter;

import org.smartregister.family.contract.FamilyOtherMemberProfileFragmentContract;
import org.smartregister.family.presenter.BaseFamilyOtherMemberProfileFragmentPresenter;

public class FamilyOtherMemberProfileFragmentPresenter extends BaseFamilyOtherMemberProfileFragmentPresenter {

    public FamilyOtherMemberProfileFragmentPresenter(FamilyOtherMemberProfileFragmentContract.View view, FamilyOtherMemberProfileFragmentContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId);
    }
}
