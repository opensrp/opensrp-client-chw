package org.smartgresiter.wcaro.contract;

import android.app.Activity;

import org.smartgresiter.wcaro.interactor.HomeVisitImmunizationInteractor;
import org.smartgresiter.wcaro.util.HomeVisitVaccineGroupDetails;
import org.smartgresiter.wcaro.util.ImmunizationState;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineWrapper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HomeVisitImmunizationContract {
    interface View extends InteractorCallBack {

        void setActivity(Activity activity);

        void setChildClient(CommonPersonObjectClient childClient);

        void refreshPresenter(List<Alert> alerts, List<Vaccine> vaccines, List<Map<String, Object>> sch);

        void undoVaccines();

        Presenter initializePresenter();

        Presenter getPresenter();

        void updateImmunizationState();
    }

    interface Presenter {

        void createAllVaccineGroups(List<Alert> alerts, List<Vaccine> vaccines, List<Map<String, Object>> sch);

        void getVaccinesNotGivenLastVisit();

        void calculateCurrentActiveGroup();

        HomeVisitImmunizationContract.View getView();

        void onDestroy(boolean isChangingConfiguration);

        boolean isPartiallyComplete();

        boolean isComplete();

        Interactor getHomeVisitImmunizationInteractor();

        void setHomeVisitImmunizationInteractor(HomeVisitImmunizationInteractor homeVisitImmunizationInteractor);

        void setView(WeakReference<View> view);

        ArrayList<VaccineRepo.Vaccine> getVaccinesDueFromLastVisit();

        void setVaccinesDueFromLastVisit(ArrayList<VaccineRepo.Vaccine> vaccinesDueFromLastVisit);

        ArrayList<HomeVisitVaccineGroupDetails> getAllgroups();

        void setAllgroups(ArrayList<HomeVisitVaccineGroupDetails> allgroups);

        ArrayList<VaccineWrapper> getNotGivenVaccines();

        void setNotGivenVaccines(ArrayList<VaccineWrapper> notGivenVaccines);

        HomeVisitVaccineGroupDetails getCurrentActiveGroup();

        void setCurrentActiveGroup(HomeVisitVaccineGroupDetails currentActiveGroup);

        boolean groupIsDue();

        ArrayList<VaccineWrapper> createVaccineWrappers(HomeVisitVaccineGroupDetails duevaccines);

        CommonPersonObjectClient getchildClient();

        void setChildClient(CommonPersonObjectClient childClient);

        void updateNotGivenVaccine(VaccineWrapper name);

        ArrayList<VaccineWrapper> getVaccinesGivenThisVisit();

        void assigntoGivenVaccines(ArrayList<VaccineWrapper> tagsToUpdate);

        void undoGivenVaccines();

        void updateImmunizationState(InteractorCallBack callBack);

        ArrayList<VaccineRepo.Vaccine> getVaccinesDueFromLastVisitStillDueState();

        boolean isSingleVaccineGroupPartialComplete();

        boolean isSingleVaccineGroupComplete();

        void setGroupVaccineText(List<Map<String, Object>> sch);

        void setSingleVaccineText(ArrayList<VaccineRepo.Vaccine> vaccinesDueFromLastVisit, List<Map<String, Object>> sch);

        String getGroupImmunizationSecondaryText();

        void setGroupImmunizationSecondaryText(String groupImmunizationSecondaryText);

        String getSingleImmunizationSecondaryText();

        void setSingleImmunizationSecondaryText(String singleImmunizationSecondaryText);
    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        HomeVisitVaccineGroupDetails getCurrentActiveHomeVisitVaccineGroupDetail(ArrayList<HomeVisitVaccineGroupDetails> allGroups);

        HomeVisitVaccineGroupDetails getLastActiveHomeVisitVaccineGroupDetail(ArrayList<HomeVisitVaccineGroupDetails> allGroups);

        boolean isPartiallyComplete(HomeVisitVaccineGroupDetails toprocess);

        boolean isComplete(HomeVisitVaccineGroupDetails toprocess);

        boolean groupIsDue(HomeVisitVaccineGroupDetails toprocess);

        boolean hasVaccinesNotGivenSinceLastVisit(ArrayList<HomeVisitVaccineGroupDetails> allGroup);

        int getIndexOfCurrentGroup(ArrayList<HomeVisitVaccineGroupDetails> allGroup);

        ArrayList<VaccineRepo.Vaccine> getNotGivenVaccinesLastVisitList(ArrayList<HomeVisitVaccineGroupDetails> allGroup);

        ArrayList<VaccineRepo.Vaccine> getNotGivenVaccinesNotInNotGivenThisVisit(HomeVisitVaccineGroupDetails allGroup);

        ArrayList<HomeVisitVaccineGroupDetails> determineAllHomeVisitVaccineGroupDetails(List<Alert> alerts, List<Vaccine> vaccines, ArrayList<VaccineWrapper> notGivenVaccines, List<Map<String, Object>> sch);

        ArrayList<HomeVisitVaccineGroupDetails> assignDate(ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetailsArrayList, List<Map<String, Object>> sch);

        ArrayList<HomeVisitVaccineGroupDetails> assignGivenVaccine(ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetailsArrayList, Map<String, Date> receivedvaccines);

        ArrayList<HomeVisitVaccineGroupDetails> assignDueVaccine(List<VaccineRepo.Vaccine> vList, ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetailsArrayList, List<Alert> alerts);

        boolean hasAlert(VaccineRepo.Vaccine vaccine, List<Alert> alerts);

        ImmunizationState alertState(Alert toProcess);

        boolean isReceived(String s, Map<String, Date> receivedvaccines);

        ImmunizationState assignAlert(VaccineRepo.Vaccine vaccine, List<Alert> alerts);

        void updateImmunizationState(CommonPersonObjectClient childClient, ArrayList<VaccineWrapper> notGivenVaccines, InteractorCallBack callBack);
    }

    interface InteractorCallBack {
        void immunizationState(List<Alert> alerts, List<Vaccine> vaccines, List<Map<String, Object>> sch);
    }
}
