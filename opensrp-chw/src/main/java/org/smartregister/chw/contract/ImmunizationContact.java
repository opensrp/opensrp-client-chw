package org.smartregister.chw.contract;

import org.smartregister.chw.util.HomeVisitVaccineGroup;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;

public interface ImmunizationContact {

    interface View {

        Presenter initializePresenter();

        void allDataLoaded();

        void updateAdapter(int position);
    }
    interface Presenter{

        void fetchImmunizationData(CommonPersonObjectClient commonPersonObjectClient);

        void fetchImmunizationEditData(CommonPersonObjectClient commonPersonObjectClient);

        View getView();
    }

    interface Interactor{

        void fetchImmunizationData(CommonPersonObjectClient commonPersonObjectClient, InteractorCallBack callBack);

        void fetchImmunizationEditData(CommonPersonObjectClient commonPersonObjectClient,InteractorCallBack callBack);
    }
    interface InteractorCallBack {

        void allDataLoaded();

        void updateData(ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetails);

        void updateEditData(ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetails);
    }
}
