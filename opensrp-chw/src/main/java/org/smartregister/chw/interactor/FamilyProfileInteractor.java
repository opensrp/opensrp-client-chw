package org.smartregister.chw.interactor;

import android.support.annotation.VisibleForTesting;

import org.smartregister.chw.core.interactor.CoreFamilyProfileInteractor;
import org.smartregister.family.util.AppExecutors;

public class FamilyProfileInteractor extends CoreFamilyProfileInteractor {

    @VisibleForTesting
    FamilyProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public FamilyProfileInteractor() {
        super();
    }

}
