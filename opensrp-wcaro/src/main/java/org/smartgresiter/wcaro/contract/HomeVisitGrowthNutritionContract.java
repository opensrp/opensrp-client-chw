package org.smartgresiter.wcaro.contract;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.util.Map;

public interface HomeVisitGrowthNutritionContract {
    interface View {
        Presenter initializePresenter();

        void updateExclusiveFeedingData(String name);

        void updateMnpData(String name);

        void updateVitaminAData(String name);

        void updateDewormingData(String name);

        void statusImageViewUpdate(String type, boolean value);

    }

    interface Presenter {
        void parseRecordServiceData(CommonPersonObjectClient commonPersonObjectClient);

        void setSaveState(String type, ServiceWrapper serviceWrapper);

        void serNotVisitState(String type, ServiceWrapper serviceWrapper);

        void resetAllSaveState();

        boolean isAllSelected();

        boolean isSelected(String type);

        HomeVisitGrowthNutritionContract.View getView();

        void onDestroy(boolean isChangingConfiguration);

        Map<String, ServiceWrapper> getSaveStateMap();

        void setSaveStateMap(Map<String, ServiceWrapper> saveStateMap);
    }

    interface Interactor {
        void parseRecordServiceData(CommonPersonObjectClient commonPersonObjectClient, InteractorCallBack callBack);

        void onDestroy(boolean isChangingConfiguration);

    }

    interface InteractorCallBack {
        void updateRecordVisitData(Map<String, ServiceWrapper> stringServiceWrapperMap);

    }
}
