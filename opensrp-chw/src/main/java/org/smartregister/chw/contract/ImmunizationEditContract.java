package org.smartregister.chw.contract;

import org.smartregister.chw.util.HomeVisitVaccineGroup;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;

public interface ImmunizationEditContract {

    interface View {

        Presenter initializePresenter();
        void updateAdapter();
    }
    interface Presenter{

        void fetchImmunizationEditData(CommonPersonObjectClient commonPersonObjectClient);
        ImmunizationEditContract.View getView();
    }
    interface Interactor{

        void fetchImmunizationEditData(CommonPersonObjectClient commonPersonObjectClient,InteractorCallBack callBack);
    }
    interface InteractorCallBack {

        void updateEditData(ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetails);
    }

}
