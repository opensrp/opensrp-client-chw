package org.smartregister.chw.interactor;

import android.util.Log;

import org.joda.time.DateTime;
import org.smartregister.chw.contract.HomeVisitImmunizationContract;
import org.smartregister.chw.listener.ImmunizationStateChangeListener;
import org.smartregister.chw.task.VaccinationAsyncTask;
import org.smartregister.chw.util.HomeVisitVaccineGroup;
import org.smartregister.chw.util.ImmunizationState;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.smartregister.chw.util.Constants.IMMUNIZATION_CONSTANT.DATE;
import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;
import static org.smartregister.util.Utils.startAsyncTask;

public class HomeVisitImmunizationInteractor implements HomeVisitImmunizationContract.Interactor {
    private static String TAG = HomeVisitImmunizationInteractor.class.toString();

    public HomeVisitImmunizationInteractor() {
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        Log.d(TAG, "onDestroy unimplemented");
    }

    @Override
    public HomeVisitVaccineGroup getCurrentActiveHomeVisitVaccineGroupDetail(ArrayList<HomeVisitVaccineGroup> allGroups) {
        HomeVisitVaccineGroup currentActiveHomeVisit = null;
        int index = 0;

        for (HomeVisitVaccineGroup toReturn : allGroups) {
            if (toReturn.getDueVaccines().size() > 0) {
                if (toReturn.getNotGivenInThisVisitVaccines().size() == 0 && toReturn.getGivenVaccines().size() == 0) {
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
                HomeVisitVaccineGroup toReturn = allGroups.get(i);
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
                HomeVisitVaccineGroup toReturn = allGroups.get(i);
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
    public HomeVisitVaccineGroup getLastActiveHomeVisitVaccineGroupDetail(ArrayList<HomeVisitVaccineGroup> allGroups) {
        HomeVisitVaccineGroup toReturn = null;
        for (int i = 0; i < allGroups.size(); i++) {
            if (!allGroups.get(i).getAlert().equals(ImmunizationState.NO_ALERT)) {
                toReturn = allGroups.get(i);
            }
        }
        return toReturn;
    }

    @Override
    public boolean isPartiallyComplete(HomeVisitVaccineGroup toprocess) {
        if (toprocess != null && toprocess.getDueVaccines() != null && toprocess.getDueVaccines().size() > 0) {
            if (toprocess.getGivenVaccines().size() < toprocess.getDueVaccines().size()) {
                if (toprocess.getGivenVaccines().size() > 0) {
                    return true;
                } else {
                    return toprocess.getNotGivenInThisVisitVaccines().size() == toprocess.getDueVaccines().size();
                }
            }
        }
        return false;
    }

    @Override
    public boolean isComplete(HomeVisitVaccineGroup toprocess) {
        if (toprocess != null && toprocess.getDueVaccines() != null && toprocess.getDueVaccines().size() > 0) {
            if (toprocess.getGivenVaccines().size() == toprocess.getDueVaccines().size()) {
                return toprocess.getNotGivenInThisVisitVaccines().size() == 0;
            }
        }
        return false;
    }

    @Override
    public boolean groupIsDue(HomeVisitVaccineGroup toprocess) {
        if (toprocess != null && toprocess.getDueVaccines() != null && toprocess.getDueVaccines().size() > 0) {
            if (toprocess.getGivenVaccines().size() == 0) {
                return toprocess.getNotGivenInThisVisitVaccines().size() == 0;
            }
        }
        return false;
    }

    @Override
    public boolean hasVaccinesNotGivenSinceLastVisit(ArrayList<HomeVisitVaccineGroup> allGroup) {
        int indexofCurrentGroup = getIndexOfCurrentGroup(allGroup);
        if (isPartiallyComplete(allGroup.get(indexofCurrentGroup))) {
            indexofCurrentGroup = indexofCurrentGroup + 1;
        }
        for (int i = 0; i < indexofCurrentGroup + 1; i++) {
            HomeVisitVaccineGroup toReturn = allGroup.get(i);
            if (
                    toReturn.getDueVaccines().size() > toReturn.getGivenVaccines().size()
                            && (toReturn.getNotGivenInThisVisitVaccines().size() <= 0)
            ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getIndexOfCurrentGroup(ArrayList<HomeVisitVaccineGroup> allGroup) {
        HomeVisitVaccineGroup currentActiveGroup = getCurrentActiveHomeVisitVaccineGroupDetail(allGroup);
        if (currentActiveGroup == null) {
            currentActiveGroup = getLastActiveHomeVisitVaccineGroupDetail(allGroup);
        }
        int indexofCurrentGroup = 0;
        for (int i = 0; i < allGroup.size(); i++) {
            HomeVisitVaccineGroup toReturn = allGroup.get(i);
            if (toReturn.getDueVaccines().size() > 0) {
                if (toReturn.getGroup().equalsIgnoreCase(currentActiveGroup.getGroup())) {
                    indexofCurrentGroup = i;
                }
            }
        }
        return indexofCurrentGroup;
    }

    @Override
    public ArrayList<VaccineRepo.Vaccine> getNotGivenVaccinesLastVisitList(ArrayList<HomeVisitVaccineGroup> allGroup) {
        ArrayList<VaccineRepo.Vaccine> toReturn = new ArrayList<>();
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
    public ArrayList<VaccineRepo.Vaccine> getNotGivenVaccinesNotInNotGivenThisVisit(HomeVisitVaccineGroup allGroup) {
        ArrayList<VaccineRepo.Vaccine> getNotGivenVaccinesNotInNotGivenThisVisit = new ArrayList<>();
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
    public ArrayList<HomeVisitVaccineGroup> determineAllHomeVisitVaccineGroupDetails(List<Alert> alerts, List<Vaccine> vaccines, ArrayList<VaccineWrapper> notGivenVaccines, List<Map<String, Object>> sch) {
        Map<String, Date> receivedvaccines = receivedVaccines(vaccines);
        List<VaccineRepo.Vaccine> vList = Arrays.asList(VaccineRepo.Vaccine.values());

        ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupArrayList = new ArrayList<>();
        LinkedHashMap<String, Integer> vaccineGroupMap = new LinkedHashMap<>();
        for (VaccineRepo.Vaccine vaccine : vList) {
            if (vaccine.category().equalsIgnoreCase("child")) {

                String stateKey = VaccinateActionUtils.stateKey(vaccine);
                if (isNotBlank(stateKey)) {

                    Integer position = vaccineGroupMap.get(stateKey);
                    // create a group if missing
                    if (position == null) {
                        HomeVisitVaccineGroup homeVisitVaccineGroup = new HomeVisitVaccineGroup();
                        homeVisitVaccineGroup.setGroup(stateKey);

                        homeVisitVaccineGroupArrayList.add(homeVisitVaccineGroup);

                        // get item location
                        position = homeVisitVaccineGroupArrayList.indexOf(homeVisitVaccineGroup);
                        vaccineGroupMap.put(stateKey, position);
                    }

                    // add due date
                    if (hasAlert(vaccine, alerts)) {

                        // add vaccine
                        homeVisitVaccineGroupArrayList.get(position).getDueVaccines().add(vaccine);

                        // add alert
                        ImmunizationState state = assignAlert(vaccine, alerts);
                        if (state == ImmunizationState.DUE || state == ImmunizationState.OVERDUE || state == ImmunizationState.UPCOMING || state == ImmunizationState.EXPIRED) {
                            homeVisitVaccineGroupArrayList.get(position).setAlert(assignAlert(vaccine, alerts));
                        }

                        // check if vaccine is received and record as given
                        if (isReceived(vaccine.display(), receivedvaccines)) {
                            homeVisitVaccineGroupArrayList.get(position).getGivenVaccines().add(vaccine);
                        }

                        // compute due date
                        for (Map<String, Object> toprocess : sch) {
                            if (((VaccineRepo.Vaccine) (toprocess.get("vaccine"))).name().equalsIgnoreCase(vaccine.name())) {
                                DateTime dueDate = (DateTime) toprocess.get(DATE);
                                if (dueDate != null) {
                                    homeVisitVaccineGroupArrayList.get(position).setDueDate(dueDate.toLocalDate() + "");
                                    homeVisitVaccineGroupArrayList.get(position).setDueDisplayDate(DateUtil.formatDate(dueDate.toLocalDate(), "dd MMM yyyy"));
                                }
                            }
                        }
                    }

                    homeVisitVaccineGroupArrayList.get(position).calculateNotGivenVaccines();
                }
            }
        }

        for (int i = 0; i < homeVisitVaccineGroupArrayList.size(); i++) {
            // compute not given vaccines
            homeVisitVaccineGroupArrayList.get(i).calculateNotGivenVaccines();
        }

        for (HomeVisitVaccineGroup singlegroup : homeVisitVaccineGroupArrayList) {
            for (int i = 0; i < singlegroup.getDueVaccines().size(); i++) {
                for (VaccineWrapper notgivenVaccine : notGivenVaccines) {
                    if (singlegroup.getDueVaccines().get(i).display().equalsIgnoreCase(notgivenVaccine.getName())) {
                        singlegroup.getNotGivenInThisVisitVaccines().add(notgivenVaccine.getVaccine());
                    }
                }
            }
        }

        return homeVisitVaccineGroupArrayList;
    }

    private boolean hasAlert(VaccineRepo.Vaccine vaccine, List<Alert> alerts) {
        for (Alert alert : alerts) {
            if (alert.scheduleName().equalsIgnoreCase(vaccine.display())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ImmunizationState alertState(Alert toProcess) {
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
        for (String name : receivedvaccines.keySet()) {
            if (s.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ImmunizationState assignAlert(VaccineRepo.Vaccine vaccine, List<Alert> alerts) {
        ImmunizationState state = ImmunizationState.NO_ALERT;
        for (Alert alert : alerts) {
            if (alert.scheduleName().equalsIgnoreCase(vaccine.display())) {
                state = alertState(alert);
            }
        }
        return state;
    }

    @Override
    public void updateImmunizationState(CommonPersonObjectClient childClient, ArrayList<VaccineWrapper> notGivenVaccines, final HomeVisitImmunizationContract.InteractorCallBack callBack) {
        VaccinationAsyncTask vaccinationAsyncTask = new VaccinationAsyncTask(childClient.getCaseId(), childClient.getColumnmaps(), notGivenVaccines, new ImmunizationStateChangeListener() {
            @Override
            public void onImmunicationStateChange(List<Alert> alerts, List<Vaccine> vaccines, String stateKey, List<Map<String, Object>> sch, ImmunizationState state) {
                callBack.immunizationState(alerts, vaccines, sch);
            }
        });
        startAsyncTask(vaccinationAsyncTask, null);
    }
}
