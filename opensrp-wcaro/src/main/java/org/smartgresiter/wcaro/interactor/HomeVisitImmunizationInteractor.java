package org.smartgresiter.wcaro.interactor;

import android.support.annotation.VisibleForTesting;

import org.smartgresiter.wcaro.contract.HomeVisitImmunizationContract;
import org.smartgresiter.wcaro.task.UpdateServiceTask;
import org.smartgresiter.wcaro.util.HomeVisitVaccineGroupDetails;
import org.smartgresiter.wcaro.util.ImmunizationState;
import org.smartregister.domain.Alert;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.util.VaccinateActionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;

public class HomeVisitImmunizationInteractor implements HomeVisitImmunizationContract.Interactor {
    private AppExecutors appExecutors;
    private UpdateServiceTask updateServiceTask;
    @VisibleForTesting
    HomeVisitImmunizationInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }
    public HomeVisitImmunizationInteractor(){
        this(new AppExecutors());
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

    }

    @Override
    public HomeVisitVaccineGroupDetails getCurrentActiveHomeVisitVaccineGroupDetail(ArrayList<HomeVisitVaccineGroupDetails> allGroups){
        HomeVisitVaccineGroupDetails currentActiveHomeVisit = null;
        for(HomeVisitVaccineGroupDetails toReturn: allGroups){
            if(toReturn.getDueVaccines().size()>0){
                if(!(toReturn.getNotGivenInThisVisitVaccines().size()>0 || toReturn.getGivenVaccines().size()>0)){
                    if(!toReturn.getAlert().equals(ImmunizationState.NO_ALERT)) {
                        currentActiveHomeVisit = toReturn;
                        break;
                    }
                }
            }
        }

        //check if this is not the last due group

        return currentActiveHomeVisit;
    }



    public HomeVisitVaccineGroupDetails getLastActiveHomeVisitVaccineGroupDetail(ArrayList<HomeVisitVaccineGroupDetails> allGroups){
        HomeVisitVaccineGroupDetails toReturn = null;
        for(int i = 0;i<allGroups.size();i++){
            if(!allGroups.get(i).getAlert().equals(ImmunizationState.NO_ALERT)) {
                toReturn = allGroups.get(i);
            }
        }
        return toReturn;
    }

    public boolean isPartiallyComplete(HomeVisitVaccineGroupDetails toprocess){
        if(toprocess.getDueVaccines().size()>0){
            if(toprocess.getNotGivenInThisVisitVaccines().size()>0){
                return true;
            }
        }
        return false;
    }
    public boolean isComplete(HomeVisitVaccineGroupDetails toprocess){
        if(toprocess.getDueVaccines().size()>0){
            if(toprocess.getGivenVaccines().size() == toprocess.getDueVaccines().size()){
                if(toprocess.getNotGivenInThisVisitVaccines().size() == 0) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean groupIsDue(HomeVisitVaccineGroupDetails toprocess){
        if(toprocess.getDueVaccines().size()>0){
            if(toprocess.getGivenVaccines().size() == 0){
                if(toprocess.getNotGivenInThisVisitVaccines().size() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasVaccinesNotGivenSinceLastVisit(ArrayList<HomeVisitVaccineGroupDetails> allGroup){
        int indexofCurrentGroup = getIndexOfCurrentGroup(allGroup);
        for(int i = 0;i<indexofCurrentGroup;i++){
            HomeVisitVaccineGroupDetails toReturn = allGroup.get(i);
            if(toReturn.getDueVaccines().size() > toReturn.getGivenVaccines().size()){
                return true;
            }
        }
        return false;
    }

    private int getIndexOfCurrentGroup(ArrayList<HomeVisitVaccineGroupDetails> allGroup) {
        int indexofCurrentGroup = 0;
        for(int i = 0;i<allGroup.size();i++){
            HomeVisitVaccineGroupDetails toReturn = allGroup.get(i);
            if(toReturn.getDueVaccines().size()>0){
                if(!(toReturn.getNotGivenInThisVisitVaccines().size()>0 && toReturn.getGivenVaccines().size()>0)){
                    if(!toReturn.getAlert().equals(ImmunizationState.NO_ALERT)) {
                        indexofCurrentGroup = i;
                    }
                }
            }
        }
        return indexofCurrentGroup;
    }

    public ArrayList<VaccineRepo.Vaccine> getNotGivenVaccinesLastVisitList(ArrayList<HomeVisitVaccineGroupDetails> allGroup){
        ArrayList<VaccineRepo.Vaccine> toReturn = new ArrayList<VaccineRepo.Vaccine>();
        if(hasVaccinesNotGivenSinceLastVisit(allGroup)){
            int indexOfCurrentGroup = getIndexOfCurrentGroup(allGroup);
            for(int i = 0;i<indexOfCurrentGroup;i++){
//                allGroup.get(i).calculateNotGivenVaccines();
                toReturn.addAll(allGroup.get(i).getNotGivenVaccines());
            }
        }
        return toReturn;
    }

    @Override
    public ArrayList<HomeVisitVaccineGroupDetails> determineAllHomeVisitVaccineGroupDetails(List<Alert> alerts, List<Vaccine> vaccines){
        ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetailsArrayList = new ArrayList<HomeVisitVaccineGroupDetails>();
        Map<String, Date> receivedvaccines = receivedVaccines(vaccines);
        List<VaccineRepo.Vaccine> vList = Arrays.asList(VaccineRepo.Vaccine.values());
        ArrayList<String> vaccineGroupName= new ArrayList<String>();
        String groupName = "";
        for(VaccineRepo.Vaccine vaccine : vList){
            if(vaccine.category().equalsIgnoreCase("child")) {
                if(!vaccineGroupName.contains(VaccinateActionUtils.stateKey(vaccine))){
                    vaccineGroupName.add(VaccinateActionUtils.stateKey(vaccine));
                }
            }
        }
        for(String emptyname : vaccineGroupName){
            if(isBlank(emptyname)){
                vaccineGroupName.remove(emptyname);
            }
        }
        for(int i = 0;i<vaccineGroupName.size();i++){
            HomeVisitVaccineGroupDetails homeVisitVaccineGroupDetails = new HomeVisitVaccineGroupDetails();
            homeVisitVaccineGroupDetails.setGroup(vaccineGroupName.get(i));
            homeVisitVaccineGroupDetailsArrayList.add(homeVisitVaccineGroupDetails);
        }
        homeVisitVaccineGroupDetailsArrayList = assignDueVaccine(vList,homeVisitVaccineGroupDetailsArrayList,alerts);
        homeVisitVaccineGroupDetailsArrayList = assignGivenVaccine(homeVisitVaccineGroupDetailsArrayList,receivedvaccines);
        return  homeVisitVaccineGroupDetailsArrayList;
    }

    private ArrayList<HomeVisitVaccineGroupDetails> assignGivenVaccine(ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetailsArrayList, Map<String, Date> receivedvaccines) {
        for(int i = 0;i<homeVisitVaccineGroupDetailsArrayList.size();i++){
            ArrayList<VaccineRepo.Vaccine> dueVaccines = homeVisitVaccineGroupDetailsArrayList.get(i).getDueVaccines();
            for(VaccineRepo.Vaccine checkVaccine: dueVaccines){
                if(isReceived(checkVaccine.display(),receivedvaccines)){
                    homeVisitVaccineGroupDetailsArrayList.get(i).getGivenVaccines().add(checkVaccine);
                }
            }
            homeVisitVaccineGroupDetailsArrayList.get(i).calculateNotGivenVaccines();
        }
        return homeVisitVaccineGroupDetailsArrayList;
    }

    private ArrayList<HomeVisitVaccineGroupDetails> assignDueVaccine(List<VaccineRepo.Vaccine> vList, ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetailsArrayList, List<Alert> alerts) {
        for(int i = 0;i<homeVisitVaccineGroupDetailsArrayList.size();i++){
            for(VaccineRepo.Vaccine vaccine : vList){
                if(VaccinateActionUtils.stateKey(vaccine).equalsIgnoreCase(homeVisitVaccineGroupDetailsArrayList.get(i).getGroup())){
                    if(hasAlert(vaccine,alerts)){
                        homeVisitVaccineGroupDetailsArrayList.get(i).getDueVaccines().add(vaccine);
                        homeVisitVaccineGroupDetailsArrayList.get(i).setAlert(assignAlert(vaccine,alerts));
                    }
                }
            }
        }
        return homeVisitVaccineGroupDetailsArrayList;
    }

    private boolean hasAlert(VaccineRepo.Vaccine vaccine, List<Alert> alerts) {
        boolean hasAlert = false;
        for(Alert alert : alerts){
            if(alert.scheduleName().equalsIgnoreCase(vaccine.display())){
                hasAlert = true;
            }
        }
        return hasAlert;
    }

    public ImmunizationState alertState(Alert toProcess){
        ImmunizationState state = ImmunizationState.NO_ALERT;
        if (toProcess == null) {
            state = ImmunizationState.NO_ALERT;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.NORMAL.name())) {
            state = ImmunizationState.DUE;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.UPCOMING.name())) {
            state = ImmunizationState.UPCOMING;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.URGENT.name())) {
            state = ImmunizationState.OVERDUE;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.EXPIRED.name())) {
            state = ImmunizationState.EXPIRED;
        }
        return state;
    }

    private boolean isReceived(String s, Map<String, Date> receivedvaccines) {
        boolean isReceived = false;
        for (String name : receivedvaccines.keySet()) {
            if (s.equalsIgnoreCase(name)){
                isReceived = true;
            }
        }
        return isReceived;
    }

    private ImmunizationState assignAlert(VaccineRepo.Vaccine vaccine, List<Alert> alerts) {
        ImmunizationState state = ImmunizationState.NO_ALERT;
        for(Alert alert : alerts){
            if(alert.scheduleName().equalsIgnoreCase(vaccine.display())){
                state = alertState(alert);
            }
        }
        return state;
    }



}
