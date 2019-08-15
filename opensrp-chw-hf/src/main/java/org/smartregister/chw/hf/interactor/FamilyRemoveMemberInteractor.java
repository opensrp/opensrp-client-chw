package org.smartregister.chw.hf.interactor;

import org.smartregister.chw.core.interactor.CoreFamilyRemoveMemberInteractor;
import org.smartregister.chw.hf.HealthFacilityApplication;

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