package org.smartgresiter.wcaro.presenter;

import org.smartgresiter.wcaro.contract.HomeVisitImmunizationContract;
import org.smartgresiter.wcaro.interactor.HomeVisitImmunizationInteractor;
import org.smartgresiter.wcaro.task.UndoVaccineTask;
import org.smartgresiter.wcaro.util.HomeVisitVaccineGroupDetails;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineWrapper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeVisitImmunizationPresenter implements HomeVisitImmunizationContract.Presenter {


    HomeVisitImmunizationContract.Interactor homeVisitImmunizationInteractor;
    private WeakReference<HomeVisitImmunizationContract.View> view;
    ArrayList<VaccineRepo.Vaccine> vaccinesDueFromLastVisit = new ArrayList<VaccineRepo.Vaccine>();
    private ArrayList<HomeVisitVaccineGroupDetails> allgroups = new ArrayList<HomeVisitVaccineGroupDetails>();
    private ArrayList<VaccineWrapper> notGivenVaccines = new ArrayList<VaccineWrapper>();
    private HomeVisitVaccineGroupDetails currentActiveGroup;
    private CommonPersonObjectClient childClient;
    private ArrayList<VaccineWrapper> vaccinesGivenThisVisit = new ArrayList<VaccineWrapper>();


    public HomeVisitImmunizationPresenter(HomeVisitImmunizationContract.View view){
        this.view = new WeakReference<>(view);
        homeVisitImmunizationInteractor = new HomeVisitImmunizationInteractor();
    }

    @Override
    public void createAllVaccineGroups(List<Alert> alerts, List<Vaccine> vaccines, List<Map<String, Object>> sch){
        allgroups = homeVisitImmunizationInteractor.determineAllHomeVisitVaccineGroupDetails(alerts,vaccines,notGivenVaccines,sch);
    }

    @Override
    public void getVaccinesNotGivenLastVisit(){
        if(homeVisitImmunizationInteractor.hasVaccinesNotGivenSinceLastVisit(allgroups)){
            vaccinesDueFromLastVisit =  homeVisitImmunizationInteractor.getNotGivenVaccinesLastVisitList(allgroups);
        }
    }

    @Override
    public void calculateCurrentActiveGroup(){
        currentActiveGroup = homeVisitImmunizationInteractor.getCurrentActiveHomeVisitVaccineGroupDetail(allgroups);
        if(currentActiveGroup == null){
            currentActiveGroup = homeVisitImmunizationInteractor.getLastActiveHomeVisitVaccineGroupDetail(allgroups);
        }
    }

    @Override
    public HomeVisitImmunizationContract.View getView() {
        if (view != null) {
            return view.get();
        } else {
            return null;
        }
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

    }

    @Override
    public boolean isPartiallyComplete() {
        return getHomeVisitImmunizationInteractor().isPartiallyComplete(currentActiveGroup);
    }

    @Override
    public boolean isComplete() {
        return getHomeVisitImmunizationInteractor().isComplete(currentActiveGroup);
    }

    @Override
    public HomeVisitImmunizationContract.Interactor getHomeVisitImmunizationInteractor() {
        return homeVisitImmunizationInteractor;
    }

    @Override
    public void setHomeVisitImmunizationInteractor(HomeVisitImmunizationInteractor homeVisitImmunizationInteractor) {
        this.homeVisitImmunizationInteractor = homeVisitImmunizationInteractor;
    }

    @Override
    public void setView(WeakReference<HomeVisitImmunizationContract.View> view) {
        this.view = view;
    }

    @Override
    public ArrayList<VaccineRepo.Vaccine> getVaccinesDueFromLastVisit() {
        return vaccinesDueFromLastVisit;
    }

    @Override
    public void setVaccinesDueFromLastVisit(ArrayList<VaccineRepo.Vaccine> vaccinesDueFromLastVisit) {
        this.vaccinesDueFromLastVisit = vaccinesDueFromLastVisit;
    }

    @Override
    public ArrayList<HomeVisitVaccineGroupDetails> getAllgroups() {
        return allgroups;
    }

    @Override
    public void setAllgroups(ArrayList<HomeVisitVaccineGroupDetails> allgroups) {
        this.allgroups = allgroups;
    }

    @Override
    public ArrayList<VaccineWrapper> getNotGivenVaccines() {
        return notGivenVaccines;
    }

    @Override
    public void setNotGivenVaccines(ArrayList<VaccineWrapper> notGivenVaccines) {
        this.notGivenVaccines = notGivenVaccines;
    }

    @Override
    public HomeVisitVaccineGroupDetails getCurrentActiveGroup() {
        return currentActiveGroup;
    }

    @Override
    public void setCurrentActiveGroup(HomeVisitVaccineGroupDetails currentActiveGroup) {
        this.currentActiveGroup = currentActiveGroup;
    }

    @Override
    public boolean groupIsDue() {
        return homeVisitImmunizationInteractor.groupIsDue(currentActiveGroup);
    }

    @Override
    public ArrayList<VaccineWrapper> createVaccineWrappers(HomeVisitVaccineGroupDetails duevaccines) {

        ArrayList<VaccineWrapper> vaccineWrappers = new ArrayList<VaccineWrapper>();
        for (VaccineRepo.Vaccine vaccine : duevaccines.getDueVaccines()) {
            VaccineWrapper vaccineWrapper = new VaccineWrapper();
            vaccineWrapper.setVaccine(vaccine);
            vaccineWrapper.setName(vaccine.display());
            vaccineWrapper.setDefaultName(vaccine.display());
            vaccineWrappers.add(vaccineWrapper);
        }
        return vaccineWrappers;
    }

    @Override
    public CommonPersonObjectClient getchildClient() {
        return childClient;
    }

    @Override
    public void setChildClient(CommonPersonObjectClient childClient) {
        this.childClient = childClient;
    }

    @Override
    public void updateNotGivenVaccine(VaccineWrapper name) {
        if (!notGivenVaccines.contains(name)) {
            notGivenVaccines.add(name);
        }
    }

    @Override
    public ArrayList<VaccineWrapper> getVaccinesGivenThisVisit() {
        return vaccinesGivenThisVisit;
    }

    @Override
    public void assigntoGivenVaccines(ArrayList<VaccineWrapper> tagsToUpdate) {
        vaccinesGivenThisVisit.addAll(tagsToUpdate);
    }

    @Override
    public void undoGivenVaccines() {
        org.smartregister.util.Utils.startAsyncTask(new UndoVaccineTask(vaccinesGivenThisVisit, childClient), null);
    }

    @Override
    public void updateImmunizationState(HomeVisitImmunizationContract.InteractorCallBack callBack) {
        homeVisitImmunizationInteractor.updateImmunizationState(childClient,notGivenVaccines,callBack);
    }


}
