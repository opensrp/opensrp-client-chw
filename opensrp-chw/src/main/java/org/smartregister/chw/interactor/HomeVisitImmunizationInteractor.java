package org.smartregister.chw.interactor;

import android.text.TextUtils;
import android.util.Log;

import org.joda.time.DateTime;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.HomeVisitImmunizationContract;
import org.smartregister.chw.model.VaccineTaskModel;
import org.smartregister.chw.util.ChwServiceSchedule;
import org.smartregister.chw.util.HomeVisitVaccineGroup;
import org.smartregister.chw.util.ImmunizationState;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.smartregister.chw.util.Constants.IMMUNIZATION_CONSTANT.DATE;
import static org.smartregister.immunization.util.VaccinatorUtils.generateScheduleList;
import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;


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

        // get first object in list that has an alert other than NO_ALERT
        for (HomeVisitVaccineGroup toReturn : allGroups) {
            if (toReturn.getDueVaccines().size() > 0 && !toReturn.getAlert().equals(ImmunizationState.NO_ALERT)) {

                if (currentActiveHomeVisit == null && toReturn.getNotGivenInThisVisitVaccines().size() == 0 && toReturn.getGivenVaccines().size() == 0) {
                    currentActiveHomeVisit = toReturn;
                }

                if (currentActiveHomeVisit != null && (toReturn.getNotGivenInThisVisitVaccines().size() > 0 || toReturn.getGivenVaccines().size() > 0)) {
                    return toReturn;
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
    public ArrayList<HomeVisitVaccineGroup> determineAllHomeVisitVaccineGroup(List<Alert> alerts, List<Vaccine> vaccines, ArrayList<VaccineWrapper> notGivenVaccines, List<Map<String, Object>> sch) {
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
                    computeDueDate(position, vaccine, alerts, receivedvaccines, homeVisitVaccineGroupArrayList, sch);
                }
            }
        }

        for (int x = 0; x < homeVisitVaccineGroupArrayList.size(); x++) {
            // compute not given vaccines
            homeVisitVaccineGroupArrayList.get(x).calculateNotGivenVaccines();

            for (int i = 0; i < homeVisitVaccineGroupArrayList.get(x).getDueVaccines().size(); i++) {
                for (VaccineWrapper notgivenVaccine : notGivenVaccines) {
                    if (homeVisitVaccineGroupArrayList.get(x).getDueVaccines().get(i).display().equalsIgnoreCase(notgivenVaccine.getName())) {
                        homeVisitVaccineGroupArrayList.get(x).getNotGivenInThisVisitVaccines().add(notgivenVaccine.getVaccine());
                    }
                }
            }
        }

        return homeVisitVaccineGroupArrayList;
    }

    private void computeDueDate(
            Integer position, VaccineRepo.Vaccine vaccine, List<Alert> alerts, Map<String, Date> receivedvaccines,
            ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupArrayList, List<Map<String, Object>> sch
    ) {
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
                        homeVisitVaccineGroupArrayList.get(position).setVaccineByDate(((VaccineRepo.Vaccine)toprocess.get("vaccine")),dueDate);
                        homeVisitVaccineGroupArrayList.get(position).setDueDate(dueDate.toLocalDate() + "");
                        homeVisitVaccineGroupArrayList.get(position).setDueDisplayDate(DateUtil.formatDate(dueDate.toLocalDate(), "dd MMM yyyy"));
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

        getVaccineTask(childClient.getColumnmaps(), childClient.getCaseId(), notGivenVaccines)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VaccineTaskModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(VaccineTaskModel vaccineTaskModel) {
                        callBack.immunizationState(vaccineTaskModel.getAlerts(), vaccineTaskModel.getVaccines()
                                , vaccineTaskModel.getReceivedVaccines(), vaccineTaskModel.getScheduleList());

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });


    }

    /**
     * Replacement of previous vaccinationasynctask
     * it'll calculate the received vaccine list of a child.
     *
     * @param getColumnMaps
     * @param entityId
     * @param notDoneVaccines
     * @return
     */
    private Observable<VaccineTaskModel> getVaccineTask(final Map<String, String> getColumnMaps, final String entityId, final ArrayList<VaccineWrapper> notDoneVaccines) {

        return Observable.create(new ObservableOnSubscribe<VaccineTaskModel>() {
            @Override
            public void subscribe(ObservableEmitter<VaccineTaskModel> emmiter) throws Exception {
                String dobString = org.smartregister.util.Utils.getValue(getColumnMaps, DBConstants.KEY.DOB, false);
                DateTime dob = org.smartregister.chw.util.Utils.dobStringToDateTime(dobString);
                if (dob == null) {
                    dob = new DateTime();
                }

                if (!TextUtils.isEmpty(dobString)) {
                    DateTime dateTime = new DateTime(dobString);
                    try {
                        VaccineSchedule.updateOfflineAlerts(entityId, dateTime, "child");
                    } catch (Exception e) {

                    }
                    try {
                        ChwServiceSchedule.updateOfflineAlerts(entityId, dateTime);
                    } catch (Exception e) {

                    }
                }
                List<Alert> alerts = ChwApplication.getInstance().getContext().alertService().findByEntityIdAndAlertNames(entityId, VaccinateActionUtils.allAlertNames("child"));
                List<Vaccine> vaccines = ChwApplication.getInstance().vaccineRepository().findByEntityId(entityId);
                Map<String, Date> recievedVaccines = receivedVaccines(vaccines);
                int size = notDoneVaccines.size();
                for (int i = 0; i < size; i++) {
                    recievedVaccines.put(notDoneVaccines.get(i).getName().toLowerCase(), new Date());
                }

                List<Map<String, Object>> sch = generateScheduleList("child", dob, recievedVaccines, alerts);
                VaccineTaskModel vaccineTaskModel = new VaccineTaskModel();
                vaccineTaskModel.setAlerts(alerts);
                vaccineTaskModel.setVaccines(vaccines);
                vaccineTaskModel.setReceivedVaccines(recievedVaccines);
                vaccineTaskModel.setScheduleList(sch);
                emmiter.onNext(vaccineTaskModel);
            }
        });
    }
}
