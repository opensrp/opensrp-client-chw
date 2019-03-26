package org.smartregister.chw.interactor;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.HomeVisitGrowthNutritionContract;
import org.smartregister.chw.domain.HomeVisit;
import org.smartregister.chw.fragment.GrowthNutritionInputFragment;
import org.smartregister.chw.listener.UpdateServiceListener;
import org.smartregister.chw.task.UpdateServiceTask;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.GrowthServiceData;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.AlertStatus;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
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

import static org.smartregister.util.Utils.startAsyncTask;

public class HomeVisitGrowthNutritionInteractor implements HomeVisitGrowthNutritionContract.Interactor {
    private static final String TAG = HomeVisitGrowthNutritionInteractor.class.toString();

    private AppExecutors appExecutors;

    private UpdateServiceTask updateServiceTask;
    private HomeVisit currentHomeVisit = new HomeVisit();

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
                                callBack.updateRecordVisitData(serviceWrapperMap);
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
        String lastHomeVisitStr=org.smartregister.util.Utils.getValue(commonPersonObjectClient.getColumnmaps(), ChildDBConstants.KEY.LAST_HOME_VISIT, false);
        long lastHomeVisit= TextUtils.isEmpty(lastHomeVisitStr)?0:Long.parseLong(lastHomeVisitStr);
        ChwApplication.homeVisitRepository().getHomeVisitData(lastHomeVisit)
                .flatMap(new Function<HomeVisit, ObservableSource<Map<String, ServiceWrapper>>>() {
                    @Override
                    public ObservableSource<Map<String, ServiceWrapper>> apply(HomeVisit homeVisit) throws Exception {
                        currentHomeVisit=homeVisit;
                        return getServiceWrapperMapFromJson(homeVisit.getServicesGiven());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map<String, ServiceWrapper>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map<String, ServiceWrapper> stringServiceWrapperMap) {
                        callBack.updateRecordVisitData(stringServiceWrapperMap);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });




    }
    public Observable<Map<String, ServiceWrapper>> getServiceWrapperMapFromJson(final JSONObject jsonObject){
        return Observable.create(new ObservableOnSubscribe<Map<String, ServiceWrapper>>() {
            @Override
            public void subscribe(ObservableEmitter<Map<String, ServiceWrapper>> e) throws Exception {
                Map<String, ServiceWrapper> serviceWrapperMap = ChildUtils.gsonConverter.fromJson(jsonObject.toString(),new TypeToken<HashMap<String, ServiceWrapper>>(){}.getType());
                e.onNext(serviceWrapperMap);
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
        Object[] displayName = ChildUtils.getStringWithNumber(name);
        if (displayName.length > 1) {
            String str = (String) displayName[0];
            String no = (String) displayName[1];
            if (type.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue())) {
                return str + " " + no + " month";
            } else if (type.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.MNP.getValue())) {
                return str + " " + ChildUtils.getFirstSecondAsNumber(no) + " pack";
            } else {
                return str + " " + ChildUtils.getFirstSecondAsNumber(no) + " dose";
            }
        }
        return "";

    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        //TODO Implement onDestroy
        Log.d(TAG, "onDestroy unimplemented");
    }
}
