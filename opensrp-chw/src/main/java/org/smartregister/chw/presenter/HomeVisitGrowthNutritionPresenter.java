package org.smartregister.chw.presenter;

import android.content.Context;

import org.smartregister.chw.R;
import org.smartregister.chw.contract.HomeVisitGrowthNutritionContract;
import org.smartregister.chw.fragment.GrowthNutritionInputFragment;
import org.smartregister.chw.interactor.HomeVisitGrowthNutritionInteractor;
import org.smartregister.chw.util.Utils;
import org.smartregister.chw.util.ChwServiceSchedule;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class HomeVisitGrowthNutritionPresenter implements HomeVisitGrowthNutritionContract.Presenter, HomeVisitGrowthNutritionContract.InteractorCallBack {
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
    private int growthListCount = -1;
    private Context context;

    public HomeVisitGrowthNutritionPresenter(HomeVisitGrowthNutritionContract.View view) {
        this.view = new WeakReference<>(view);
        context = view.getViewContext();
        interactor = new HomeVisitGrowthNutritionInteractor();
    }


    @Override
    public void parseRecordServiceData(CommonPersonObjectClient commonPersonObjectClient,boolean isEditMode) {
        this.commonPersonObjectClient = commonPersonObjectClient;
        if(isEditMode){
            interactor.parseEditRecordServiceData(commonPersonObjectClient,this);
        }else{
            interactor.parseRecordServiceData(commonPersonObjectClient, this);
        }

    }

    @Override
    public void setSaveState(String type, ServiceWrapper serviceWrapper) {
        saveStateMap.put(type, serviceWrapper);
        saveServiceMap.put(type, serviceWrapper.getAlert().scheduleName());
        if (type.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue())) {
            Date date = org.smartregister.family.util.Utils.dobStringToDate(serviceWrapper.getUpdatedVaccineDateAsString());
            if (getView() != null)
                getView().statusImageViewUpdate(type, true, context.getString(R.string.given_on, Utils.dd_MMM_yyyy.format(date)), serviceWrapper.getValue());
        } else {
            Date date = org.smartregister.family.util.Utils.dobStringToDate(serviceWrapper.getUpdatedVaccineDateAsString());
            if (getView() != null)
                getView().statusImageViewUpdate(type, true, context.getString(R.string.given_on, Utils.dd_MMM_yyyy.format(date)), "");

        }
    }

    @Override
    public void setNotVisitState(String type, ServiceWrapper serviceWrapper) {

        if (isSave(type)) return;
        notVisitStateMap.put(type, serviceWrapper);
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

    @Override
    public boolean isSelected(String type) {
        for (String key : saveStateMap.keySet()) {
            if (key.equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAllSelected() {
        if (growthListCount == (saveStateMap.size() + notVisitStateMap.size())) {
            return true;
        } else {
            return false;
        }

    }


    @Override
    public void updateRecordVisitData(Map<String, ServiceWrapper> stringServiceWrapperMap) {
        growthListCount = 0;
        serviceWrapperMap = stringServiceWrapperMap;
        serviceWrapperExclusive = getServiceWrapperByType(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue());
        if (serviceWrapperExclusive != null) {
            Alert alert = serviceWrapperExclusive.getAlert();
            if (alert != null && !alert.status().equals(AlertStatus.expired)) {
                growthListCount++;

                if (getView() != null)
                    getView().updateExclusiveFeedingData(alert.scheduleName(), alert.startDate());
            } else {
                String lastDoneExclusive = serviceWrapperExclusive.getServiceType().getName();

            }
        }
        serviceWrapperMnp = getServiceWrapperByType(GrowthNutritionInputFragment.GROWTH_TYPE.MNP.getValue());
        if (serviceWrapperMnp != null) {
            Alert alert = serviceWrapperMnp.getAlert();
            if (alert != null) {
                growthListCount++;

                if (getView() != null)
                    getView().updateMnpData(alert.scheduleName(), alert.startDate());
            } else {
                String lastDoneExclusive = serviceWrapperMnp.getServiceType().getName();

            }
        }
        serviceWrapperVitamin = getServiceWrapperByType(GrowthNutritionInputFragment.GROWTH_TYPE.VITAMIN.getValue());
        if (serviceWrapperVitamin != null) {
            Alert alert = serviceWrapperVitamin.getAlert();
            if (alert != null) {
                growthListCount++;

                if (getView() != null)
                    getView().updateVitaminAData(alert.scheduleName(), alert.startDate());
            } else {
                String lastDoneExclusive = serviceWrapperVitamin.getServiceType().getName();

            }

        }
        serviceWrapperDeworming = getServiceWrapperByType(GrowthNutritionInputFragment.GROWTH_TYPE.DEWORMING.getValue());
        if (serviceWrapperDeworming != null) {
            Alert alert = serviceWrapperDeworming.getAlert();
            if (alert != null) {
                growthListCount++;

                if (getView() != null)
                    getView().updateDewormingData(alert.scheduleName(), alert.startDate());
            } else {
                String lastDoneVitamin = serviceWrapperDeworming.getServiceType().getName();

            }
        }
        getView().allDataLoaded();
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
        return serviceWrapperExclusive;
    }

    public ServiceWrapper getServiceWrapperMnp() {
        return serviceWrapperMnp;
    }

    public ServiceWrapper getServiceWrapperVitamin() {
        return serviceWrapperVitamin;
    }

    public ServiceWrapper getServiceWrapperDeworming() {
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
}
