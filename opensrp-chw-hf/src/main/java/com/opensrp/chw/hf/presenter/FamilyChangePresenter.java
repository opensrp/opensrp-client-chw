package com.opensrp.chw.hf.presenter;

import com.opensrp.chw.core.contract.FamilyChangeContract;
import com.opensrp.chw.core.presenter.CoreFamilyChangePresenter;
import com.opensrp.chw.hf.interactor.FamilyChangeContractInteractor;


public class FamilyChangePresenter extends CoreFamilyChangePresenter {
    public FamilyChangePresenter(FamilyChangeContract.View view, String familyID) {
        super(view, familyID);
        this.interactor = new FamilyChangeContractInteractor();
    }
}
