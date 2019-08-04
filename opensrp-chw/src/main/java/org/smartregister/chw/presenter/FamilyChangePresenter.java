package org.smartregister.chw.presenter;

import com.opensrp.chw.core.contract.FamilyChangeContract;
import com.opensrp.chw.core.presenter.CoreFamilyChangePresenter;

import org.smartregister.chw.interactor.FamilyChangeContractInteractor;

public class FamilyChangePresenter extends CoreFamilyChangePresenter {
    public FamilyChangePresenter(FamilyChangeContract.View view, String familyID) {
        super(view, familyID);
        this.interactor = new FamilyChangeContractInteractor();
    }
}
