package org.smartgresiter.wcaro.interactor;

import android.support.annotation.VisibleForTesting;

import org.smartgresiter.wcaro.contract.HomeVisitGrowthNutritionContract;
import org.smartgresiter.wcaro.fragment.GrowthNutritionInputFragment;
import org.smartgresiter.wcaro.listener.UpdateServiceListener;
import org.smartgresiter.wcaro.task.UpdateServiceTask;
import org.smartgresiter.wcaro.util.ChildUtils;
import org.smartgresiter.wcaro.util.GrowthServiceData;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.AlertStatus;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.Map;

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

    }
}
