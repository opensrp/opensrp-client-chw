package com.opensrp.chw.core.interactor;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.opensrp.chw.core.application.CoreChwApplication;
import com.opensrp.chw.core.contract.HomeVisitGrowthNutritionContract;
import com.opensrp.chw.core.domain.HomeVisit;
import com.opensrp.chw.core.fragment.GrowthNutritionInputFragment;
import com.opensrp.chw.core.listener.UpdateServiceListener;
import com.opensrp.chw.core.model.ServiceTaskModel;
import com.opensrp.chw.core.task.UpdateServiceTask;
import com.opensrp.chw.core.utils.ChildDBConstants;
import com.opensrp.chw.core.utils.CoreChildUtils;
import com.opensrp.chw.core.utils.GrowthServiceData;

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
    private static final String TAG = HomeVisitGrowthNutritionInteractor.class.toString();

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
        UpdateServiceTask updateServiceTask = new UpdateServiceTask(commonPersonObjectClient, new UpdateServiceListener() {
            @Override
            public void onUpdateServiceList(final Map<String, ServiceWrapper> serviceWrapperMap) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callBack.updateGivenRecordVisitData(serviceWrapperMap);
                            }
                        });
                    }
                };
                appExecutors.diskIO().execute(runnable);
            }
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

    public ArrayList<GrowthServiceData> getAllDueService(Map<String, ServiceWrapper> serviceWrapperMap) {
        ArrayList<GrowthServiceData> growthServiceDataList = new ArrayList<>();

        for (String key : serviceWrapperMap.keySet()) {
            ServiceWrapper serviceWrapper = serviceWrapperMap.get(key);
            if (serviceWrapper != null && serviceWrapper.getAlert() != null && !serviceWrapper.getAlert().status().equals(AlertStatus.expired)) {
                GrowthServiceData growthServiceData = new GrowthServiceData();
                growthServiceData.setDate(serviceWrapper.getAlert().startDate());
                growthServiceData.setName(serviceWrapper.getAlert().scheduleName());
                growthServiceData.setDisplayName(getDisplayNameBasedOnType(key, growthServiceData.getName()));
                String duedateString = DateUtil.formatDate(growthServiceData.getDate(), "dd MMM yyyy");
                growthServiceData.setDisplayAbleDate(duedateString);
                growthServiceDataList.add(growthServiceData);
            }

        }
        return growthServiceDataList;
    }

    private String getDisplayNameBasedOnType(String type, String name) {
        Object[] displayName = CoreChildUtils.getStringWithNumber(name);
        if (displayName.length > 1) {
            String str = (String) displayName[0];
            String no = (String) displayName[1];
            if (type.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue())) {
                return str + " " + no + " month";
            } else if (type.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.MNP.getValue())) {
                return str + " " + CoreChildUtils.getFirstSecondAsNumber(no) + " pack";
            } else {
                return str + " " + CoreChildUtils.getFirstSecondAsNumber(no) + " dose";
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
