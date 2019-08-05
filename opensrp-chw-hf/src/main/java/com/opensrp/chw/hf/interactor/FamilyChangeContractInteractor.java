package com.opensrp.chw.hf.interactor;

import com.opensrp.chw.core.interactor.CoreFamilyChangeContractInteractor;
import com.opensrp.chw.hf.HealthFacilityApplication;


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
