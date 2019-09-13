package org.smartregister.chw.core.presenter;

import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.contract.FamilyProfileMemberContract;
import org.smartregister.family.presenter.BaseFamilyProfileMemberPresenter;
import org.smartregister.family.util.DBConstants;

public class CoreFamilyProfileMemberPresenter extends BaseFamilyProfileMemberPresenter {

    public CoreFamilyProfileMemberPresenter(FamilyProfileMemberContract.View view, FamilyProfileMemberContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId, String familyHead, String primaryCaregiver) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId, familyHead, primaryCaregiver);
    }

}
