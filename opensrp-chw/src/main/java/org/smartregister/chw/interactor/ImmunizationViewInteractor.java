package org.smartregister.chw.interactor;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.opensrp.chw.core.utils.ChildDBConstants;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.ImmunizationContact;
import org.smartregister.chw.domain.HomeVisit;
import org.smartregister.chw.model.ImmunizationModel;
import org.smartregister.chw.model.VaccineTaskModel;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.ChwServiceSchedule;
import org.smartregister.chw.util.HomeVisitVaccineGroup;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.service.AlertService;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.smartregister.immunization.util.VaccinatorUtils.generateScheduleList;
import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;

public class ImmunizationViewInteractor implements ImmunizationContact.Interactor {

    private ImmunizationModel model;
    private AlertService alertService;
    private VaccineRepository vaccineRepository;

    public ImmunizationViewInteractor() {
        model = new ImmunizationModel();
        alertService = ChwApplication.getInstance().getContext().alertService();
        vaccineRepository = ChwApplication.getInstance().vaccineRepository();
    }

    public List<Vaccine> getVaccines() {
        return model.getVaccines();
    }

    @Override
    public void fetchImmunizationData(final CommonPersonObjectClient commonPersonObjectClient, final ImmunizationContact.InteractorCallBack callBack) {

        getVaccineTask(commonPersonObjectClient, new ArrayList<VaccineWrapper>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VaccineTaskModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.v("fetchImmunizationData -> onSubscribe");
                    }

                    @Override
                    public void onNext(VaccineTaskModel vaccineTaskModel) {
                        ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupsList = model.determineAllHomeVisitVaccineGroup(commonPersonObjectClient, vaccineTaskModel.getAlerts(), vaccineTaskModel.getVaccines(), vaccineTaskModel.getNotGivenVaccine(), vaccineTaskModel.getScheduleList());
                        callBack.updateData(homeVisitVaccineGroupsList, vaccineTaskModel.getReceivedVaccines());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {
                        Timber.v("fetchImmunizationData -> onComplete");
                    }
                });
    }

    @Override
    public void fetchImmunizationEditData(final CommonPersonObjectClient commonPersonObjectClient, final ImmunizationContact.InteractorCallBack callBack) {
        getLastVaccineTask(commonPersonObjectClient, new ArrayList<VaccineWrapper>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VaccineTaskModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.v("fetchImmunizationData -> onSubscribe");
                    }

                    @Override
                    public void onNext(VaccineTaskModel vaccineTaskModel) {
                        ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupsList = model.determineAllHomeVisitVaccineGroup(commonPersonObjectClient, vaccineTaskModel.getAlerts(), vaccineTaskModel.getVaccines(), vaccineTaskModel.getNotGivenVaccine(), vaccineTaskModel.getScheduleList());
                        for (Iterator<HomeVisitVaccineGroup> iterator = homeVisitVaccineGroupsList.iterator(); iterator.hasNext(); ) {
                            HomeVisitVaccineGroup homeVisitVaccineGroup = iterator.next();
                            if (homeVisitVaccineGroup.getDueVaccines().size() == 0) {
                                //iterator.remove();
                                homeVisitVaccineGroup.setViewType(HomeVisitVaccineGroup.TYPE_HIDDEN);
                            }

                        }
                        callBack.updateEditData(homeVisitVaccineGroupsList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {
                        Timber.v("fetchImmunizationData -> onComplete");
                    }
                });
    }

    /**
     * Replacement of previous vaccinationasynctask
     * it'll calculate the received vaccine list of a child.
     *
     * @param childClient
     * @param notDoneVaccines
     * @return
     */
    public Observable<VaccineTaskModel> getLastVaccineTask(final CommonPersonObjectClient childClient, final ArrayList<VaccineWrapper> notDoneVaccines) {

        return Observable.create(new ObservableOnSubscribe<VaccineTaskModel>() {
            @Override
            public void subscribe(ObservableEmitter<VaccineTaskModel> emmiter) throws Exception {
                String lastHomeVisitStr = org.smartregister.util.Utils.getValue(childClient.getColumnmaps(), ChildDBConstants.KEY.LAST_HOME_VISIT, false);
                long lastHomeVisit = TextUtils.isEmpty(lastHomeVisitStr) ? 0 : Long.parseLong(lastHomeVisitStr);
                HomeVisit homeVisit = ChwApplication.homeVisitRepository().findByDate(lastHomeVisit);

                String dobString = org.smartregister.util.Utils.getValue(childClient.getColumnmaps(), DBConstants.KEY.DOB, false);
                DateTime dob = org.smartregister.chw.util.Utils.dobStringToDateTime(dobString);

                List<Alert> alerts = ChwApplication.getInstance().getContext().alertService().findByEntityIdAndAlertNames(childClient.entityId(), VaccinateActionUtils.allAlertNames("child"));
                List<Vaccine> vaccines = ChwApplication.getInstance().vaccineRepository().findLatestTwentyFourHoursByEntityId(childClient.entityId());
                Map<String, Date> recievedVaccines = receivedVaccines(vaccines);
                List<Map<String, Object>> sch = generateScheduleList("child", dob, recievedVaccines, alerts);
                VaccineTaskModel vaccineTaskModel = new VaccineTaskModel();
                vaccineTaskModel.setAlerts(alerts);
                vaccineTaskModel.setVaccines(vaccines);
                vaccineTaskModel.setReceivedVaccines(recievedVaccines);
                vaccineTaskModel.setScheduleList(sch);
                if (homeVisit != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(homeVisit.getVaccineNotGiven().toString());
                        JSONArray array = jsonObject.getJSONArray("vaccineNotGiven");
                        if (array != null) {
                            ArrayList<VaccineWrapper> notGivenVaccine = ChildUtils.gsonConverter.fromJson(array.toString(), new TypeToken<ArrayList<VaccineWrapper>>() {
                            }.getType());
                            vaccineTaskModel.setNotGivenVaccine(notGivenVaccine);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                emmiter.onNext(vaccineTaskModel);
            }
        });
    }


    /**
     * Replacement of previous vaccinationasynctask
     * it'll calculate the received vaccine list of a child.
     *
     * @param childClient
     * @param notDoneVaccines
     * @return
     */
    public Observable<VaccineTaskModel> getVaccineTask(final CommonPersonObjectClient childClient, final ArrayList<VaccineWrapper> notDoneVaccines) {
        final String dobString = org.smartregister.util.Utils.getValue(childClient.getColumnmaps(), DBConstants.KEY.DOB, false);

        return Observable.create(new ObservableOnSubscribe<VaccineTaskModel>() {
            @Override
            public void subscribe(ObservableEmitter<VaccineTaskModel> emmiter) throws Exception {
                DateTime dob = org.smartregister.chw.util.Utils.dobStringToDateTime(dobString);
                if (dob == null) {
                    dob = new DateTime();
                }

                if (!TextUtils.isEmpty(dobString)) {
                    DateTime dateTime = new DateTime(dobString);
                    try {
                        VaccineSchedule.updateOfflineAlerts(childClient.getCaseId(), dateTime, "child");
                    } catch (Exception e) {

                    }
                    try {
                        ChwServiceSchedule.updateOfflineAlerts(childClient.getCaseId(), dateTime);
                    } catch (Exception e) {

                    }
                }
                List<Alert> alerts = alertService.findByEntityIdAndAlertNames(childClient.getCaseId(), VaccinateActionUtils.allAlertNames("child"));
                List<Vaccine> vaccines = vaccineRepository.findByEntityId(childClient.getCaseId());
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
