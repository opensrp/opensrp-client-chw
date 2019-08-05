package com.opensrp.chw.hf.presenter;

import com.opensrp.chw.core.contract.FamilyProfileExtendedContract;
import com.opensrp.chw.core.model.CoreChildRegisterModel;
import com.opensrp.chw.core.presenter.CoreFamilyProfilePresenter;
import com.opensrp.chw.hf.interactor.FamilyProfileInteractor;
import com.opensrp.chw.hf.model.ChildRegisterModel;

import org.smartregister.family.contract.FamilyProfileContract;

public class FamilyProfilePresenter extends CoreFamilyProfilePresenter{

    public FamilyProfilePresenter(FamilyProfileExtendedContract.View loginView, FamilyProfileContract.Model model, String familyBaseEntityId, String familyHead, String primaryCaregiver, String familyName) {
        super(loginView, model, familyBaseEntityId, familyHead, primaryCaregiver, familyName);
        interactor = new FamilyProfileInteractor();
        verifyHasPhone();
    }

    @Override
    protected CoreChildRegisterModel getChildRegisterModel() {
        return new ChildRegisterModel();
    }
}
