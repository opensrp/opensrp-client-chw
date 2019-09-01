package org.smartregister.brac.hnpp.interactor;

import org.smartregister.brac.hnpp.HnppApplication;
import org.smartregister.chw.core.interactor.CoreFamilyRemoveMemberInteractor;

public class FamilyRemoveMemberInteractor extends CoreFamilyRemoveMemberInteractor {
    private static FamilyRemoveMemberInteractor instance;

    private FamilyRemoveMemberInteractor() {
        setCoreChwApplication();
    }

    @Override
    protected void setCoreChwApplication() {
        this.coreChwApplication = HnppApplication.getInstance();
    }

    public static FamilyRemoveMemberInteractor getInstance() {
        if (instance == null) {
            instance = new FamilyRemoveMemberInteractor();
        }
        return instance;
    }

}