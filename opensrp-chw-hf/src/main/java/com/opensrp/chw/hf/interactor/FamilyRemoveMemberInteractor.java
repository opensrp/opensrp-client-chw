package com.opensrp.chw.hf.interactor;

import com.opensrp.chw.core.interactor.CoreFamilyRemoveMemberInteractor;
import com.opensrp.chw.hf.HealthFacilityApplication;

public class FamilyRemoveMemberInteractor extends CoreFamilyRemoveMemberInteractor {
    private static FamilyRemoveMemberInteractor instance;

    private FamilyRemoveMemberInteractor() {
        setCoreChwApplication();
    }

    public static FamilyRemoveMemberInteractor getInstance() {
        if (instance == null) {
            instance = new FamilyRemoveMemberInteractor();
        }
        return instance;
    }

    @Override
    protected void setCoreChwApplication() {
        this.coreChwApplication = HealthFacilityApplication.getInstance();
    }

}