package org.smartregister.brac.hnpp.interactor;

import org.smartregister.brac.hnpp.HnppApplication;
import org.smartregister.chw.core.interactor.CoreFamilyChangeContractInteractor;


public class FamilyChangeContractInteractor extends CoreFamilyChangeContractInteractor {

    public FamilyChangeContractInteractor() {
        setCoreChwApplication();
        setFlavour();
    }

    @Override
    protected void setCoreChwApplication() {
        this.coreChwApplication = HnppApplication.getInstance();
    }

    @Override
    protected void setFlavour() {
        this.flavor = new HfFamilyChangeContractInteractor();
    }
}
