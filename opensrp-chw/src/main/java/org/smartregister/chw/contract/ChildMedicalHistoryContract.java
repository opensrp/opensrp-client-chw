package org.smartregister.chw.contract;


import android.content.Context;

import org.smartregister.chw.util.BaseService;
import org.smartregister.chw.util.BaseVaccine;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public interface ChildMedicalHistoryContract {
    interface View {

        Presenter initializePresenter();

        void updateVaccinationData();

        void updateGrowthNutrition();

        void updateBirthCertification();

        void updateObsIllness();

        void updateFullyImmunization(String text);

        void updateVaccineCard(String value);

        void updateDietaryData();

        void updateMuacData();

        void updateLLitnData();

        void updateEcdData();

        Context getContext();
    }

    interface Presenter {

        //void generateHomeVisitServiceList(long homeVisitDate);

        void setInitialVaccineList(Map<String, Date> veccineList);

        void fetchFullyImmunization(String dateOfBirth);

        void fetchGrowthNutrition(CommonPersonObjectClient commonPersonObjectClient);

        void fetchDietaryData(CommonPersonObjectClient commonPersonObjectClient);

        void fetchMuacData(CommonPersonObjectClient commonPersonObjectClient);

        void fetchLLitnData(CommonPersonObjectClient commonPersonObjectClient);

        void fetchEcdData(CommonPersonObjectClient commonPersonObjectClient);

        void fetchBirthData(CommonPersonObjectClient commonPersonObjectClient);

        void fetchIllnessData(CommonPersonObjectClient commonPersonObjectClient);

        ArrayList<BaseVaccine> getVaccineBaseItem();

        ArrayList<BaseService> getGrowthNutrition();

        ArrayList<BaseService> getDietaryList();

        ArrayList<BaseService> getMuacList();

        ArrayList<BaseService> getLlitnList();

        ArrayList<BaseService> getEcdList();

        ArrayList<String> getBirthCertification();

        ArrayList<String> getObsIllness();

        ChildMedicalHistoryContract.View getView();

        void onDestroy(boolean isChangingConfiguration);
    }

    interface Interactor {

        //void generateHomeVisitServiceList(long homeVisitDate);

        void fetchBirthCertificateData(CommonPersonObjectClient commonPersonObjectClient, InteractorCallBack callBack);

        void fetchIllnessData(CommonPersonObjectClient commonPersonObjectClient, InteractorCallBack callBack);

        void setInitialVaccineList(Map<String, Date> recievedVaccines, InteractorCallBack callBack);

        void fetchGrowthNutritionData(CommonPersonObjectClient commonPersonObjectClient, InteractorCallBack callBack);

        void fetchDietaryData(CommonPersonObjectClient commonPersonObjectClient, InteractorCallBack callBack);

        void fetchMuacData(CommonPersonObjectClient commonPersonObjectClient, InteractorCallBack callBack);

        void fetchLLitnData(CommonPersonObjectClient commonPersonObjectClient, InteractorCallBack callBack);

        void fetchEcdData(CommonPersonObjectClient commonPersonObjectClient, InteractorCallBack callBack);

        void fetchFullyImmunizationData(String dob, Map<String, Date> recievedVaccines, InteractorCallBack callBack);

        void onDestroy(boolean isChangingConfiguration);
    }

    interface InteractorCallBack {

        void updateBirthCertification(ArrayList<String> birthCertification);

        void updateIllnessData(ArrayList<String> obsIllnessArrayList);

        void updateVaccineData(ArrayList<BaseVaccine> recievedVaccines);

        void updateGrowthNutrition(ArrayList<BaseService> services);

        void updateDietaryData(ArrayList<BaseService> services);

        void updateMuacData(ArrayList<BaseService> services);

        void updateLLitnDataData(ArrayList<BaseService> services);

        void updateEcdDataData(ArrayList<BaseService> services);

        void updateFullyImmunization(String text);

        void updateVaccineCard(String value);

    }
}
