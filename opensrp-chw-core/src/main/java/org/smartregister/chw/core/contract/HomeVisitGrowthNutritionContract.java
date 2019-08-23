package org.smartregister.chw.core.contract;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.util.Map;

public interface HomeVisitGrowthNutritionContract {

    interface Interactor {

        void parseRecordServiceData(CommonPersonObjectClient commonPersonObjectClient, InteractorCallBack callBack);

        void onDestroy(boolean isChangingConfiguration);

    }

    interface InteractorCallBack {
        void updateGivenRecordVisitData(Map<String, ServiceWrapper> stringServiceWrapperMap);

    }
}
