package org.smartgresiter.wcaro.presenter;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyOtherMemberContract;
import org.smartregister.family.presenter.BaseFamilyOtherMemberProfileActivityPresenter;

public class FamilyOtherMemberActivityPresenter extends BaseFamilyOtherMemberProfileActivityPresenter {

    private String familyBaseEntityId;
    private String familyName;

    public FamilyOtherMemberActivityPresenter(FamilyOtherMemberContract.View view, FamilyOtherMemberContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId, String baseEntityId, String familyHead, String primaryCaregiver, String villageTown, String familyName) {
        super(view, model, viewConfigurationIdentifier, baseEntityId, familyHead, primaryCaregiver, villageTown);
        this.familyBaseEntityId = familyBaseEntityId;
        this.familyName = familyName;
    }

    public String getFamilyBaseEntityId() {
        return familyBaseEntityId;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void startFormForEdit(CommonPersonObjectClient commonPersonObject) {

    }
}
