package org.smartgresiter.wcaro.interactor;

import android.support.annotation.VisibleForTesting;

import org.apache.commons.lang3.text.WordUtils;
import org.smartgresiter.wcaro.contract.MedicalHistoryContract;
import org.smartgresiter.wcaro.util.BaseVaccine;
import org.smartgresiter.wcaro.util.ChildUtils;
import org.smartgresiter.wcaro.util.GrowthNutrition;
import org.smartgresiter.wcaro.util.ReceivedVaccine;
import org.smartgresiter.wcaro.util.VaccineContent;
import org.smartgresiter.wcaro.util.VaccineHeader;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.util.VaccinateActionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MedicalHistoryInteractor implements MedicalHistoryContract.Interactor {
    private AppExecutors appExecutors;
    @VisibleForTesting
    MedicalHistoryInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }
    public MedicalHistoryInteractor(){
        this(new AppExecutors());
    }

    @Override
    public void fetchFullyImmunizationData(String dob,Map<String, Date> recievedVaccines,final MedicalHistoryContract.InteractorCallBack callBack) {
       String dobString = dob.contains("y") ? dob.substring(0, dob.indexOf("y")) : dob;
       int year=0;
       try {
           year=Integer.parseInt(dobString);
       }catch (Exception e){

       }
       List<String> vacList=new ArrayList<>();
       for(String name:recievedVaccines.keySet()){
           String trimLower=name.replace(" ","").toLowerCase();
           vacList.add(trimLower);
       }
        final String fullyImmunizationText=ChildUtils.isFullyImmunized(year,vacList);
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.updateFullyImmunization(fullyImmunizationText);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void setInitialVaccineList(Map<String, Date> recievedVaccines, final MedicalHistoryContract.InteractorCallBack callBack) {

        ArrayList<ReceivedVaccine> receivedVaccineArrayList=new ArrayList<>();
        final ArrayList<BaseVaccine> baseVaccineArrayList=new ArrayList<>();
        List<VaccineRepo.Vaccine> vList = Arrays.asList(VaccineRepo.Vaccine.values());
        for(String name:recievedVaccines.keySet()){
            for(VaccineRepo.Vaccine vaccine:vList){
                if(name.equalsIgnoreCase(vaccine.display())){
                    String stateKey = VaccinateActionUtils.stateKey(vaccine);
                    ReceivedVaccine receivedVaccine=new ReceivedVaccine();
                    receivedVaccine.setVaccineCategory(stateKey);
                    receivedVaccine.setVaccineName(name);
                    receivedVaccine.setVaccineDate(recievedVaccines.get(name));
                    receivedVaccineArrayList.add(receivedVaccine);
                }
            }
        }

        String lastCategory="";
        for(ReceivedVaccine receivedVaccine:receivedVaccineArrayList){
            if(!receivedVaccine.getVaccineCategory().equalsIgnoreCase(lastCategory)){
                VaccineHeader vaccineHeader=new VaccineHeader();
                if(receivedVaccine.getVaccineCategory().trim().equalsIgnoreCase("at birth")){
                    receivedVaccine.setVaccineCategory("Birth");
                }
                lastCategory=receivedVaccine.getVaccineCategory();
                vaccineHeader.setVaccineHeaderName(receivedVaccine.getVaccineCategory());
                baseVaccineArrayList.add(vaccineHeader);
                VaccineContent content=new VaccineContent();
                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
                String date = DATE_FORMAT.format(receivedVaccine.getVaccineDate());
                content.setVaccineDate(date);
                content.setVaccineName(receivedVaccine.getVaccineName());
                baseVaccineArrayList.add(content);
            }else{
                VaccineContent content=new VaccineContent();
                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
                String date = DATE_FORMAT.format(receivedVaccine.getVaccineDate());
                content.setVaccineDate(date);
                content.setVaccineName(receivedVaccine.getVaccineName());
                baseVaccineArrayList.add(content);
            }
        }
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.updateVaccineData(baseVaccineArrayList);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void fetchGrowthNutritionData(String baseEntity,final MedicalHistoryContract.InteractorCallBack callBack) {
        final ArrayList<GrowthNutrition> growthNutritions=new ArrayList<>();
//        GrowthNutrition growthNutrition=new GrowthNutrition();
//        growthNutrition.setGrowthName("Early brestfeeding");
//        growthNutrition.setStatus("No");
//        growthNutritions.add(growthNutrition);
//        GrowthNutrition growthNutrition2=new GrowthNutrition();
//        growthNutrition2.setGrowthName("Early brestfeeding (1m)");
//        growthNutrition2.setStatus("Yes");
//        growthNutritions.add(growthNutrition2);
//        GrowthNutrition growthNutrition3=new GrowthNutrition();
//        growthNutrition3.setGrowthName("Early brestfeeding (3m)");
//        growthNutrition3.setStatus("No");
//        growthNutritions.add(growthNutrition3);
//        GrowthNutrition growthNutrition4=new GrowthNutrition();
//        growthNutrition4.setGrowthName("Early brestfeeding (4m)");
//        growthNutrition4.setStatus("No");
//        growthNutritions.add(growthNutrition4);
//        GrowthNutrition growthNutrition5=new GrowthNutrition();
//        growthNutrition5.setGrowthName("Early brestfeeding (5m)");
//        growthNutrition5.setStatus("No");
//        growthNutritions.add(growthNutrition5);
//        GrowthNutrition growthNutrition6=new GrowthNutrition();
//        growthNutrition6.setGrowthName("Early brestfeeding (6m)");
//        growthNutrition6.setStatus("yes");
//        growthNutritions.add(growthNutrition6);
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.updateGrowthNutrition(growthNutritions);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

    }
}
