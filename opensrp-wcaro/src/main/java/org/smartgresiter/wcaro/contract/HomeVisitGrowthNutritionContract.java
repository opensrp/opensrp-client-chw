package org.smartgresiter.wcaro.contract;

import android.content.Context;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.util.Map;

public interface HomeVisitGrowthNutritionContract {
    interface View {
        Presenter initializePresenter();

        void updateExclusiveFeedingData(String name,String dueDate);

        void updateMnpData(String name,String dueDate);

        void updateVitaminAData(String name,String dueDate);

        void updateDewormingData(String name,String dueDate);

        void statusImageViewUpdate(String type, boolean value,String message,String yesNoValue);

        Context getViewContext();

    }

    interface Presenter {
        void parseRecordServiceData(CommonPersonObjectClient commonPersonObjectClient);

        void setSaveState(String type, ServiceWrapper serviceWrapper);

        void setNotVisitState(String type, ServiceWrapper serviceWrapper);

        void resetAllSaveState();

        boolean isAllSelected();

        boolean isSelected(String type);

        HomeVisitGrowthNutritionContract.View getView();

        void onDestroy(boolean isChangingConfiguration);

        Map<String, String> getSaveStateMap();

    }

    interface Interactor {
        void parseRecordServiceData(CommonPersonObjectClient commonPersonObjectClient, InteractorCallBack callBack);

        void onDestroy(boolean isChangingConfiguration);

    }

    interface InteractorCallBack {
        void updateRecordVisitData(Map<String, ServiceWrapper> stringServiceWrapperMap);

    }
}
