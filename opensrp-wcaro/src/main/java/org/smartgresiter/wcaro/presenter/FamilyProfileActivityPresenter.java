package org.smartgresiter.wcaro.presenter;

import org.smartregister.family.contract.FamilyProfileActivityContract;
import org.smartregister.family.presenter.BaseFamilyProfileActivityPresenter;

public class FamilyProfileActivityPresenter extends BaseFamilyProfileActivityPresenter {

    public FamilyProfileActivityPresenter(FamilyProfileActivityContract.View view, FamilyProfileActivityContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId);
    }
}
