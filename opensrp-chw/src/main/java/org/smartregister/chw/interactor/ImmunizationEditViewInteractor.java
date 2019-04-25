//package org.smartregister.chw.interactor;
//
//import android.support.annotation.VisibleForTesting;
//import android.text.TextUtils;
//
//import com.google.gson.reflect.TypeToken;
//
//import org.joda.time.DateTime;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.smartregister.chw.application.ChwApplication;
//import org.smartregister.chw.contract.ImmunizationEditContract;
//import org.smartregister.chw.domain.HomeVisit;
//import org.smartregister.chw.model.VaccineTaskModel;
//import org.smartregister.chw.util.ChildDBConstants;
//import org.smartregister.chw.util.ChildUtils;
//import org.smartregister.chw.util.HomeVisitVaccineGroup;
//import org.smartregister.commonregistry.CommonPersonObjectClient;
//import org.smartregister.domain.Alert;
//import org.smartregister.family.util.AppExecutors;
//import org.smartregister.family.util.DBConstants;
//import org.smartregister.immunization.domain.Vaccine;
//import org.smartregister.immunization.domain.VaccineWrapper;
//import org.smartregister.immunization.util.VaccinateActionUtils;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import io.reactivex.Observable;
//import io.reactivex.ObservableEmitter;
//import io.reactivex.ObservableOnSubscribe;
//import io.reactivex.ObservableSource;
//import io.reactivex.Observer;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Function;
//import io.reactivex.schedulers.Schedulers;
//import static org.smartregister.immunization.util.VaccinatorUtils.generateScheduleList;
//import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;
//
//public class ImmunizationEditViewInteractor implements ImmunizationEditContract.Interactor {
//
//    private AppExecutors appExecutors;
//    private HomeVisitImmunizationInteractor homeVisitImmunizationInteractor;
//    private List<Vaccine> vaccines;
//    @VisibleForTesting
//    ImmunizationEditViewInteractor(AppExecutors appExecutors){
//        this.appExecutors = appExecutors;
//    }
//    public ImmunizationEditViewInteractor(){
//        this(new AppExecutors());
//        homeVisitImmunizationInteractor = new HomeVisitImmunizationInteractor();
//    }
//
//    public List<Vaccine> getVaccines() {
//        return homeVisitImmunizationInteractor.getVaccines();
//    }
//
//    @Override
//    public void fetchImmunizationEditData(CommonPersonObjectClient commonPersonObjectClient, final ImmunizationEditContract.InteractorCallBack callBack) {
//
//        String lastHomeVisitStr=org.smartregister.util.Utils.getValue(commonPersonObjectClient.getColumnmaps(), ChildDBConstants.KEY.LAST_HOME_VISIT, false);
//        long lastHomeVisit= TextUtils.isEmpty(lastHomeVisitStr)?0:Long.parseLong(lastHomeVisitStr);
//        HomeVisit homeVisit = ChwApplication.homeVisitRepository().findByDate(lastHomeVisit);
//
//        String dobString = org.smartregister.util.Utils.getValue(commonPersonObjectClient.getColumnmaps(), DBConstants.KEY.DOB, false);
//        DateTime dob = org.smartregister.chw.util.Utils.dobStringToDateTime(dobString);
//
//        List<Alert> alerts= ChwApplication.getInstance().getContext().alertService().findByEntityIdAndAlertNames(commonPersonObjectClient.entityId(), VaccinateActionUtils.allAlertNames("child"));
//        List<Vaccine> vaccines = ChwApplication.getInstance().vaccineRepository().findLatestTwentyFourHoursByEntityId(commonPersonObjectClient.entityId());
//        Map<String, Date> recievedVaccines = receivedVaccines(vaccines);
//        List<Map<String, Object>> sch = generateScheduleList("child", dob, recievedVaccines, alerts);
//        VaccineTaskModel vaccineTaskModel = new VaccineTaskModel();
//        vaccineTaskModel.setAlerts(alerts);
//        vaccineTaskModel.setVaccines(vaccines);
//        vaccineTaskModel.setReceivedVaccines(recievedVaccines);
//        vaccineTaskModel.setScheduleList(sch);
//        if(homeVisit!=null){
//            try {
//                JSONObject jsonObject = new JSONObject(homeVisit.getVaccineNotGiven().toString());
//                JSONArray array = jsonObject.getJSONArray("vaccineNotGiven");
//                if(array!=null){
//                    ArrayList<VaccineWrapper> notGivenVaccine = ChildUtils.gsonConverter.fromJson(array.toString(),new TypeToken<ArrayList<VaccineWrapper>>(){}.getType());
//                    vaccineTaskModel.setNotGivenVaccine(notGivenVaccine);
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//        ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupsList = homeVisitImmunizationInteractor.determineAllHomeVisitVaccineGroup(vaccineTaskModel.getAlerts(),vaccineTaskModel.getVaccines(),vaccineTaskModel.getNotGivenVaccine(),vaccineTaskModel.getScheduleList());
//        for (Iterator<HomeVisitVaccineGroup> iterator = homeVisitVaccineGroupsList.iterator(); iterator.hasNext(); ) {
//            HomeVisitVaccineGroup homeVisitVaccineGroup = iterator.next();
//            if (homeVisitVaccineGroup.getDueVaccines().size()==0) {
//                iterator.remove();
//            }
//
//        }
//        callBack.updateEditData(homeVisitVaccineGroupsList);
//
//
////        getLastVaccineList(commonPersonObjectClient)
////                .subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(new Observer<VaccineTaskModel>() {
////                    @Override
////                    public void onSubscribe(Disposable d) {
////
////                    }
////
////                    @Override
////                    public void onNext(VaccineTaskModel vaccineTaskModel) {
////                        ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupsList = homeVisitImmunizationInteractor.determineAllHomeVisitVaccineGroup(vaccineTaskModel.getAlerts(),vaccineTaskModel.getVaccines(),vaccineTaskModel.getNotGivenVaccine(),vaccineTaskModel.getScheduleList());
////
////                        for (Iterator<HomeVisitVaccineGroup> iterator = homeVisitVaccineGroupsList.iterator(); iterator.hasNext(); ) {
////                            HomeVisitVaccineGroup homeVisitVaccineGroup = iterator.next();
////                            if (homeVisitVaccineGroup.getDueVaccines().size()==0) {
////                                iterator.remove();
////                            }
////
////                        }
////                        callBack.updateEditData(homeVisitVaccineGroupsList);
////
////                    }
////
////                    @Override
////                    public void onError(Throwable e) {
////
////                    }
////
////                    @Override
////                    public void onComplete() {
////
////                    }
////                });
//
//
//    }
//    private Observable<VaccineTaskModel> getLastVaccineList(final CommonPersonObjectClient commonPersonObjectClient){
//        return Observable.create(new ObservableOnSubscribe<VaccineTaskModel>() {
//            @Override
//            public void subscribe(ObservableEmitter<VaccineTaskModel> emmiter)  {
//                String lastHomeVisitStr=org.smartregister.util.Utils.getValue(commonPersonObjectClient.getColumnmaps(), ChildDBConstants.KEY.LAST_HOME_VISIT, false);
//                long lastHomeVisit= TextUtils.isEmpty(lastHomeVisitStr)?0:Long.parseLong(lastHomeVisitStr);
//                HomeVisit homeVisit = ChwApplication.homeVisitRepository().findByDate(lastHomeVisit);
//
//                String dobString = org.smartregister.util.Utils.getValue(commonPersonObjectClient.getColumnmaps(), DBConstants.KEY.DOB, false);
//                DateTime dob = org.smartregister.chw.util.Utils.dobStringToDateTime(dobString);
//
//                List<Alert> alerts= ChwApplication.getInstance().getContext().alertService().findByEntityIdAndAlertNames(commonPersonObjectClient.entityId(), VaccinateActionUtils.allAlertNames("child"));
//                List<Vaccine> vaccines = ChwApplication.getInstance().vaccineRepository().findLatestTwentyFourHoursByEntityId(commonPersonObjectClient.entityId());
//                Map<String, Date> recievedVaccines = receivedVaccines(vaccines);
//                List<Map<String, Object>> sch = generateScheduleList("child", dob, recievedVaccines, alerts);
//                VaccineTaskModel vaccineTaskModel = new VaccineTaskModel();
//                vaccineTaskModel.setAlerts(alerts);
//                vaccineTaskModel.setVaccines(vaccines);
//                vaccineTaskModel.setReceivedVaccines(recievedVaccines);
//                vaccineTaskModel.setScheduleList(sch);
//                if(homeVisit!=null){
//                    try {
//                        JSONObject jsonObject = new JSONObject(homeVisit.getVaccineNotGiven().toString());
//                        JSONArray array = jsonObject.getJSONArray("vaccineNotGiven");
//                        if (array!=null){
//                            ArrayList<VaccineWrapper> notGivenVaccine = ChildUtils.gsonConverter.fromJson(array.toString(),new TypeToken<ArrayList<VaccineWrapper>>(){}.getType());
//                            vaccineTaskModel.setNotGivenVaccine(notGivenVaccine);
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//                emmiter.onNext(vaccineTaskModel);
////                if(!emmiter.isDisposed()){
////                    emmiter.onComplete();
////                }
//               }
//        });
//    }
//}
