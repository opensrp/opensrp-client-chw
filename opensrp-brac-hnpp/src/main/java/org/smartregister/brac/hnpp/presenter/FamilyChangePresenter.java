package org.smartregister.brac.hnpp.presenter;

import org.smartregister.chw.core.contract.FamilyChangeContract;
import org.smartregister.chw.core.presenter.CoreFamilyChangePresenter;
import org.smartregister.brac.hnpp.interactor.FamilyChangeContractInteractor;


public class FamilyChangePresenter extends CoreFamilyChangePresenter {
    public FamilyChangePresenter(FamilyChangeContract.View view, String familyID) {
        super(view, familyID);
        this.interactor = new FamilyChangeContractInteractor();
    }
}
