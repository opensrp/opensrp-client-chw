package org.smartgresiter.wcaro.contract;


import org.smartgresiter.wcaro.util.BaseService;
import org.smartgresiter.wcaro.util.BaseVaccine;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public interface MedicalHistoryContract {
    interface View {

        Presenter initializePresenter();

        void updateVaccinationData();

        void updateGrowthNutrition();

        void updateBirthCertification();

        void updateObsIllness();

        void updateFullyImmunization(String text);
    }

    interface Presenter {

        void setInitialVaccineList(Map<String, Date> veccineList);

        void fetchGrowthNutrition(String baseEntity);

        void fetchFullyImmunization(String dateOfBirth);

        void fetchBirthAndIllnessData(CommonPersonObjectClient commonPersonObjectClient);

        ArrayList<BaseVaccine> getVaccineBaseItem();

        ArrayList<BaseService> getGrowthNutrition();

        ArrayList<String> getBirthCertification();

        ArrayList<String> getObsIllness();

        MedicalHistoryContract.View getView();

        void onDestroy(boolean isChangingConfiguration);
    }

    interface Interactor {

        void fetchBirthAndIllnessData(CommonPersonObjectClient commonPersonObjectClient, InteractorCallBack callBack);

        void setInitialVaccineList(Map<String, Date> recievedVaccines, InteractorCallBack callBack);

        void fetchGrowthNutritionData(String baseEntity, InteractorCallBack callBack);

        void fetchFullyImmunizationData(String dob, Map<String, Date> recievedVaccines, InteractorCallBack callBack);

        void onDestroy(boolean isChangingConfiguration);
    }

    interface InteractorCallBack {

        void updateBirthCertification(ArrayList<String> birthCertification);

        void updateIllnessData(ArrayList<String> obsIllnessArrayList);

        void updateVaccineData(ArrayList<BaseVaccine> recievedVaccines);

        void updateGrowthNutrition(ArrayList<BaseService> services);

        void updateFullyImmunization(String text);

    }
}
