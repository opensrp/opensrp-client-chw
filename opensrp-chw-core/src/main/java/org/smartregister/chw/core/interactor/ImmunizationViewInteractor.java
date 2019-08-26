package org.smartregister.chw.core.interactor;

import android.content.Context;
import android.text.TextUtils;

import org.joda.time.DateTime;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.ImmunizationContact;
import org.smartregister.chw.core.model.ImmunizationModel;
import org.smartregister.chw.core.model.VaccineTaskModel;
import org.smartregister.chw.core.utils.ChwServiceSchedule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.HomeVisitVaccineGroup;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.service.AlertService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ImmunizationViewInteractor implements ImmunizationContact.Interactor {

    private ImmunizationModel model;
    private AlertService alertService;
    private VaccineRepository vaccineRepository;

    public ImmunizationViewInteractor(Context context) {
        model = new ImmunizationModel(context);
        alertService = CoreChwApplication.getInstance().getContext().alertService();
        vaccineRepository = CoreChwApplication.getInstance().vaccineRepository();
    }

    public List<Vaccine> getVaccines() {
        return model.getVaccines();
    }

    @Override
    public void fetchImmunizationData(final CommonPersonObjectClient commonPersonObjectClient, final ImmunizationContact.InteractorCallBack callBack) {
        getVaccineTask(commonPersonObjectClient, new ArrayList<>())
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

        return Observable.create(emmiter -> {
            DateTime dob = Utils.dobStringToDateTime(dobString);
            if (dob == null) {
                dob = new DateTime();
            }

            if (!TextUtils.isEmpty(dobString)) {
                DateTime dateTime = new DateTime(dobString);
                try {
                    VaccineSchedule.updateOfflineAlerts(childClient.getCaseId(), dateTime, CoreConstants.SERVICE_GROUPS.CHILD);
                } catch (Exception e) {

                }
                try {
                    ChwServiceSchedule.updateOfflineAlerts(childClient.getCaseId(), dateTime, CoreConstants.SERVICE_GROUPS.CHILD);
                } catch (Exception e) {

                }
            }
            List<Alert> alerts = alertService.findByEntityIdAndAlertNames(childClient.getCaseId(), VaccinateActionUtils.allAlertNames(CoreConstants.SERVICE_GROUPS.CHILD));
            List<Vaccine> vaccines = vaccineRepository.findByEntityId(childClient.getCaseId());
            Map<String, Date> recievedVaccines = VaccinatorUtils.receivedVaccines(vaccines);
            int size = notDoneVaccines.size();
            for (int i = 0; i < size; i++) {
                recievedVaccines.put(notDoneVaccines.get(i).getName().toLowerCase(), new Date());
            }

            List<Map<String, Object>> sch = VaccinatorUtils.generateScheduleList(CoreConstants.SERVICE_GROUPS.CHILD, dob, recievedVaccines, alerts);
            VaccineTaskModel vaccineTaskModel = new VaccineTaskModel();
            vaccineTaskModel.setAlerts(alerts);
            vaccineTaskModel.setVaccines(vaccines);
            vaccineTaskModel.setReceivedVaccines(recievedVaccines);
            vaccineTaskModel.setScheduleList(sch);
            emmiter.onNext(vaccineTaskModel);
        });
    }
}
