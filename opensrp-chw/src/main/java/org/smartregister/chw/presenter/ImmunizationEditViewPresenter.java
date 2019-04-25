//package org.smartregister.chw.presenter;
//
//import android.text.TextUtils;
//
//import org.apache.commons.lang3.StringUtils;
//import org.joda.time.DateTime;
//import org.smartregister.chw.contract.ImmunizationEditContract;
//import org.smartregister.chw.interactor.ImmunizationEditViewInteractor;
//import org.smartregister.chw.util.HomeVisitVaccineGroup;
//import org.smartregister.chw.util.JsonFormUtils;
//import org.smartregister.commonregistry.CommonPersonObjectClient;
//import org.smartregister.family.util.Utils;
//import org.smartregister.immunization.ImmunizationLibrary;
//import org.smartregister.immunization.db.VaccineRepo;
//import org.smartregister.immunization.domain.Vaccine;
//import org.smartregister.immunization.domain.VaccineSchedule;
//import org.smartregister.immunization.domain.VaccineWrapper;
//import org.smartregister.immunization.repository.VaccineRepository;
//
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.List;
//
//import io.reactivex.Observable;
//import io.reactivex.ObservableEmitter;
//import io.reactivex.ObservableOnSubscribe;
//
//public class ImmunizationEditViewPresenter implements ImmunizationEditContract.Presenter,ImmunizationEditContract.InteractorCallBack {
//
//    private WeakReference<ImmunizationEditContract.View> view;
//    private ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetails = new ArrayList<>();
//    private ImmunizationEditContract.Interactor interactor;
//    private ArrayList<VaccineWrapper> vaccinesGivenInitially = new ArrayList<VaccineWrapper>();
//    private ArrayList<VaccineWrapper> vaccinesGivenThisVisit = new ArrayList<VaccineWrapper>();
//    private ArrayList<VaccineWrapper> notGivenVaccines = new ArrayList<VaccineWrapper>();
//    private final VaccineRepository vaccineRepository;
//    public ImmunizationEditViewPresenter(ImmunizationEditContract.View view){
//        this.view = new WeakReference<>(view);
//        interactor = new ImmunizationEditViewInteractor();
//        vaccineRepository = ImmunizationLibrary.getInstance().vaccineRepository();
//    }
//
//    public ArrayList<HomeVisitVaccineGroup> getHomeVisitVaccineGroupDetails() {
//        return homeVisitVaccineGroupDetails;
//    }
//
//    @Override
//    public void fetchImmunizationEditData(CommonPersonObjectClient commonPersonObjectClient) {
//        interactor.fetchImmunizationEditData(commonPersonObjectClient,this);
//
//    }
//
//    @Override
//    public void allDataLoaded() {
//        getView().allDataLoaded();
//    }
//
//    @Override
//    public void updateEditData(ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetails) {
//        allDataLoaded();
//        this.homeVisitVaccineGroupDetails = homeVisitVaccineGroupDetails;
//        for (HomeVisitVaccineGroup homeVisitVaccineGroup :this.homeVisitVaccineGroupDetails){
//            homeVisitVaccineGroup.setViewType(HomeVisitVaccineGroup.TYPE_ACTIVE);
//        }
//        getView().updateAdapter();
//
//    }
//    public ArrayList<VaccineWrapper> getDueVaccineWrappers(HomeVisitVaccineGroup duevaccines) {
//
//        ArrayList<VaccineWrapper> vaccineWrappers = new ArrayList<VaccineWrapper>();
//        for (VaccineRepo.Vaccine vaccine : duevaccines.getDueVaccines()) {
//            VaccineWrapper vaccineWrapper = new VaccineWrapper();
//            vaccineWrapper.setVaccine(vaccine);
//            vaccineWrapper.setName(vaccine.display());
//            Long id = getVaccineId(vaccine.display());
//            vaccineWrapper.setDbKey(id);
//            vaccineWrapper.setDefaultName(vaccine.display());
//            vaccineWrappers.add(vaccineWrapper);
//            vaccinesGivenInitially.add(vaccineWrapper);
//        }
//        return vaccineWrappers;
//    }
//    public ArrayList<VaccineWrapper> getNotGivenVaccineWrappers(HomeVisitVaccineGroup group) {
//
//        ArrayList<VaccineWrapper> vaccineWrappers = new ArrayList<VaccineWrapper>();
//        for (VaccineRepo.Vaccine vaccine : group.getNotGivenVaccines()) {
//            VaccineWrapper vaccineWrapper = new VaccineWrapper();
//            vaccineWrapper.setVaccine(vaccine);
//            vaccineWrapper.setName(vaccine.display());
//            vaccineWrapper.setDefaultName(vaccine.display());
//            vaccineWrappers.add(vaccineWrapper);
//        }
//        return vaccineWrappers;
//    }
//    public Long getVaccineId(String vaccineName){
//        List<Vaccine> vaccines = ((ImmunizationEditViewInteractor)interactor).getVaccines();
//        for(Vaccine vaccine:vaccines){
//            if(vaccine.getName().equalsIgnoreCase(vaccineName)){
//                return vaccine.getId();
//            }
//        }
//        return null;
//    }
//    public void updateNotGivenVaccine(VaccineWrapper name) {
//        if (!notGivenVaccines.contains(name)) {
//            notGivenVaccines.add(name);
//        }
//    }
//    public ArrayList<VaccineWrapper> getNotGivenVaccines() {
//        return notGivenVaccines;
//    }
//
//    public void assigntoGivenVaccines(ArrayList<VaccineWrapper> tagsToUpdate) {
//        vaccinesGivenThisVisit.clear();
//        vaccinesGivenThisVisit.addAll(tagsToUpdate);
//    }
//    public ArrayList<VaccineRepo.Vaccine> convertGivenVaccineWrapperListToVaccineRepo(){
//        ArrayList<VaccineRepo.Vaccine> vaccineArrayList = new ArrayList<>();
//        for (VaccineWrapper vaccineWrapper : vaccinesGivenThisVisit){
//            VaccineRepo.Vaccine vaccine = vaccineWrapper.getVaccine();
//            vaccineArrayList.add(vaccine);
//        }
//        return vaccineArrayList;
//
//    }
//    public ArrayList<VaccineRepo.Vaccine> convertNotVaccineWrapperListToVaccineRepo(){
//        ArrayList<VaccineRepo.Vaccine> vaccineArrayList = new ArrayList<>();
//        for (VaccineWrapper vaccineWrapper : notGivenVaccines){
//            VaccineRepo.Vaccine vaccine = vaccineWrapper.getVaccine();
//            vaccineArrayList.add(vaccine);
//        }
//        return vaccineArrayList;
//
//    }
//    public Observable undoVaccine(final CommonPersonObjectClient childClient) {
//
//        return Observable.create(new ObservableOnSubscribe() {
//            @Override
//            public void subscribe(ObservableEmitter e) throws Exception {
//                for (VaccineWrapper tag : vaccinesGivenThisVisit) {
//                    if (tag != null && tag.getDbKey() != null) {
//                        Long dbKey = tag.getDbKey();
//                        vaccineRepository.deleteVaccine(dbKey);
//
//                    }
//                }
//                String dobString = org.smartregister.util.Utils.getValue(childClient.getColumnmaps(), "dob", false);
//                if (!TextUtils.isEmpty(dobString)) {
//                    DateTime dateTime = new DateTime(dobString);
//                    VaccineSchedule.updateOfflineAlerts(childClient.entityId(), dateTime, "child");
//                }
//                e.onComplete();
//            }
//        });
//    }
//    public Observable undoPreviousGivenVaccine(final CommonPersonObjectClient childClient) {
//
//        return Observable.create(new ObservableOnSubscribe() {
//            @Override
//            public void subscribe(ObservableEmitter e) throws Exception {
//                for (VaccineWrapper tag : vaccinesGivenInitially) {
//                    if (tag != null && notGivenVaccines.contains(tag) && tag.getDbKey() != null) {
//                        notGivenVaccines.remove(tag);
//                        Long dbKey = tag.getDbKey();
//                        vaccineRepository.deleteVaccine(dbKey);
//
//                    }
//                }
//                String dobString = org.smartregister.util.Utils.getValue(childClient.getColumnmaps(), "dob", false);
//                if (!TextUtils.isEmpty(dobString)) {
//                    DateTime dateTime = new DateTime(dobString);
//                    VaccineSchedule.updateOfflineAlerts(childClient.entityId(), dateTime, "child");
//                }
//                e.onComplete();
//            }
//        });
//    }
//    public Observable saveGivenThisVaccine(final CommonPersonObjectClient childClient) {
//
//        return Observable.create(new ObservableOnSubscribe() {
//            @Override
//            public void subscribe(ObservableEmitter e) throws Exception {
//                for (VaccineWrapper tag : vaccinesGivenThisVisit) {
//                    //if (tag != null && tag.getDbKey() != null) {
//                        saveVaccine(tag,childClient);
//
//                    //}
//                }
//                String dobString = org.smartregister.util.Utils.getValue(childClient.getColumnmaps(), "dob", false);
//                if (!TextUtils.isEmpty(dobString)) {
//                    DateTime dateTime = new DateTime(dobString);
//                    VaccineSchedule.updateOfflineAlerts(childClient.entityId(), dateTime, "child");
//                }
//
//                e.onComplete();
//            }
//        });
//    }
//    private void saveVaccine(VaccineWrapper tag,CommonPersonObjectClient childClient) {
//        if (tag.getUpdatedVaccineDate() == null) {
//            return;
//        }
//        Vaccine vaccine = new Vaccine();
//        if (tag.getDbKey() != null) {
//            vaccine = vaccineRepository.find(tag.getDbKey());
//        }
//        vaccine.setBaseEntityId(childClient.entityId());
//        vaccine.setName(tag.getName());
//        vaccine.setDate(tag.getUpdatedVaccineDate().toDate());
//
//        String lastChar = vaccine.getName().substring(vaccine.getName().length() - 1);
//        if (StringUtils.isNumeric(lastChar)) {
//            vaccine.setCalculation(Integer.valueOf(lastChar));
//        } else {
//            vaccine.setCalculation(-1);
//        }
//
//        JsonFormUtils.tagSyncMetadata(Utils.context().allSharedPreferences(), vaccine);
//        vaccineRepository.add(vaccine);
//        tag.setDbKey(vaccine.getId());
//    }
//    @Override
//    public ImmunizationEditContract.View getView() {
//        if (view != null) {
//            return view.get();
//        } else {
//            return null;
//        }
//    }
//}
