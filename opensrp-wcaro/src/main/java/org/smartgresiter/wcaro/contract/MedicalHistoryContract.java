package org.smartgresiter.wcaro.contract;


import org.smartgresiter.wcaro.util.BaseVaccine;
import org.smartgresiter.wcaro.util.GrowthNutrition;
import org.smartregister.immunization.domain.Vaccine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MedicalHistoryContract {
    interface View {
        Presenter initializePresenter();
        void  updateVaccinationData();
        void  updateGrowthNutrition();
    }
    interface Presenter{
        void setInitialVaccineList(List<Vaccine> veccineList);
        void fetchGrowthNutrition(String baseEntity);
        void initialize();
        ArrayList<BaseVaccine> getVaccineBaseItem();
        ArrayList<GrowthNutrition> getGrowthNutrition();
        MedicalHistoryContract.View getView();
        void onDestroy(boolean isChangingConfiguration);
    }
    interface Interactor{
        void setInitialVaccineList(List<Vaccine> veccineList,InteractorCallBack callBack);
        void fetchGrowthNutritionData(String baseEntity,InteractorCallBack callBack);
        void onDestroy(boolean isChangingConfiguration);
    }
    interface InteractorCallBack{
        void updateVaccineData(ArrayList<BaseVaccine> recievedVaccines);
        void updateGrowthNutrition(ArrayList<GrowthNutrition> growthNutritions);

    }
}
