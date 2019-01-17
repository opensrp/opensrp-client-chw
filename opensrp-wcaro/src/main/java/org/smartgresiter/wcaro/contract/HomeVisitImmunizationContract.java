package org.smartgresiter.wcaro.contract;

import org.smartgresiter.wcaro.util.HomeVisitVaccineGroupDetails;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.immunization.domain.Vaccine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface HomeVisitImmunizationContract {
    interface View{
        Presenter initializePresenter();

    }
    interface Presenter{
        HomeVisitImmunizationContract.View getView();
        void onDestroy(boolean isChangingConfiguration);

    }
    interface Interactor{
        void onDestroy(boolean isChangingConfiguration);

        HomeVisitVaccineGroupDetails getCurrentActiveHomeVisitVaccineGroupDetail(ArrayList<HomeVisitVaccineGroupDetails> allGroups);

        ArrayList<HomeVisitVaccineGroupDetails> determineAllHomeVisitVaccineGroupDetails(List<Alert> alerts, List<Vaccine> vaccines);
    }
    interface InteractorCallBack{
        void updateRecordVisitData(Map<String, ServiceWrapper> stringServiceWrapperMap);
    }
}
