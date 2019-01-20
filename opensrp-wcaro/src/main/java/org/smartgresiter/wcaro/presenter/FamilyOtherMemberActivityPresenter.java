package org.smartgresiter.wcaro.presenter;

import org.smartregister.family.contract.FamilyOtherMemberContract;
import org.smartregister.family.presenter.BaseFamilyOtherMemberProfileActivityPresenter;

public class FamilyOtherMemberActivityPresenter extends BaseFamilyOtherMemberProfileActivityPresenter {

    public FamilyOtherMemberActivityPresenter(FamilyOtherMemberContract.View view, FamilyOtherMemberContract.Model model, String viewConfigurationIdentifier, String baseEntityId, String familyHead, String primaryCaregiver, String villageTown) {
        super(view, model, viewConfigurationIdentifier, baseEntityId, familyHead, primaryCaregiver, villageTown);
    }

}
