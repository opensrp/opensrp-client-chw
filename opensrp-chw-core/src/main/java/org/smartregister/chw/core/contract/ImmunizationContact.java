package org.smartregister.chw.core.contract;

import android.content.Context;

import org.smartregister.chw.core.utils.HomeVisitVaccineGroup;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public interface ImmunizationContact {

    interface View {

        Presenter initializePresenter();

        void allDataLoaded();

        void updateAdapter(int position, Context context);

        Context getMyContext();
    }

    interface Presenter {

        void fetchImmunizationData(CommonPersonObjectClient commonPersonObjectClient, String groupName);

        View getView();
    }

    interface Interactor {

        void fetchImmunizationData(CommonPersonObjectClient commonPersonObjectClient, InteractorCallBack callBack);

    }

    interface InteractorCallBack {

        void updateData(ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetails, Map<String, Date> receivedVaccine);

    }
}
