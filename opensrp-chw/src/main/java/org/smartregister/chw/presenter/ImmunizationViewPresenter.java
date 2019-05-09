package org.smartregister.chw.presenter;

import android.text.TextUtils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.smartregister.chw.contract.ImmunizationContact;
import org.smartregister.chw.interactor.ImmunizationViewInteractor;
import org.smartregister.chw.util.HomeVisitVaccineGroup;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.Utils;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class ImmunizationViewPresenter implements ImmunizationContact.Presenter, ImmunizationContact.InteractorCallBack {

    private WeakReference<ImmunizationContact.View> view;
    private ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetails = new ArrayList<>();
    private ImmunizationViewInteractor interactor;
    private ArrayList<VaccineWrapper> vaccinesGivenInitially = new ArrayList<VaccineWrapper>();
    private ArrayList<VaccineWrapper> vaccinesGivenThisVisit = new ArrayList<VaccineWrapper>();
    private ArrayList<VaccineWrapper> givenGroupWiseVaccines = new ArrayList<VaccineWrapper>();
    private ArrayList<VaccineWrapper> notGivenVaccines = new ArrayList<VaccineWrapper>();
    private ArrayList<VaccineWrapper> notGivenGroupWiseVaccines = new ArrayList<VaccineWrapper>();
    private ArrayList<String> saveGroupList = new ArrayList<>();
    private final VaccineRepository vaccineRepository;
    private String groupName = "";

    public ImmunizationViewPresenter(ImmunizationContact.View view) {
        this.view = new WeakReference<>(view);
        interactor = new ImmunizationViewInteractor();
        vaccineRepository = ImmunizationLibrary.getInstance().vaccineRepository();
    }

    public ImmunizationViewPresenter() {
        interactor = new ImmunizationViewInteractor();
        vaccineRepository = ImmunizationLibrary.getInstance().vaccineRepository();
    }

    public ArrayList<HomeVisitVaccineGroup> getHomeVisitVaccineGroupDetails() {
        return homeVisitVaccineGroupDetails;
    }

    @Override
    public void fetchImmunizationData(CommonPersonObjectClient commonPersonObjectClient,String groupName) {
        this.groupName = groupName;
        interactor.fetchImmunizationData(commonPersonObjectClient, this);
    }

    @Override
    public void fetchImmunizationEditData(CommonPersonObjectClient commonPersonObjectClient) {
        interactor.fetchImmunizationEditData(commonPersonObjectClient, this);

    }

    public void upcomingServiceFetch(CommonPersonObjectClient commonPersonObjectClient, ImmunizationContact.InteractorCallBack callBack) {
        interactor.fetchImmunizationData(commonPersonObjectClient, callBack);
    }

    @Override
    public ImmunizationContact.View getView() {
        if (view != null) {
            return view.get();
        } else {
            return null;
        }
    }

    @Override
    public void updateData(ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetails, Map<String, Date> vaccines) {


        if(!TextUtils.isEmpty(groupName)){
            saveGroupList.remove(groupName);
            for(HomeVisitVaccineGroup homeVisitVaccineGroup:homeVisitVaccineGroupDetails){
                if(isUpdateRow(homeVisitVaccineGroup)){
                    break;
                }
                Log.logError("SUBMIT_CHECK","updateData>>");

            }
            getView().onUpdateNextPosition();

            getView().updateSubmitBtn();
        }else{
            this.homeVisitVaccineGroupDetails = homeVisitVaccineGroupDetails;
            for (int i = 0; i < this.homeVisitVaccineGroupDetails.size(); i++) {
                HomeVisitVaccineGroup homeVisitVaccineGroup = this.homeVisitVaccineGroupDetails.get(i);
                if (i == 0) {
                    homeVisitVaccineGroup.setViewType(HomeVisitVaccineGroup.TYPE_INITIAL);
                } else {
                    homeVisitVaccineGroup.setViewType(HomeVisitVaccineGroup.TYPE_INACTIVE);
                }
            }
            getView().allDataLoaded();
            getView().updateAdapter(0);

            getView().updateSubmitBtn();
        }

    }
    private boolean isUpdateRow(HomeVisitVaccineGroup homeVisitVaccineGroup){
        int size = this.homeVisitVaccineGroupDetails.size();
        for(int i=0;i<size;i++){
            HomeVisitVaccineGroup hhh = this.homeVisitVaccineGroupDetails.get(i);
            if(hhh.getGroup().equalsIgnoreCase(homeVisitVaccineGroup.getGroup()) && homeVisitVaccineGroup.getGroup().equalsIgnoreCase(groupName)){
                this.homeVisitVaccineGroupDetails.set(i,homeVisitVaccineGroup);
                if(this.homeVisitVaccineGroupDetails.get(i).getDueVaccines().size() == 0)this.homeVisitVaccineGroupDetails.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateEditData(ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetails) {
        getView().allDataLoaded();
        this.homeVisitVaccineGroupDetails = homeVisitVaccineGroupDetails;
        for (HomeVisitVaccineGroup homeVisitVaccineGroup : this.homeVisitVaccineGroupDetails) {
            homeVisitVaccineGroup.setViewType(HomeVisitVaccineGroup.TYPE_ACTIVE);
        }
        getView().updateAdapter(0);
    }

    public ArrayList<VaccineWrapper> getDueVaccineWrappers(HomeVisitVaccineGroup duevaccines) {

        ArrayList<VaccineWrapper> vaccineWrappers = new ArrayList<VaccineWrapper>();
        for (VaccineRepo.Vaccine vaccine : duevaccines.getDueVaccines()) {
            VaccineWrapper vaccineWrapper = new VaccineWrapper();
            vaccineWrapper.setVaccine(vaccine);
            vaccineWrapper.setName(vaccine.display());
            Long id = getVaccineId(vaccine.display());
            vaccineWrapper.setDbKey(id);
            vaccineWrapper.setDefaultName(vaccine.display());
            vaccineWrappers.add(vaccineWrapper);
            vaccinesGivenInitially.add(vaccineWrapper);
        }
        return vaccineWrappers;
    }

    public ArrayList<VaccineWrapper> getNotGivenVaccineWrappers(HomeVisitVaccineGroup group) {

        ArrayList<VaccineWrapper> vaccineWrappers = new ArrayList<VaccineWrapper>();
        for (VaccineRepo.Vaccine vaccine : group.getNotGivenVaccines()) {
            VaccineWrapper vaccineWrapper = new VaccineWrapper();
            vaccineWrapper.setVaccine(vaccine);
            vaccineWrapper.setName(vaccine.display());
            vaccineWrapper.setDefaultName(vaccine.display());
            vaccineWrappers.add(vaccineWrapper);
        }
        return vaccineWrappers;
    }

    public Long getVaccineId(String vaccineName) {
        List<Vaccine> vaccines = ((ImmunizationViewInteractor) interactor).getVaccines();
        for (Vaccine vaccine : vaccines) {
            if (vaccine.getName().equalsIgnoreCase(vaccineName)) {
                return vaccine.getId();
            }
        }
        return null;
    }

    public ArrayList<VaccineWrapper> getNotGivenVaccines() {
        return notGivenVaccines;
    }

    public void assigntoGivenVaccines(ArrayList<VaccineWrapper> tagsToUpdate) {
        givenGroupWiseVaccines.clear();
        givenGroupWiseVaccines.addAll(tagsToUpdate);
        for (VaccineWrapper name : tagsToUpdate) {
            if (!vaccinesGivenThisVisit.contains(name)) {
                vaccinesGivenThisVisit.add(name);
            }
        }
    }

    public void assignToNotGivenVaccines(ArrayList<VaccineWrapper> tagsToUpdate, String groupName) {
        notGivenGroupWiseVaccines.clear();
        notGivenGroupWiseVaccines.addAll(tagsToUpdate);
        if (!saveGroupList.contains(groupName)) {
            saveGroupList.add(groupName);
        }
        for (VaccineWrapper name : tagsToUpdate) {
            if (!notGivenVaccines.contains(name)) {
                notGivenVaccines.add(name);
            }
        }
    }
    public boolean isFirstEntry(String groupName){
        return !saveGroupList.contains(groupName);
    }

    public ArrayList<VaccineRepo.Vaccine> convertGivenVaccineWrapperListToVaccineRepo() {
        ArrayList<VaccineRepo.Vaccine> vaccineArrayList = new ArrayList<>();
        for (VaccineWrapper vaccineWrapper : givenGroupWiseVaccines) {
            VaccineRepo.Vaccine vaccine = vaccineWrapper.getVaccine();
            vaccineArrayList.add(vaccine);
        }
        return vaccineArrayList;

    }

    public ArrayList<VaccineRepo.Vaccine> convertNotVaccineWrapperListToVaccineRepo() {
        ArrayList<VaccineRepo.Vaccine> vaccineArrayList = new ArrayList<>();
        for (VaccineWrapper vaccineWrapper : notGivenGroupWiseVaccines) {
            VaccineRepo.Vaccine vaccine = vaccineWrapper.getVaccine();
            vaccineArrayList.add(vaccine);
        }
        return vaccineArrayList;

    }
    public LinkedHashMap<DateTime, ArrayList<VaccineRepo.Vaccine>> updateGroupByDate(){
        LinkedHashMap<DateTime, ArrayList<VaccineRepo.Vaccine>> dueWithDate = new LinkedHashMap<>();
        for (VaccineWrapper vaccineWrapper : givenGroupWiseVaccines) {
            VaccineRepo.Vaccine vaccine = vaccineWrapper.getVaccine();
            DateTime dueDate = vaccineWrapper.getUpdatedVaccineDate();
            if(dueWithDate.get(dueDate) == null){
                ArrayList<VaccineRepo.Vaccine> vaccineArrayList = new ArrayList<>();
                vaccineArrayList.add(vaccine);
                dueWithDate.put(dueDate,vaccineArrayList);
            }else{
                dueWithDate.get(dueDate).add(vaccine);
            }
        }
        return dueWithDate;
    }

    public Observable undoVaccine(final CommonPersonObjectClient childClient) {

        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                for (VaccineWrapper tag : vaccinesGivenThisVisit) {
                    if (tag != null && tag.getDbKey() != null) {
                        Long dbKey = tag.getDbKey();
                        vaccineRepository.deleteVaccine(dbKey);

                    }
                }
                String dobString = org.smartregister.util.Utils.getValue(childClient.getColumnmaps(), "dob", false);
                if (!TextUtils.isEmpty(dobString)) {
                    DateTime dateTime = new DateTime(dobString);
                    VaccineSchedule.updateOfflineAlerts(childClient.entityId(), dateTime, "child");
                }
                e.onComplete();
            }
        });
    }

    public Observable undoPreviousGivenVaccine(final CommonPersonObjectClient childClient) {

        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                for (VaccineWrapper tag : vaccinesGivenInitially) {
                    if (tag != null && notGivenVaccines.contains(tag) && tag.getDbKey() != null) {
                        notGivenVaccines.remove(tag);
                        Long dbKey = tag.getDbKey();
                        vaccineRepository.deleteVaccine(dbKey);

                    }
                }
                String dobString = org.smartregister.util.Utils.getValue(childClient.getColumnmaps(), "dob", false);
                if (!TextUtils.isEmpty(dobString)) {
                    DateTime dateTime = new DateTime(dobString);
                    VaccineSchedule.updateOfflineAlerts(childClient.entityId(), dateTime, "child");
                }
                e.onComplete();
            }
        });
    }

    public Observable saveGivenThisVaccine(final CommonPersonObjectClient childClient) {

        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                for (VaccineWrapper tag : vaccinesGivenThisVisit) {
                    //if (tag != null && tag.getDbKey() != null) {
                    saveVaccine(tag, childClient);

                    //}
                }
                String dobString = org.smartregister.util.Utils.getValue(childClient.getColumnmaps(), "dob", false);
                if (!TextUtils.isEmpty(dobString)) {
                    DateTime dateTime = new DateTime(dobString);
                    VaccineSchedule.updateOfflineAlerts(childClient.entityId(), dateTime, "child");
                }

                e.onComplete();
            }
        });
    }

    private void saveVaccine(VaccineWrapper tag, CommonPersonObjectClient childClient) {
        if (tag.getUpdatedVaccineDate() == null) {
            return;
        }
        Vaccine vaccine = new Vaccine();
        if (tag.getDbKey() != null) {
            vaccine = vaccineRepository.find(tag.getDbKey());
        }
        vaccine.setBaseEntityId(childClient.entityId());
        vaccine.setName(tag.getName());
        vaccine.setDate(tag.getUpdatedVaccineDate().toDate());

        String lastChar = vaccine.getName().substring(vaccine.getName().length() - 1);
        if (StringUtils.isNumeric(lastChar)) {
            vaccine.setCalculation(Integer.valueOf(lastChar));
        } else {
            vaccine.setCalculation(-1);
        }

        JsonFormUtils.tagSyncMetadata(Utils.context().allSharedPreferences(), vaccine);
        vaccineRepository.add(vaccine);
        tag.setDbKey(vaccine.getId());
    }
    public boolean isAllSelected(){
        for(HomeVisitVaccineGroup homeVisitVaccineGroup:homeVisitVaccineGroupDetails){
            if(homeVisitVaccineGroup.getViewType()!= HomeVisitVaccineGroup.TYPE_ACTIVE){
                return false;
            }
        }
        return true;
//        org.smartregister.util.Log.logError("SUBMIT_BTN","saveGroupList.size()>>"+saveGroupList.size()+": "+homeVisitVaccineGroupDetails.size());
//
//        return   saveGroupList.size() == homeVisitVaccineGroupDetails.size();
    }
}
