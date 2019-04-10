package org.smartregister.chw.interactor;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.ImmunizationEditContract;
import org.smartregister.chw.domain.HomeVisit;
import org.smartregister.chw.model.VaccineTaskModel;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.HomeVisitVaccineGroupDetails;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.util.VaccinateActionUtils;
import java.util.ArrayList;
import java.util.Date;
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
        HomeVisitImmunizationInteractor homeVisitImmunizationInteractor = new HomeVisitImmunizationInteractor();


        getLastVaccineList(commonPersonObjectClient)
                .flatMap(new Function<VaccineTaskModel, ObservableSource<ArrayList<HomeVisitVaccineGroupDetails>>>() {
            @Override
            public ObservableSource<ArrayList<HomeVisitVaccineGroupDetails>> apply(VaccineTaskModel vaccineTaskModel) throws Exception {
                         return determineAllHomeVisitVaccineGroupDetails(callBack,vaccineTaskModel.getAlerts(),vaccineTaskModel.getVaccines(),vaccineTaskModel.getScheduleList());
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
            public void subscribe(ObservableEmitter<VaccineTaskModel> emmiter)  {
                String lastHomeVisitStr=org.smartregister.util.Utils.getValue(commonPersonObjectClient.getColumnmaps(), ChildDBConstants.KEY.LAST_HOME_VISIT, false);
                long lastHomeVisit= TextUtils.isEmpty(lastHomeVisitStr)?0:Long.parseLong(lastHomeVisitStr);
                HomeVisit homeVisit = ChwApplication.homeVisitRepository().findByDate(lastHomeVisit);

                String dobString = org.smartregister.util.Utils.getValue(commonPersonObjectClient.getColumnmaps(), DBConstants.KEY.DOB, false);
                DateTime dob = org.smartregister.chw.util.Utils.dobStringToDateTime(dobString);

                List<Alert> alerts= ChwApplication.getInstance().getContext().alertService().findByEntityIdAndAlertNames(commonPersonObjectClient.entityId(), VaccinateActionUtils.allAlertNames("child"));
                List<Vaccine> vaccines = ChwApplication.getInstance().vaccineRepository().findByEntityId(commonPersonObjectClient.entityId());
                Map<String, Date> recievedVaccines = receivedVaccines(vaccines);
                List<Map<String, Object>> sch = generateScheduleList("child", dob, recievedVaccines, alerts);
                VaccineTaskModel vaccineTaskModel = new VaccineTaskModel();
                vaccineTaskModel.setAlerts(alerts);
                vaccineTaskModel.setVaccines(vaccines);
                vaccineTaskModel.setReceivedVaccines(recievedVaccines);
                vaccineTaskModel.setScheduleList(sch);
                if(homeVisit!=null){
                    ArrayList<VaccineWrapper> notGivenVaccine = ChildUtils.gsonConverter.fromJson(homeVisit.getSingleVaccinesGiven().toString(),new TypeToken<ArrayList<VaccineWrapper>>(){}.getType());
                    vaccineTaskModel.setNotGivenVaccine(notGivenVaccine);
                }
                emmiter.onNext(vaccineTaskModel);
//                if(!emmiter.isDisposed()){
//                    emmiter.onComplete();
//                }
               }
        });
    }
    private Observable<ArrayList<HomeVisitVaccineGroupDetails>>determineAllHomeVisitVaccineGroupDetails(final ImmunizationEditContract.InteractorCallBack callBack,final List<Alert> alerts,final List<Vaccine> vaccines,final List<Map<String, Object>> sch) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<HomeVisitVaccineGroupDetails>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<HomeVisitVaccineGroupDetails>> emitter) throws Exception {

            }
        });
    }
}
