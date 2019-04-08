package org.smartregister.chw.interactor;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import org.joda.time.DateTime;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.ImmunizationEditContract;
import org.smartregister.chw.model.VaccineTaskModel;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.ChwServiceSchedule;
import org.smartregister.chw.util.HomeVisitVaccineGroupDetails;
import org.smartregister.chw.util.ImmunizationState;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.smartregister.chw.util.Constants.IMMUNIZATION_CONSTANT.DATE;
import static org.smartregister.immunization.util.VaccinatorUtils.generateScheduleList;
import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;

public class ImmunizationEditViewInteractor implements ImmunizationEditContract.Interactor {

    private AppExecutors appExecutors;
    @VisibleForTesting
    ImmunizationEditViewInteractor(AppExecutors appExecutors){
        this.appExecutors = appExecutors;
    }
    public ImmunizationEditViewInteractor(){
        this(new AppExecutors());
    }
    @Override
    public void fetchImmunizationEditData(CommonPersonObjectClient commonPersonObjectClient, final ImmunizationEditContract.InteractorCallBack callBack) {
        getLastVaccineList(commonPersonObjectClient).flatMap(new Function<VaccineTaskModel, ObservableSource<ArrayList<HomeVisitVaccineGroupDetails>>>() {
            @Override
            public ObservableSource<ArrayList<HomeVisitVaccineGroupDetails>> apply(VaccineTaskModel vaccineTaskModel) throws Exception {
                return determineAllHomeVisitVaccineGroupDetails(vaccineTaskModel.getAlerts(),vaccineTaskModel.getVaccines(),vaccineTaskModel.getScheduleList());
                }
            })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<HomeVisitVaccineGroupDetails>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetails) {
                        callBack.updateEditData(homeVisitVaccineGroupDetails);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }
    private Observable<VaccineTaskModel> getLastVaccineList(final CommonPersonObjectClient commonPersonObjectClient){

        return Observable.create(new ObservableOnSubscribe<VaccineTaskModel>() {
            @Override
            public void subscribe(ObservableEmitter<VaccineTaskModel> emmiter) throws Exception {
                String dobString = org.smartregister.util.Utils.getValue(commonPersonObjectClient.getColumnmaps(), DBConstants.KEY.DOB, false);
                DateTime dob = org.smartregister.chw.util.Utils.dobStringToDateTime(dobString);

                List<Alert> alerts= ChwApplication.getInstance().getContext().alertService().findByEntityIdAndAlertNames(commonPersonObjectClient.entityId(), VaccinateActionUtils.allAlertNames("child"));
                List<Vaccine> vaccines = ChwApplication.getInstance().vaccineRepository().findLatestTwentyFourHoursByEntityId(commonPersonObjectClient.entityId());
                Map<String, Date> recievedVaccines = receivedVaccines(vaccines);
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
    private Observable<ArrayList<HomeVisitVaccineGroupDetails>>determineAllHomeVisitVaccineGroupDetails(final List<Alert> alerts,final List<Vaccine> vaccines,final List<Map<String, Object>> sch) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<HomeVisitVaccineGroupDetails>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<HomeVisitVaccineGroupDetails>> emitter) throws Exception {
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
// recompute
                                homeVisitVaccineGroupDetails.calculateNotGivenVaccines();
                                map.put(stateKey, homeVisitVaccineGroupDetails);
                            }
                        }

                    }
                }
                ArrayList<HomeVisitVaccineGroupDetails> details = new ArrayList<>(map.values());

                emitter.onNext(details);
            }
        });
    }
}
