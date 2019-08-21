package org.smartregister.chw.presenter;

import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.contract.ChildMedicalHistoryContract;
import org.smartregister.chw.interactor.ChildMedicalHistoryInteractor;
import org.smartregister.chw.util.BaseService;
import org.smartregister.chw.util.BaseVaccine;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.AppExecutors;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class ChildMedicalHistoryPresenter implements ChildMedicalHistoryContract.Presenter, ChildMedicalHistoryContract.InteractorCallBack {
    private WeakReference<ChildMedicalHistoryContract.View> view;
    private ChildMedicalHistoryContract.Interactor interactor;
    private Map<String, Date> recievedVaccines;
    private ArrayList<BaseVaccine> baseVaccineArrayList;
    private ArrayList<BaseService> growthNutritionArrayList, dietaryArrayList, muacArrayList, llitnDataArrayList, ecdDataArrayList;
    private ArrayList<String> birthCertifications;
    private ArrayList<String> obsIllnesses;


    public ChildMedicalHistoryPresenter(ChildMedicalHistoryContract.View view, AppExecutors appExecutors, VisitRepository visitRepository) {
        this.view = new WeakReference<>(view);
        interactor = new ChildMedicalHistoryInteractor(appExecutors, visitRepository, getView().getContext());
    }

//    @Override
//    public void generateHomeVisitServiceList(long homeVisitDate) {
//        interactor.generateHomeVisitServiceList(homeVisitDate);
//    }

    @Override
    public void setInitialVaccineList(Map<String, Date> veccineList) {
        recievedVaccines = veccineList;
        interactor.setInitialVaccineList(recievedVaccines, this);
    }

    @Override
    public void fetchFullyImmunization(String dateOfBirth) {
        interactor.fetchFullyImmunizationData(dateOfBirth, recievedVaccines, this);
    }

    @Override
    public void fetchGrowthNutrition(CommonPersonObjectClient commonPersonObjectClient) {
        interactor.fetchGrowthNutritionData(commonPersonObjectClient, this);
    }

    @Override
    public void fetchDietaryData(CommonPersonObjectClient commonPersonObjectClient) {
        interactor.fetchDietaryData(commonPersonObjectClient, this);
    }

    @Override
    public void fetchMuacData(CommonPersonObjectClient commonPersonObjectClient) {
        interactor.fetchMuacData(commonPersonObjectClient, this);
    }

    @Override
    public void fetchLLitnData(CommonPersonObjectClient commonPersonObjectClient) {
        interactor.fetchLLitnData(commonPersonObjectClient, this);
    }

    @Override
    public void fetchEcdData(CommonPersonObjectClient commonPersonObjectClient) {
        interactor.fetchEcdData(commonPersonObjectClient, this);
    }

    @Override
    public void fetchBirthData(CommonPersonObjectClient commonPersonObjectClient) {
        interactor.fetchBirthCertificateData(commonPersonObjectClient, this);
    }

    @Override
    public void fetchIllnessData(CommonPersonObjectClient commonPersonObjectClient) {
        interactor.fetchIllnessData(commonPersonObjectClient, this);
    }

    @Override
    public ArrayList<BaseVaccine> getVaccineBaseItem() {
        return baseVaccineArrayList;
    }

    @Override
    public ArrayList<BaseService> getGrowthNutrition() {
        return growthNutritionArrayList;
    }

    @Override
    public ArrayList<BaseService> getDietaryList() {
        return dietaryArrayList;
    }

    @Override
    public ArrayList<BaseService> getMuacList() {
        return muacArrayList;
    }

    @Override
    public ArrayList<BaseService> getLlitnList() {
        return llitnDataArrayList;
    }

    @Override
    public ArrayList<BaseService> getEcdList() {
        return ecdDataArrayList;
    }

    @Override
    public ArrayList<String> getBirthCertification() {
        return birthCertifications;
    }

    @Override
    public ArrayList<String> getObsIllness() {
        return obsIllnesses;
    }

    @Override
    public ChildMedicalHistoryContract.View getView() {
        return (view != null) ? view.get() : null;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        view = null;//set to null on destroy

        // Inform interactor
        interactor.onDestroy(isChangingConfiguration);

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            interactor = null;
        }
    }

    @Override
    public void updateBirthCertification(ArrayList<String> birthCertification) {
        this.birthCertifications = birthCertification;
        getView().updateBirthCertification();

    }

    @Override
    public void updateIllnessData(ArrayList<String> obsIllnessArrayList) {
        this.obsIllnesses = obsIllnessArrayList;
        getView().updateObsIllness();

    }

    @Override
    public void updateVaccineData(ArrayList<BaseVaccine> baseVaccines) {
        this.baseVaccineArrayList = baseVaccines;
        getView().updateVaccinationData();

    }

    @Override
    public void updateGrowthNutrition(ArrayList<BaseService> growthNutritions) {
        this.growthNutritionArrayList = growthNutritions;
        getView().updateGrowthNutrition();

    }

    @Override
    public void updateDietaryData(ArrayList<BaseService> services) {
        this.dietaryArrayList = services;
        getView().updateDietaryData();

    }

    @Override
    public void updateMuacData(ArrayList<BaseService> services) {
        this.muacArrayList = services;
        getView().updateMuacData();
    }

    @Override
    public void updateLLitnDataData(ArrayList<BaseService> services) {
        this.llitnDataArrayList = services;
        getView().updateLLitnData();

    }

    @Override
    public void updateEcdDataData(ArrayList<BaseService> services) {
        this.ecdDataArrayList = services;
        getView().updateEcdData();

    }

    @Override
    public void updateFullyImmunization(String text) {
        getView().updateFullyImmunization(text);
    }

    @Override
    public void updateVaccineCard(String value) {
        getView().updateVaccineCard(value);
    }

}
