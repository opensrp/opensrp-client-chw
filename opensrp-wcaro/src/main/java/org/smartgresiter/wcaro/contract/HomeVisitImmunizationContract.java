package org.smartgresiter.wcaro.contract;

import org.smartgresiter.wcaro.interactor.HomeVisitImmunizationInteractor;
import org.smartgresiter.wcaro.util.HomeVisitVaccineGroupDetails;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineWrapper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface HomeVisitImmunizationContract {
    interface View{
        Presenter initializePresenter();

    }
    interface Presenter{
        void createAllVaccineGroups(List<Alert> alerts, List<Vaccine> vaccines);

        void getVaccinesNotGivenLastVisit();

        void calculateCurrentActiveGroup();

        HomeVisitImmunizationContract.View getView();
        void onDestroy(boolean isChangingConfiguration);

        boolean isPartiallyComplete();

        boolean isComplete();

        HomeVisitImmunizationInteractor getHomeVisitImmunizationInteractor();

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
    }
    interface Interactor{
        void onDestroy(boolean isChangingConfiguration);

        HomeVisitVaccineGroupDetails getCurrentActiveHomeVisitVaccineGroupDetail(ArrayList<HomeVisitVaccineGroupDetails> allGroups);

        ArrayList<HomeVisitVaccineGroupDetails> determineAllHomeVisitVaccineGroupDetails(List<Alert> alerts, List<Vaccine> vaccines, ArrayList<VaccineWrapper> notGivenVaccines);
    }
    interface InteractorCallBack{
    }
}
