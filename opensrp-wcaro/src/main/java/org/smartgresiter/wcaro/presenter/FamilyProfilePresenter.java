package org.smartgresiter.wcaro.presenter;

import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.presenter.BaseFamilyProfilePresenter;

public class FamilyProfilePresenter extends BaseFamilyProfilePresenter {

    public FamilyProfilePresenter(FamilyProfileContract.View loginView, FamilyProfileContract.Model model, String familyBaseEntityId) {
        super(loginView, model, familyBaseEntityId);
    }

}
