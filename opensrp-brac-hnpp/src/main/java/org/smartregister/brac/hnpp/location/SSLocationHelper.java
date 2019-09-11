package org.smartregister.brac.hnpp.location;

import org.smartregister.brac.hnpp.HnppApplication;

import java.util.ArrayList;
import java.util.List;

public class SSLocationHelper {

    private static SSLocationHelper instance;

    private ArrayList<SSLocationForm> ssLocationForms = new ArrayList<>();
    private SSLocationHelper(){
        setSsLocationForms();
    }
    public static SSLocationHelper getInstance(){
        if(instance == null){
            instance = new SSLocationHelper();
        }
        return instance;
    }
    public ArrayList<SSLocationForm> getSsLocationForms() {
        if(ssLocationForms != null && ssLocationForms.isEmpty()){
            setSsLocationForms();
        }
        return ssLocationForms;
    }

    private void setSsLocationForms(){
        List<SSModel> ssModels =  HnppApplication.getSSLocationRepository().getAllLocations();
        for(SSModel ssModel : ssModels){
            for (SSLocations ssLocations:ssModel.locations){
                SSLocationForm ssLocationForm = new SSLocationForm();
                ssLocationForm.name = ssModel.username;
                ssLocationForm.locations = ssLocations;
                ssLocationForms.add(ssLocationForm);
            }
        }

    }

    public String generateHouseHoldId(SSLocations ssLocations,String lastFourDigit){
        return  ssLocations.division.code+""+ssLocations.district.code+""+ssLocations.city_corporation_upazila.code+""
                +ssLocations.pourosava.code+""+ssLocations.union_ward.code+""+ssLocations.mouza.code+""
                +ssLocations.village.code+""+lastFourDigit;
    }

}
