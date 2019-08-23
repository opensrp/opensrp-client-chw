package org.smartregister.chw.core.presenter;

import org.smartregister.family.contract.FamilyOtherMemberProfileFragmentContract;
import org.smartregister.family.presenter.BaseFamilyOtherMemberProfileFragmentPresenter;
import org.smartregister.family.util.DBConstants;

public abstract class CoreFamilyOtherMemberProfileFragmentPresenter extends BaseFamilyOtherMemberProfileFragmentPresenter {
    private String baseEntityId;

    public CoreFamilyOtherMemberProfileFragmentPresenter(FamilyOtherMemberProfileFragmentContract.View view, FamilyOtherMemberProfileFragmentContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId, String baseEntityId) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId);
        this.baseEntityId = baseEntityId;
    }

    @Override
    public String getMainCondition() {
        return String.format(" %s = '%s' and %s is null ", DBConstants.KEY.OBJECT_ID, baseEntityId, DBConstants.KEY.DATE_REMOVED);
    }
}
