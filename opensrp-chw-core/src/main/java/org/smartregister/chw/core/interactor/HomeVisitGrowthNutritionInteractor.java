package org.smartregister.chw.core.interactor;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.HomeVisitGrowthNutritionContract;
import org.smartregister.chw.core.domain.HomeVisit;
import org.smartregister.chw.core.fragment.GrowthNutritionInputFragment;
import org.smartregister.chw.core.model.ServiceTaskModel;
import org.smartregister.chw.core.task.UpdateServiceTask;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.GrowthServiceData;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.AlertStatus;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.smartregister.util.Utils.startAsyncTask;

public class HomeVisitGrowthNutritionInteractor implements HomeVisitGrowthNutritionContract.Interactor {

    private AppExecutors appExecutors;

    @VisibleForTesting
    HomeVisitGrowthNutritionInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public HomeVisitGrowthNutritionInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void parseRecordServiceData(final CommonPersonObjectClient commonPersonObjectClient, final HomeVisitGrowthNutritionContract.InteractorCallBack callBack) {
        UpdateServiceTask updateServiceTask = new UpdateServiceTask(commonPersonObjectClient, serviceWrapperMap -> {
            Runnable runnable = () -> appExecutors.mainThread().execute(() -> callBack.updateGivenRecordVisitData(serviceWrapperMap));
            appExecutors.diskIO().execute(runnable);
        });
        startAsyncTask(updateServiceTask, null);
    }

    @Override
    public void parseEditRecordServiceData(CommonPersonObjectClient commonPersonObjectClient, final HomeVisitGrowthNutritionContract.InteractorCallBack callBack) {

        getServiceWrapperMapFromLastHomeVisit(commonPersonObjectClient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ServiceTaskModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ServiceTaskModel serviceTaskModel) {
                        if (serviceTaskModel == null) {
                            callBack.allDataLoaded();
                        } else {
                            callBack.updateGivenRecordVisitData(serviceTaskModel.getGivenServiceMap());
                            callBack.updateNotGivenRecordVisitData(serviceTaskModel.getNotGivenServiceMap());
                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    public Observable<ServiceTaskModel> getServiceWrapperMapFromLastHomeVisit(final CommonPersonObjectClient commonPersonObjectClient) {
        return Observable.create(new ObservableOnSubscribe<ServiceTaskModel>() {
            @Override
            public void subscribe(ObservableEmitter<ServiceTaskModel> e) throws Exception {
                String lastHomeVisitStr = org.smartregister.util.Utils.getValue(commonPersonObjectClient.getColumnmaps(), ChildDBConstants.KEY.LAST_HOME_VISIT, false);
                long lastHomeVisit = TextUtils.isEmpty(lastHomeVisitStr) ? 0 : Long.parseLong(lastHomeVisitStr);
                HomeVisit homeVisit = CoreChwApplication.homeVisitRepository().findByDate(lastHomeVisit);
                if (homeVisit != null) {
                    Map<String, ServiceWrapper> serviceGivenWrapper = CoreChildUtils.gsonConverter.fromJson(homeVisit.getServicesGiven().toString(), new TypeToken<HashMap<String, ServiceWrapper>>() {
                    }.getType());
                    Map<String, ServiceWrapper> serviceNotGivenWrapper = CoreChildUtils.gsonConverter.fromJson(homeVisit.getServiceNotGiven().toString(), new TypeToken<HashMap<String, ServiceWrapper>>() {
                    }.getType());
                    ServiceTaskModel serviceTaskModel = new ServiceTaskModel();
                    serviceTaskModel.setGivenServiceMap(serviceGivenWrapper);
                    serviceTaskModel.setNotGivenServiceMap(serviceNotGivenWrapper);
                    e.onNext(serviceTaskModel);
                } else {
                    e.onNext(null);
                }

            }
        });
    }

    public ArrayList<GrowthServiceData> getAllDueService(Map<String, ServiceWrapper> serviceWrapperMap, Context context) {
        ArrayList<GrowthServiceData> growthServiceDataList = new ArrayList<>();

        for (String key : serviceWrapperMap.keySet()) {
            ServiceWrapper serviceWrapper = serviceWrapperMap.get(key);
            if (serviceWrapper != null && serviceWrapper.getAlert() != null && !serviceWrapper.getAlert().status().equals(AlertStatus.expired)) {
                GrowthServiceData growthServiceData = new GrowthServiceData();
                growthServiceData.setDate(serviceWrapper.getAlert().startDate());
                growthServiceData.setName(serviceWrapper.getAlert().scheduleName());
                growthServiceData.setDisplayName(getDisplayNameBasedOnType(key, growthServiceData.getName(), context));
                String duedateString = DateUtil.formatDate(growthServiceData.getDate(), "dd MMM yyyy");
                growthServiceData.setDisplayAbleDate(duedateString);
                growthServiceDataList.add(growthServiceData);
            }

        }
        return growthServiceDataList;
    }

    private String getDisplayNameBasedOnType(String type, String name, Context context) {
        Object[] displayName = CoreChildUtils.getStringWithNumber(name);
        if (displayName.length > 1) {
            String str = (String) displayName[0];
            String no = (String) displayName[1];
            if (type.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue())) {
                return Utils.getServiceTypeLanguageSpecific(context, str) + " " + no + " " + context.getString(R.string.month);
            } else if (type.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.MNP.getValue())) {
                return Utils.getServiceTypeLanguageSpecific(context, str) + " " + CoreChildUtils.getFirstSecondAsNumber(no, context) + " " + context.getString(R.string.visit_pack);
            } else {
                return Utils.getServiceTypeLanguageSpecific(context, str) + " " + CoreChildUtils.getFirstSecondAsNumber(no, context) + " " + context.getString(R.string.visit_dose);
            }
        }
        return "";

    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        //TODO Implement onDestroy
        Timber.d("onDestroy unimplemented");
    }
}
