package org.smartregister.brac.hnpp.location;

import org.smartregister.brac.hnpp.HnppApplication;
import org.smartregister.clientandeventmodel.Address;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SSLocationHelper {

    private static SSLocationHelper instance;

    //private ArrayList<SSLocationForm> ssLocationForms = new ArrayList<>();
    private ArrayList<SSModel> ssModels  = new ArrayList<>();
    private SSLocationHelper(){
        setSsLocationForms();
    }
    public static SSLocationHelper getInstance(){
        if(instance == null){
            instance = new SSLocationHelper();
        }
        return instance;
    }

    public ArrayList<SSModel> getSsModels() {
        if(ssModels !=null && ssModels.isEmpty()){
            setSsLocationForms();
        }
        return ssModels;
    }

//    public ArrayList<SSLocationForm> getSsLocationForms() {
//        if(ssLocationForms != null && ssLocationForms.isEmpty()){
//            setSsLocationForms();
//        }
//        return ssLocationForms;
//    }

    private void setSsLocationForms(){
            ssModels =  HnppApplication.getSSLocationRepository().getAllLocations();
    }

    public String generateHouseHoldId(SSLocations ssLocations,String lastFourDigit){
        return  ssLocations.division.code+""+ssLocations.district.code+""+ssLocations.city_corporation_upazila.code+""
                +ssLocations.pourasabha.code+""+ssLocations.union_ward.code+""
                +ssLocations.village.code+""+lastFourDigit;
    }
    public Address getSSAddress(SSLocations ssLocations){
        Address address = new Address();
        address.setAddressType("usual_residence");
        HashMap<String,String> addressMap = new HashMap<>();
        addressMap.put("address1", ssLocations.union_ward.name);
        addressMap.put("address2", ssLocations.city_corporation_upazila.name);
        addressMap.put("address3", ssLocations.pourasabha.name);
        addressMap.put("address8", ssLocations.village.id+"");
        address.setAddressFields(addressMap);
        address.setStateProvince(ssLocations.division.name);
        address.setCityVillage(ssLocations.village.name);
        address.setCountyDistrict(ssLocations.district.name);
        address.setCountry(ssLocations.country.name);
        return address;
    }

}
