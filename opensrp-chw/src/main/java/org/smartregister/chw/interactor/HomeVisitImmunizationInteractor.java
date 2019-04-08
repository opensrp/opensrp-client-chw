package org.smartregister.chw.interactor;

import android.text.TextUtils;
import android.util.Log;

import org.joda.time.DateTime;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.HomeVisitImmunizationContract;
import org.smartregister.chw.model.VaccineTaskModel;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.ChwServiceSchedule;
import org.smartregister.chw.util.HomeVisitVaccineGroupDetails;
import org.smartregister.chw.util.ImmunizationState;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.immunization.util.VaccinatorUtils;
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

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.smartregister.chw.util.Constants.IMMUNIZATION_CONSTANT.DATE;
import static org.smartregister.immunization.util.VaccinatorUtils.generateScheduleList;
import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;

public class HomeVisitImmunizationInteractor implements HomeVisitImmunizationContract.Interactor {
    private static String TAG = HomeVisitImmunizationInteractor.class.toString();

    public HomeVisitImmunizationInteractor() {
        // this(new AppExecutors());
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        Log.d(TAG,"onDestroy unimplemented");
    }

    @Override
    public HomeVisitVaccineGroupDetails getCurrentActiveHomeVisitVaccineGroupDetail(ArrayList<HomeVisitVaccineGroupDetails> allGroups) {
        HomeVisitVaccineGroupDetails currentActiveHomeVisit = null;
        int index = 0;
        for (HomeVisitVaccineGroupDetails toReturn : allGroups) {
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
                } else {
                    return toprocess.getNotGivenInThisVisitVaccines().size() == toprocess.getDueVaccines().size();
                }
            }
        }
        return false;
    }

    @Override
    public boolean isComplete(HomeVisitVaccineGroupDetails toprocess) {
        if (toprocess != null && toprocess.getDueVaccines() != null && toprocess.getDueVaccines().size() > 0) {
            if (toprocess.getGivenVaccines().size() == toprocess.getDueVaccines().size()) {
                return toprocess.getNotGivenInThisVisitVaccines().size() == 0;
            }
        }
        return false;
    }

    @Override
    public boolean groupIsDue(HomeVisitVaccineGroupDetails toprocess) {
        if (toprocess != null && toprocess.getDueVaccines() != null && toprocess.getDueVaccines().size() > 0) {
            if (toprocess.getGivenVaccines().size() == 0) {
                return toprocess.getNotGivenInThisVisitVaccines().size() == 0;
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

            try{
                HomeVisitVaccineGroupDetails toReturn = allGroup.get(i);
                if (
                        toReturn.getDueVaccines().size() > toReturn.getGivenVaccines().size()
                                && (toReturn.getNotGivenInThisVisitVaccines().size() <= 0)
                ) {
                    return true;
                }

            }catch (Exception e){

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
    public ArrayList<VaccineRepo.Vaccine> getNotGivenVaccinesNotInNotGivenThisVisit(HomeVisitVaccineGroupDetails allGroup) {
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
    public ArrayList<HomeVisitVaccineGroupDetails> determineAllHomeVisitVaccineGroupDetails(List<Alert> alerts, List<Vaccine> vaccines, ArrayList<VaccineWrapper> notGivenVaccines, List<Map<String, Object>> sch) {
        LinkedHashMap<String, HomeVisitVaccineGroupDetails> map = new LinkedHashMap<>();
        for (VaccineRepo.Vaccine vaccine : VaccineRepo.Vaccine.values()) {
            if (vaccine.category().equalsIgnoreCase("child")) {
                String stateKey = VaccinateActionUtils.stateKey(vaccine);

                if (isNotBlank(stateKey)) {

                    HomeVisitVaccineGroupDetails homeVisitVaccineGroupDetails = map.get(stateKey);
                    if(homeVisitVaccineGroupDetails == null){
                        homeVisitVaccineGroupDetails = new HomeVisitVaccineGroupDetails();
                        homeVisitVaccineGroupDetails.setGroup(stateKey);
                    }

                    // process
                    if(ChildUtils.hasAlert(vaccine, alerts)){

                        // add to due vaccines if it has an alert
                        homeVisitVaccineGroupDetails.getDueVaccines().add(vaccine);
                        ImmunizationState immunizationState = ChildUtils.assignAlert(vaccine, alerts);

                        if (immunizationState == (ImmunizationState.DUE) || immunizationState == (ImmunizationState.OVERDUE)
                                || immunizationState == (ImmunizationState.UPCOMING) || immunizationState == (ImmunizationState.EXPIRED)) {
                            homeVisitVaccineGroupDetails.setAlert(ChildUtils.assignAlert(vaccine, alerts));
                        }


                        // add to given vaccines if its in received list
                        if (ChildUtils.isReceived(vaccine.display(), VaccinatorUtils.receivedVaccines(vaccines))) {
                            homeVisitVaccineGroupDetails.getGivenVaccines().add(vaccine);
                        }

                        // assign date due
                        for (Map<String, Object> schedule : sch) {
                            if (((VaccineRepo.Vaccine) (schedule.get("vaccine"))).name().equalsIgnoreCase(vaccine.name())) {
                                DateTime dueDate = (DateTime) schedule.get(DATE);

                                if(dueDate != null){
                                    homeVisitVaccineGroupDetails.setVaccineByDate(((VaccineRepo.Vaccine)schedule.get("vaccine")),dueDate);
                                    homeVisitVaccineGroupDetails.setDueDate(dueDate.toLocalDate() + "");
                                    homeVisitVaccineGroupDetails.setDueDisplayDate(DateUtil.formatDate(dueDate.toLocalDate(), "dd MMM yyyy"));
                                }
                            }
                        }

                        for (int i = 0; i < homeVisitVaccineGroupDetails.getDueVaccines().size(); i++) {
                            for (VaccineWrapper notgivenVaccine : notGivenVaccines) {
                                if (homeVisitVaccineGroupDetails.getDueVaccines().get(i).display().equalsIgnoreCase(notgivenVaccine.getName())) {
                                    homeVisitVaccineGroupDetails.getNotGivenInThisVisitVaccines().add(notgivenVaccine.getVaccine());
                                }
                            }
                        }
// recompute
                      homeVisitVaccineGroupDetails.calculateNotGivenVaccines();
                        map.put(stateKey, homeVisitVaccineGroupDetails);
                    }
                }

            }
        }
        ArrayList<HomeVisitVaccineGroupDetails> details = new ArrayList<>(map.values());

        return details;
    }

    @Override
    public void updateImmunizationState(CommonPersonObjectClient childClient, ArrayList<VaccineWrapper> notGivenVaccines, final HomeVisitImmunizationContract.InteractorCallBack callBack) {

        getVaccineTask(childClient.getColumnmaps(),childClient.getCaseId(),notGivenVaccines)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VaccineTaskModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(VaccineTaskModel vaccineTaskModel) {
                        callBack.immunizationState(vaccineTaskModel.getAlerts(), vaccineTaskModel.getVaccines()
                                ,vaccineTaskModel.getReceivedVaccines(), vaccineTaskModel.getScheduleList());

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
     * @param getColumnMaps
     * @param entityId
     * @param notDoneVaccines
     * @return
     */
    private Observable<VaccineTaskModel> getVaccineTask(final Map<String, String> getColumnMaps,final String entityId,final ArrayList<VaccineWrapper> notDoneVaccines){

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
                    try{
                        VaccineSchedule.updateOfflineAlerts(entityId, dateTime, "child");
                    }catch (Exception e){

                    }
                    try{
                        ChwServiceSchedule.updateOfflineAlerts(entityId, dateTime);
                    }catch (Exception e){

                    }
                }
                List<Alert> alerts= ChwApplication.getInstance().getContext().alertService().findByEntityIdAndAlertNames(entityId, VaccinateActionUtils.allAlertNames("child"));
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
