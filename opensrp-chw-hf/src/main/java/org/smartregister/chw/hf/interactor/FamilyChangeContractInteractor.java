package org.smartregister.chw.hf.interactor;

import org.smartregister.chw.core.interactor.CoreFamilyChangeContractInteractor;
import org.smartregister.chw.hf.HealthFacilityApplication;


public class FamilyChangeContractInteractor extends CoreFamilyChangeContractInteractor {

    public FamilyChangeContractInteractor() {
        setCoreChwApplication();
        setFlavour();
    }

    @Override
    protected void setCoreChwApplication() {
        this.coreChwApplication = HealthFacilityApplication.getInstance();
    }

    @Override
    protected void setFlavour() {
        this.flavor = new HfFamilyChangeContractInteractor();
    }
}
