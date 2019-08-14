package org.smartregister.chw.interactor;

import android.support.annotation.VisibleForTesting;

import com.opensrp.chw.core.interactor.CoreFamilyProfileInteractor;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.util.Map;

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
