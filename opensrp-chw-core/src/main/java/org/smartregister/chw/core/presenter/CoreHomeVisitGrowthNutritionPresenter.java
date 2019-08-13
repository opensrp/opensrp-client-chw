package org.smartregister.chw.core.presenter;

import android.content.Context;

import com.opensrp.chw.core.R;
import org.smartregister.chw.core.contract.HomeVisitGrowthNutritionContract;
import org.smartregister.chw.core.fragment.GrowthNutritionInputFragment;
import org.smartregister.chw.core.interactor.HomeVisitGrowthNutritionInteractor;
import org.smartregister.chw.core.utils.ChwServiceSchedule;
import org.smartregister.chw.core.utils.Utils;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class CoreHomeVisitGrowthNutritionPresenter implements HomeVisitGrowthNutritionContract.Presenter, HomeVisitGrowthNutritionContract.InteractorCallBack {
    private static Flavor homeVisitGrowthNutritionPresenterFlv = null;
    private WeakReference<HomeVisitGrowthNutritionContract.View> view;
    private HomeVisitGrowthNutritionContract.Interactor interactor;
    private Map<String, ServiceWrapper> serviceWrapperMap = new LinkedHashMap<>();
    private ServiceWrapper serviceWrapperExclusive;
    private ServiceWrapper serviceWrapperMnp;
    private ServiceWrapper serviceWrapperVitamin;
    private ServiceWrapper serviceWrapperDeworming;
    private Map<String, ServiceWrapper> saveStateMap = new LinkedHashMap<>();
    private Map<String, String> saveServiceMap = new LinkedHashMap<>();
    private Map<String, ServiceWrapper> notVisitStateMap = new LinkedHashMap<>();
    private CommonPersonObjectClient commonPersonObjectClient;
    private ArrayList<String> saveGroupList = new ArrayList<>();
    private boolean isEditMode = false;
    private Context context;
    private int initialCount = 0;

    public CoreHomeVisitGrowthNutritionPresenter(HomeVisitGrowthNutritionContract.View view) {
        this.view = new WeakReference<>(view);
        context = view.getViewContext();
        interactor = new HomeVisitGrowthNutritionInteractor();
    }


    @Override
    public void parseRecordServiceData(CommonPersonObjectClient commonPersonObjectClient, boolean isEditMode) {
        this.commonPersonObjectClient = commonPersonObjectClient;
        this.isEditMode = isEditMode;
        if (isEditMode) {
            interactor.parseEditRecordServiceData(commonPersonObjectClient, this);
        } else {
            interactor.parseRecordServiceData(commonPersonObjectClient, this);
        }

    }

    @Override
    public void setSaveState(String type, ServiceWrapper serviceWrapper) {
        saveStateMap.put(type, serviceWrapper);
        if (!saveGroupList.contains(type)) {
            saveGroupList.add(type);
        }
        saveServiceMap.put(type, serviceWrapper.getAlert().scheduleName());
        if (type.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue())) {
            Date date = org.smartregister.family.util.Utils.dobStringToDate(serviceWrapper.getUpdatedVaccineDateAsString());
            if (getView() != null)
                getView().statusImageViewUpdate(type, true, context.getString(R.string.given_on, Utils.DD_MM_YYYY.format(date)), serviceWrapper.getValue());
        } else {
            Date date = org.smartregister.family.util.Utils.dobStringToDate(serviceWrapper.getUpdatedVaccineDateAsString());
            if (getView() != null)
                getView().statusImageViewUpdate(type, true, context.getString(R.string.given_on, Utils.DD_MM_YYYY.format(date)), "");

        }
    }

    @Override
    public void setNotVisitState(String type, ServiceWrapper serviceWrapper) {

        if (isSave(type)) return;
        notVisitStateMap.put(type, serviceWrapper);
        if (!saveGroupList.contains(type)) {
            saveGroupList.add(type);
        }
        if (getView() != null)
            getView().statusImageViewUpdate(type, false, context.getString(R.string.not_given), "");

    }

    public Observable undoGrowthData() {
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                RecurringServiceRecordRepository recurringServiceRecordRepository = ImmunizationLibrary.getInstance().recurringServiceRecordRepository();

                for (String type : saveStateMap.keySet()) {
                    ServiceWrapper serviceWrapper = saveStateMap.get(type);
                    if (serviceWrapper != null) {
                        recurringServiceRecordRepository.deleteServiceRecord(serviceWrapper.getDbKey());
                        ChwServiceSchedule.updateOfflineAlerts(serviceWrapper.getType(), commonPersonObjectClient.entityId(), Utils.dobToDateTime(commonPersonObjectClient));
                    }

                }
                e.onComplete();
            }
        });

    }

    public static Flavor getHomeVisitGrowthNutritionPresenterFlv() {
        return homeVisitGrowthNutritionPresenterFlv;
    }

    public static void setHomeVisitGrowthNutritionPresenterFlv(Flavor homeVisitGrowthNutritionPresenterFlv) {
        CoreHomeVisitGrowthNutritionPresenter.homeVisitGrowthNutritionPresenterFlv = homeVisitGrowthNutritionPresenterFlv;
    }

    public boolean isAllSelected() {
        Log.logError("SUBMIT_BUTTON", "isAllSelected>>" + saveGroupList.size() + ": " + initialCount);
        return saveGroupList.size() == initialCount;

    }

    @Override
    public void updateNotGivenRecordVisitData(Map<String, ServiceWrapper> stringServiceWrapperMap) {
        updateData(stringServiceWrapperMap);
        for (String type : stringServiceWrapperMap.keySet()) {
            ServiceWrapper serviceWrapper = stringServiceWrapperMap.get(type);
            setNotVisitState(type, serviceWrapper);
        }

    }

    @Override
    public void allDataLoaded() {
        getView().allDataLoaded();
    }

    @Override
    public void updateGivenRecordVisitData(Map<String, ServiceWrapper> stringServiceWrapperMap) {
        getView().allDataLoaded();
        updateData(stringServiceWrapperMap);
        if (isEditMode) {
            for (String type : stringServiceWrapperMap.keySet()) {
                ServiceWrapper serviceWrapper = stringServiceWrapperMap.get(type);
                setSaveState(type, serviceWrapper);
            }
        }
    }

    private void updateData(Map<String, ServiceWrapper> stringServiceWrapperMap) {
        saveGroupList.clear();
        initialCount = 0;
        serviceWrapperMap = stringServiceWrapperMap;
        serviceWrapperExclusive = getServiceWrapperByType(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue());
        if (serviceWrapperExclusive != null) {
            Alert alert = serviceWrapperExclusive.getAlert();
            if (alert != null && !alert.status().equals(AlertStatus.expired)) {
                initialCount++;
                if (getView() != null)
                    getView().updateExclusiveFeedingData(alert.scheduleName(), alert.startDate());
            } else {
                // String lastDoneExclusive = serviceWrapperExclusive.getServiceType().getName();

            }
        }
        serviceWrapperMnp = getServiceWrapperByType(GrowthNutritionInputFragment.GROWTH_TYPE.MNP.getValue());
        if (serviceWrapperMnp != null && homeVisitGrowthNutritionPresenterFlv.hasMNP()) {
            Alert alert = serviceWrapperMnp.getAlert();
            if (alert != null) {
                initialCount++;
                if (getView() != null)
                    getView().updateMnpData(alert.scheduleName(), alert.startDate());
            } else {
                //  String lastDoneExclusive = serviceWrapperMnp.getServiceType().getName();

            }
        }
        serviceWrapperVitamin = getServiceWrapperByType(GrowthNutritionInputFragment.GROWTH_TYPE.VITAMIN.getValue());
        if (serviceWrapperVitamin != null) {
            Alert alert = serviceWrapperVitamin.getAlert();
            if (alert != null) {
                initialCount++;
                if (getView() != null)
                    getView().updateVitaminAData(alert.scheduleName(), alert.startDate());
            } else {
                //String lastDoneExclusive = serviceWrapperVitamin.getServiceType().getName();

            }

        }
        serviceWrapperDeworming = getServiceWrapperByType(GrowthNutritionInputFragment.GROWTH_TYPE.DEWORMING.getValue());
        if (serviceWrapperDeworming != null) {
            Alert alert = serviceWrapperDeworming.getAlert();
            if (alert != null) {
                initialCount++;
                if (getView() != null)
                    getView().updateDewormingData(alert.scheduleName(), alert.startDate());
            } else {
                //String lastDoneVitamin = serviceWrapperDeworming.getServiceType().getName();

            }
        }
    }

    public ServiceWrapper getServiceWrapperByType(String type) {
        if (serviceWrapperMap != null) {
            try {
                return serviceWrapperMap.get(type);
            } catch (Exception e) {

            }

        }
        return null;

    }

    private boolean isSave(String type) {
        for (String savedType : saveStateMap.keySet()) {
            if (savedType.equalsIgnoreCase(type)) {
                return true;
            }

        }
        return false;
    }


    public ServiceWrapper getServiceWrapperExclusive() {
        if (isEditMode) {
            if (isSave(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue())) {
                return saveStateMap.get(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue());
            }
        }
        return serviceWrapperExclusive;
    }

    public ServiceWrapper getServiceWrapperMnp() {
        if (isEditMode && isSave(GrowthNutritionInputFragment.GROWTH_TYPE.MNP.getValue())) {
            return saveStateMap.get(GrowthNutritionInputFragment.GROWTH_TYPE.MNP.getValue());
        }
        return serviceWrapperMnp;
    }

    public ServiceWrapper getServiceWrapperVitamin() {
        if (isEditMode && isSave(GrowthNutritionInputFragment.GROWTH_TYPE.VITAMIN.getValue())) {
            return saveStateMap.get(GrowthNutritionInputFragment.GROWTH_TYPE.VITAMIN.getValue());
        }
        return serviceWrapperVitamin;
    }

    public ServiceWrapper getServiceWrapperDeworming() {
        if (isEditMode && isSave(GrowthNutritionInputFragment.GROWTH_TYPE.DEWORMING.getValue())) {
            return saveStateMap.get(GrowthNutritionInputFragment.GROWTH_TYPE.DEWORMING.getValue());
        }
        return serviceWrapperDeworming;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        view = null;//set to null on destroy

        // Inform interactor
        interactor.onDestroy(isChangingConfiguration);

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            interactor = null;
        }
    }

    @Override
    public HomeVisitGrowthNutritionContract.View getView() {
        if (view != null) {
            return view.get();
        } else {
            return null;
        }
    }

    @Override
    public Map<String, ServiceWrapper> getSaveStateMap() {
        return saveStateMap;
    }

    @Override
    public Map<String, ServiceWrapper> getNotSaveStateMap() {
        return notVisitStateMap;
    }


    public interface Flavor {

        boolean hasMNP();

    }
}
