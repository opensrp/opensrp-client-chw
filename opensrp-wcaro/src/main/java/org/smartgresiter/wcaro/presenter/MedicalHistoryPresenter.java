package org.smartgresiter.wcaro.presenter;

import org.smartgresiter.wcaro.contract.MedicalHistoryContract;
import org.smartgresiter.wcaro.interactor.MedicalHistoryInteractor;
import org.smartgresiter.wcaro.util.BaseService;
import org.smartgresiter.wcaro.util.BaseVaccine;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MedicalHistoryPresenter implements MedicalHistoryContract.Presenter, MedicalHistoryContract.InteractorCallBack {
    private WeakReference<MedicalHistoryContract.View> view;
    private MedicalHistoryContract.Interactor interactor;
    private Map<String, Date> recievedVaccines;
    private ArrayList<BaseVaccine> baseVaccineArrayList;
    private ArrayList<BaseService> growthNutritionArrayList;

    public MedicalHistoryPresenter(MedicalHistoryContract.View view) {
        this.view = new WeakReference<>(view);
        interactor = new MedicalHistoryInteractor();
    }

    @Override
    public void setInitialVaccineList(Map<String, Date> veccineList) {
        recievedVaccines = veccineList;
        interactor.setInitialVaccineList(recievedVaccines, this);
    }

    @Override
    public void fetchGrowthNutrition(String baseEntity) {
        interactor.fetchGrowthNutritionData(baseEntity, this);
    }

    @Override
    public void fetchFullyImmunization(String dateOfBirth) {
        interactor.fetchFullyImmunizationData(dateOfBirth, recievedVaccines, this);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void updateFullyImmunization(String text) {
        getView().updateFullyImmunization(text);
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
    public MedicalHistoryContract.View getView() {
        if (view != null) {
            return view.get();
        } else {
            return null;
        }
    }

}
