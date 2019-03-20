package org.smartgresiter.wcaro.interactor;

import android.support.annotation.VisibleForTesting;

import org.joda.time.DateTime;
import org.smartgresiter.wcaro.contract.HomeVisitImmunizationContract;
import org.smartgresiter.wcaro.listener.ImmunizationStateChangeListener;
import org.smartgresiter.wcaro.task.UpdateServiceTask;
import org.smartgresiter.wcaro.task.VaccinationAsyncTask;
import org.smartgresiter.wcaro.util.HomeVisitVaccineGroupDetails;
import org.smartgresiter.wcaro.util.ImmunizationState;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.smartgresiter.wcaro.util.Constants.IMMUNIZATION_CONSTANT.DATE;
import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;
import static org.smartregister.util.Utils.startAsyncTask;

public class HomeVisitImmunizationInteractor implements HomeVisitImmunizationContract.Interactor {
    private AppExecutors appExecutors;
    private UpdateServiceTask updateServiceTask;
    private VaccinationAsyncTask vaccinationAsyncTask;

    @VisibleForTesting
    HomeVisitImmunizationInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public HomeVisitImmunizationInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

    }

    @Override
    public HomeVisitVaccineGroupDetails getCurrentActiveHomeVisitVaccineGroupDetail(ArrayList<HomeVisitVaccineGroupDetails> allGroups) {
        HomeVisitVaccineGroupDetails currentActiveHomeVisit = null;
        int index = 0;
        for (HomeVisitVaccineGroupDetails toReturn : allGroups) {
            if (toReturn.getDueVaccines().size() > 0) {
                if (!(toReturn.getNotGivenInThisVisitVaccines().size() > 0 || toReturn.getGivenVaccines().size() > 0)) {
                    if (!toReturn.getAlert().equals(ImmunizationState.NO_ALERT)) {
                        currentActiveHomeVisit = toReturn;
                        break;
                    }
                }
            }
            index++;
        }

        //check if this is not the last due group
        boolean completedExistsAfterCurrentGroup = false;
        if (index < allGroups.size() - 1) {
            for (int i = index + 1; i < allGroups.size(); i++) {
                HomeVisitVaccineGroupDetails toReturn = allGroups.get(i);
                if (toReturn.getDueVaccines().size() > 0) {
                    if ((toReturn.getNotGivenInThisVisitVaccines().size() > 0 || toReturn.getGivenVaccines().size() > 0)) {
                        if (!toReturn.getAlert().equals(ImmunizationState.NO_ALERT)) {
                            completedExistsAfterCurrentGroup = true;
                            break;
                        }
                    }
                }
            }
        }
        if (completedExistsAfterCurrentGroup) {
            currentActiveHomeVisit = null;
            for (int i = index + 1; i < allGroups.size(); i++) {
                HomeVisitVaccineGroupDetails toReturn = allGroups.get(i);
                if (toReturn.getDueVaccines().size() > 0) {
                    if ((toReturn.getNotGivenInThisVisitVaccines().size() > 0 || toReturn.getGivenVaccines().size() > 0)) {
                        if (!toReturn.getAlert().equals(ImmunizationState.NO_ALERT)) {
                            currentActiveHomeVisit = toReturn;
                            break;
                        }
                    }
                }
            }

        }

        return currentActiveHomeVisit;
    }


    @Override
    public HomeVisitVaccineGroupDetails getLastActiveHomeVisitVaccineGroupDetail(ArrayList<HomeVisitVaccineGroupDetails> allGroups) {
        HomeVisitVaccineGroupDetails toReturn = null;
        for (int i = 0; i < allGroups.size(); i++) {
            if (!allGroups.get(i).getAlert().equals(ImmunizationState.NO_ALERT)) {
                toReturn = allGroups.get(i);
            }
        }
        return toReturn;
    }

    @Override
    public boolean isPartiallyComplete(HomeVisitVaccineGroupDetails toprocess) {
        if (toprocess != null && toprocess.getDueVaccines() != null && toprocess.getDueVaccines().size() > 0) {
//            if(toprocess.getNotGivenInThisVisitVaccines().size()>0){
//                return true;
//            }
            if (toprocess.getGivenVaccines().size() < toprocess.getDueVaccines().size()) {
                if (toprocess.getGivenVaccines().size() > 0) {
                    return true;
                }else{
                    if(toprocess.getNotGivenInThisVisitVaccines().size() == toprocess.getDueVaccines().size()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isComplete(HomeVisitVaccineGroupDetails toprocess) {
        if (toprocess != null && toprocess.getDueVaccines() != null && toprocess.getDueVaccines().size() > 0) {
            if (toprocess.getGivenVaccines().size() == toprocess.getDueVaccines().size()) {
                if (toprocess.getNotGivenInThisVisitVaccines().size() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean groupIsDue(HomeVisitVaccineGroupDetails toprocess) {
        if (toprocess != null && toprocess.getDueVaccines() != null && toprocess.getDueVaccines().size() > 0) {
            if (toprocess.getGivenVaccines().size() == 0) {
                if (toprocess.getNotGivenInThisVisitVaccines().size() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasVaccinesNotGivenSinceLastVisit(ArrayList<HomeVisitVaccineGroupDetails> allGroup) {
        int indexofCurrentGroup = getIndexOfCurrentGroup(allGroup);
        if (isPartiallyComplete(allGroup.get(indexofCurrentGroup))) {
            indexofCurrentGroup = indexofCurrentGroup + 1;
        }
        for (int i = 0; i < indexofCurrentGroup + 1; i++) {
            HomeVisitVaccineGroupDetails toReturn = allGroup.get(i);
            if (toReturn.getDueVaccines().size() > toReturn.getGivenVaccines().size()) {
                if (!(toReturn.getNotGivenInThisVisitVaccines().size() > 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getIndexOfCurrentGroup(ArrayList<HomeVisitVaccineGroupDetails> allGroup) {
        HomeVisitVaccineGroupDetails currentActiveGroup = getCurrentActiveHomeVisitVaccineGroupDetail(allGroup);
        if (currentActiveGroup == null) {
            currentActiveGroup = getLastActiveHomeVisitVaccineGroupDetail(allGroup);
        }
        int indexofCurrentGroup = 0;
        for (int i = 0; i < allGroup.size(); i++) {
            HomeVisitVaccineGroupDetails toReturn = allGroup.get(i);
            if (toReturn.getDueVaccines().size() > 0) {
                if (toReturn.getGroup().equalsIgnoreCase(currentActiveGroup.getGroup())) {
                    indexofCurrentGroup = i;
                }
            }
        }
        return indexofCurrentGroup;
    }

    @Override
    public ArrayList<VaccineRepo.Vaccine> getNotGivenVaccinesLastVisitList(ArrayList<HomeVisitVaccineGroupDetails> allGroup) {
        ArrayList<VaccineRepo.Vaccine> toReturn = new ArrayList<VaccineRepo.Vaccine>();
        if (hasVaccinesNotGivenSinceLastVisit(allGroup)) {
            int indexOfCurrentGroup = getIndexOfCurrentGroup(allGroup);
            if (isPartiallyComplete(allGroup.get(indexOfCurrentGroup))) {
                indexOfCurrentGroup = indexOfCurrentGroup + 1;
            }
            for (int i = 0; i < indexOfCurrentGroup; i++) {
//                allGroup.get(i).calculateNotGivenVaccines();
                toReturn.addAll(getNotGivenVaccinesNotInNotGivenThisVisit(allGroup.get(i)));
            }
        }
        return toReturn;
    }

    @Override
    public ArrayList<VaccineRepo.Vaccine> getNotGivenVaccinesNotInNotGivenThisVisit(HomeVisitVaccineGroupDetails allGroup) {
        ArrayList<VaccineRepo.Vaccine> getNotGivenVaccinesNotInNotGivenThisVisit = new ArrayList<VaccineRepo.Vaccine>();
        for (VaccineRepo.Vaccine toProcess : allGroup.getNotGivenVaccines()) {
            boolean isInNotGivenThisVisit = false;
            for (VaccineRepo.Vaccine notGivenThisVisit : allGroup.getNotGivenInThisVisitVaccines()) {
                if (notGivenThisVisit.display().equalsIgnoreCase(toProcess.display())) {
                    isInNotGivenThisVisit = true;
                }
            }
            if (!isInNotGivenThisVisit) {
                getNotGivenVaccinesNotInNotGivenThisVisit.add(toProcess);
            }
        }

        return getNotGivenVaccinesNotInNotGivenThisVisit;
    }

    @Override
    public ArrayList<HomeVisitVaccineGroupDetails> determineAllHomeVisitVaccineGroupDetails(List<Alert> alerts, List<Vaccine> vaccines, ArrayList<VaccineWrapper> notGivenVaccines, List<Map<String, Object>> sch) {
        ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetailsArrayList = new ArrayList<HomeVisitVaccineGroupDetails>();
        Map<String, Date> receivedvaccines = receivedVaccines(vaccines);
        List<VaccineRepo.Vaccine> vList = Arrays.asList(VaccineRepo.Vaccine.values());
        ArrayList<String> vaccineGroupName = new ArrayList<String>();
        String groupName = "";
        for (VaccineRepo.Vaccine vaccine : vList) {
            if (vaccine.category().equalsIgnoreCase("child")) {
                if (!isEmpty(VaccinateActionUtils.stateKey(vaccine)) && !vaccineGroupName.contains(VaccinateActionUtils.stateKey(vaccine))) {
                    vaccineGroupName.add(VaccinateActionUtils.stateKey(vaccine));
                }
            }
        }

        for (int i = 0; i < vaccineGroupName.size(); i++) {
            HomeVisitVaccineGroupDetails homeVisitVaccineGroupDetails = new HomeVisitVaccineGroupDetails();
            homeVisitVaccineGroupDetails.setGroup(vaccineGroupName.get(i));
            homeVisitVaccineGroupDetailsArrayList.add(homeVisitVaccineGroupDetails);
        }
        assignDueVaccine(vList, homeVisitVaccineGroupDetailsArrayList, alerts);
        assignGivenVaccine(homeVisitVaccineGroupDetailsArrayList, receivedvaccines);
        assignDate(homeVisitVaccineGroupDetailsArrayList, sch);

        for (HomeVisitVaccineGroupDetails singlegroup : homeVisitVaccineGroupDetailsArrayList) {
            for (int i = 0; i < singlegroup.getDueVaccines().size(); i++) {
                for (VaccineWrapper notgivenVaccine : notGivenVaccines) {
                    if (singlegroup.getDueVaccines().get(i).display().equalsIgnoreCase(notgivenVaccine.getName())) {
                        singlegroup.getNotGivenInThisVisitVaccines().add(notgivenVaccine.getVaccine());
                    }
                }
            }
        }

        return homeVisitVaccineGroupDetailsArrayList;
    }

    private void assignDate(ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetailsArrayList, List<Map<String, Object>> sch) {
        for (int i = 0; i < homeVisitVaccineGroupDetailsArrayList.size(); i++) {
            for (VaccineRepo.Vaccine vaccine : homeVisitVaccineGroupDetailsArrayList.get(i).getDueVaccines()) {
                for (Map<String, Object> toprocess : sch) {
                    if (((VaccineRepo.Vaccine) (toprocess.get("vaccine"))).name().equalsIgnoreCase(vaccine.name())) {
                        DateTime dueDate = (DateTime) toprocess.get(DATE);
                        homeVisitVaccineGroupDetailsArrayList.get(i).setDueDate(dueDate.toLocalDate() + "");
                        String duedateString = DateUtil.formatDate(dueDate.toLocalDate(), "dd MMM yyyy");
                        homeVisitVaccineGroupDetailsArrayList.get(i).setDueDisplayDate(duedateString);
                    }
                    toprocess.size();
                }
            }
        }
    }

    private void assignGivenVaccine(ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetailsArrayList, Map<String, Date> receivedvaccines) {
        for (int i = 0; i < homeVisitVaccineGroupDetailsArrayList.size(); i++) {
            ArrayList<VaccineRepo.Vaccine> dueVaccines = homeVisitVaccineGroupDetailsArrayList.get(i).getDueVaccines();
            for (VaccineRepo.Vaccine checkVaccine : dueVaccines) {
                if (isReceived(checkVaccine.display(), receivedvaccines)) {
                    homeVisitVaccineGroupDetailsArrayList.get(i).getGivenVaccines().add(checkVaccine);
                }
            }
            homeVisitVaccineGroupDetailsArrayList.get(i).calculateNotGivenVaccines();
        }
    }

    private void assignDueVaccine(List<VaccineRepo.Vaccine> vList, ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetailsArrayList, List<Alert> alerts) {
        for (int i = 0; i < homeVisitVaccineGroupDetailsArrayList.size(); i++) {
            for (VaccineRepo.Vaccine vaccine : vList) {
                if (VaccinateActionUtils.stateKey(vaccine).equalsIgnoreCase(homeVisitVaccineGroupDetailsArrayList.get(i).getGroup())) {
                    if (hasAlert(vaccine, alerts)) {
                        homeVisitVaccineGroupDetailsArrayList.get(i).getDueVaccines().add(vaccine);
                        ImmunizationState state = assignAlert(vaccine, alerts);
                        if (state == (ImmunizationState.DUE) || state == (ImmunizationState.OVERDUE)
                                || state == (ImmunizationState.UPCOMING) || state == (ImmunizationState.EXPIRED)) {
                            homeVisitVaccineGroupDetailsArrayList.get(i).setAlert(state);
                        }
                    }
                }
            }
        }
    }

    private boolean hasAlert(VaccineRepo.Vaccine vaccine, List<Alert> alerts) {
        for (Alert alert : alerts) {
            if (alert.scheduleName().equalsIgnoreCase(vaccine.display())) {
                return true;
            }
        }
        return false;
    }

    private ImmunizationState alertState(Alert toProcess) {
        if (toProcess == null) {
            return ImmunizationState.NO_ALERT;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.NORMAL.name())) {
            return ImmunizationState.DUE;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.UPCOMING.name())) {
            return ImmunizationState.UPCOMING;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.URGENT.name())) {
            return ImmunizationState.OVERDUE;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.EXPIRED.name())) {
            return ImmunizationState.EXPIRED;
        }
        return ImmunizationState.NO_ALERT;
    }

    private boolean isReceived(String s, Map<String, Date> receivedvaccines) {
        for (String name : receivedvaccines.keySet()) {
            if (s.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public ImmunizationState assignAlert(VaccineRepo.Vaccine vaccine, List<Alert> alerts) {
        for (Alert alert : alerts) {
            if (alert.scheduleName().equalsIgnoreCase(vaccine.display())) {
                return alertState(alert);
            }
        }
        return ImmunizationState.NO_ALERT;
    }

    @Override
    public void updateImmunizationState(CommonPersonObjectClient childClient, ArrayList<VaccineWrapper> notGivenVaccines, final HomeVisitImmunizationContract.InteractorCallBack callBack) {
        vaccinationAsyncTask = new VaccinationAsyncTask(childClient.getCaseId(), childClient.getColumnmaps(), notGivenVaccines, new ImmunizationStateChangeListener() {
            @Override
            public void onImmunicationStateChange(List<Alert> alerts, List<Vaccine> vaccines, String stateKey, List<Map<String, Object>> sch,Map<String, Object> nv, ImmunizationState state) {
                callBack.immunizationState(alerts, vaccines, sch,nv);
            }
        });
        startAsyncTask(vaccinationAsyncTask, null);
    }
}
