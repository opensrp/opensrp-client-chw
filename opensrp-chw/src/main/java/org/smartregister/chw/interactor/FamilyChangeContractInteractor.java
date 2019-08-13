package org.smartregister.chw.interactor;

import org.smartregister.chw.core.interactor.CoreFamilyChangeContractInteractor;

import org.smartregister.chw.application.ChwApplication;

public class FamilyChangeContractInteractor extends CoreFamilyChangeContractInteractor {

    public FamilyChangeContractInteractor() {
        setCoreChwApplication();
        setFlavour();
    }

    @Override
    protected void setCoreChwApplication() {
        this.coreChwApplication = ChwApplication.getInstance();
    }

    @Override
    protected void setFlavour() {
        this.flavor = new FamilyChangeContractInteractorFlv();
    }
}
