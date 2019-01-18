package org.smartgresiter.wcaro.interactor;

import android.support.annotation.VisibleForTesting;

import org.smartgresiter.wcaro.contract.HomeVisitGrowthNutritionContract;
import org.smartgresiter.wcaro.listener.UpdateServiceListener;
import org.smartgresiter.wcaro.task.UpdateServiceTask;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.util.Map;

import static org.smartregister.util.Utils.startAsyncTask;

public class HomeVisitGrowthNutritionInteractor implements HomeVisitGrowthNutritionContract.Interactor {
    private AppExecutors appExecutors;
    private UpdateServiceTask updateServiceTask;

    @VisibleForTesting
    HomeVisitGrowthNutritionInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public HomeVisitGrowthNutritionInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void parseRecordServiceData(final CommonPersonObjectClient commonPersonObjectClient, final HomeVisitGrowthNutritionContract.InteractorCallBack callBack) {
        updateServiceTask = new UpdateServiceTask(commonPersonObjectClient, new UpdateServiceListener() {
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
    public void onDestroy(boolean isChangingConfiguration) {

    }
}
