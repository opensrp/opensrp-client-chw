package org.smartregister.chw.contract;


import org.smartregister.chw.util.BaseService;
import org.smartregister.chw.util.BaseVaccine;
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

        void updateVaccineCard(String value);
    }

    interface Presenter {

        void generateHomeVisitServiceList(long homeVisitDate);

        void setInitialVaccineList(Map<String, Date> veccineList);

        void fetchGrowthNutrition(CommonPersonObjectClient commonPersonObjectClient);

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

        void generateHomeVisitServiceList(long homeVisitDate);

        void fetchBirthAndIllnessData(CommonPersonObjectClient commonPersonObjectClient, InteractorCallBack callBack);

        void setInitialVaccineList(Map<String, Date> recievedVaccines, InteractorCallBack callBack);

        void fetchGrowthNutritionData(CommonPersonObjectClient commonPersonObjectClient,InteractorCallBack callBack);

        void fetchFullyImmunizationData(String dob, Map<String, Date> recievedVaccines, InteractorCallBack callBack);

        void onDestroy(boolean isChangingConfiguration);
    }

    interface InteractorCallBack {

        void updateBirthCertification(ArrayList<String> birthCertification);

        void updateIllnessData(ArrayList<String> obsIllnessArrayList);

        void updateVaccineData(ArrayList<BaseVaccine> recievedVaccines);

        void updateGrowthNutrition(ArrayList<BaseService> services);

        void updateFullyImmunization(String text);

        void updateVaccineCard(String value);

    }
}
