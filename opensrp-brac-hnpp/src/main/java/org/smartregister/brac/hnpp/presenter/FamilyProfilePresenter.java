package org.smartregister.brac.hnpp.presenter;

import org.smartregister.brac.hnpp.model.ChildRegisterModel;
import org.smartregister.chw.core.contract.FamilyProfileExtendedContract;
import org.smartregister.chw.core.model.CoreChildRegisterModel;
import org.smartregister.chw.core.presenter.CoreFamilyProfilePresenter;
import org.smartregister.brac.hnpp.interactor.HnppFamilyProfileInteractor;
import org.smartregister.family.contract.FamilyProfileContract;

public class FamilyProfilePresenter extends CoreFamilyProfilePresenter {

    public FamilyProfilePresenter(FamilyProfileExtendedContract.View loginView, FamilyProfileContract.Model model, String familyBaseEntityId, String familyHead, String primaryCaregiver, String familyName) {
        super(loginView, model, familyBaseEntityId, familyHead, primaryCaregiver, familyName);
        interactor = new HnppFamilyProfileInteractor();
        verifyHasPhone();
    }

    @Override
    protected CoreChildRegisterModel getChildRegisterModel() {
        return new ChildRegisterModel();
    }

}
