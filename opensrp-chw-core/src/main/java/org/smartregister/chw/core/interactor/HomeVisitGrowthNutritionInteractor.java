package org.smartregister.chw.core.interactor;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.contract.HomeVisitGrowthNutritionContract;
import org.smartregister.chw.core.task.UpdateServiceTask;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.GrowthServiceData;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.AlertStatus;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.Map;

import timber.log.Timber;

public class HomeVisitGrowthNutritionInteractor implements HomeVisitGrowthNutritionContract.Interactor {

    private AppExecutors appExecutors;

    public HomeVisitGrowthNutritionInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    HomeVisitGrowthNutritionInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void parseRecordServiceData(final CommonPersonObjectClient commonPersonObjectClient, final HomeVisitGrowthNutritionContract.InteractorCallBack callBack) {
        UpdateServiceTask updateServiceTask = new UpdateServiceTask(commonPersonObjectClient, serviceWrapperMap -> {
            Runnable runnable = () -> appExecutors.mainThread().execute(() -> callBack.updateGivenRecordVisitData(serviceWrapperMap));
            appExecutors.diskIO().execute(runnable);
        });
        org.smartregister.util.Utils.startAsyncTask(updateServiceTask, null);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        //TODO Implement onDestroy
        Timber.d("onDestroy unimplemented");
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
            if (type.equalsIgnoreCase(CoreConstants.GROWTH_TYPE.EXCLUSIVE.getValue())) {
                return Utils.getServiceTypeLanguageSpecific(context, str) + " " + no + " " + context.getString(R.string.month);
            } else if (type.equalsIgnoreCase(CoreConstants.GROWTH_TYPE.MNP.getValue())) {
                return Utils.getServiceTypeLanguageSpecific(context, str) + " " + CoreChildUtils.getFirstSecondAsNumber(no, context) + " " + context.getString(R.string.visit_pack);
            } else {
                return Utils.getServiceTypeLanguageSpecific(context, str) + " " + CoreChildUtils.getFirstSecondAsNumber(no, context) + " " + context.getString(R.string.visit_dose);
            }
        }
        return "";

    }
}
