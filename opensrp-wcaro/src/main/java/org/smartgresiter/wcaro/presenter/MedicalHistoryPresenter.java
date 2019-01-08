package org.smartgresiter.wcaro.presenter;

import android.util.ArrayMap;

import org.smartgresiter.wcaro.contract.MedicalHistoryContract;
import org.smartgresiter.wcaro.interactor.MedicalHistoryInteractor;
import org.smartgresiter.wcaro.util.BaseVaccine;
import org.smartgresiter.wcaro.util.GrowthNutrition;
import org.smartgresiter.wcaro.util.ReceivedVaccine;
import org.smartgresiter.wcaro.util.VaccineContent;
import org.smartgresiter.wcaro.util.VaccineHeader;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.util.DateUtil;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MedicalHistoryPresenter implements MedicalHistoryContract.Presenter,MedicalHistoryContract.InteractorCallBack {
    private WeakReference<MedicalHistoryContract.View> view;
    private MedicalHistoryContract.Interactor interactor;
    private ArrayList<BaseVaccine> baseVaccineArrayList;
    private ArrayList<GrowthNutrition> growthNutritionArrayList;

    public MedicalHistoryPresenter(MedicalHistoryContract.View view){
        this.view=new WeakReference<>(view);
        interactor=new MedicalHistoryInteractor();
    }

    @Override
    public void setInitialVaccineList(List<Vaccine> veccineList) {
        interactor.setInitialVaccineList(veccineList,this);
    }

    @Override
    public void fetchGrowthNutrition(String baseEntity) {
        interactor.fetchGrowthNutritionData(baseEntity,this);
    }

    @Override
    public void initialize() {

    }

    @Override
    public ArrayList<BaseVaccine> getVaccineBaseItem() {
        return baseVaccineArrayList;
    }

    @Override
    public ArrayList<GrowthNutrition> getGrowthNutrition() {
        return growthNutritionArrayList;
    }

    @Override
    public void updateVaccineData(ArrayList<BaseVaccine> baseVaccines) {
        this.baseVaccineArrayList=baseVaccines;

        getView().updateVaccinationData();

    }

    @Override
    public void updateGrowthNutrition(ArrayList<GrowthNutrition> growthNutritions) {
        this.growthNutritionArrayList=growthNutritions;
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
