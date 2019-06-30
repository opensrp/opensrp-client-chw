package org.smartregister.chw.interactor;

import android.content.Context;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.domain.HomeVisit;
import org.smartregister.chw.util.HomeVisitServiceDataModel;
import org.smartregister.chw.util.ServiceTask;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;
import java.util.List;

public class ChildHomeVisitInteractorFlv extends DefaultChildHomeVisitInteractorFlv {
    private List<HomeVisitServiceDataModel> homeVisitServiceDataModels = new ArrayList<>();

    @Override
    public ArrayList<ServiceTask> getTaskService(CommonPersonObjectClient childClient, boolean isEditMode, Context context) {
        return getCustomTasks(homeVisitServiceDataModels, childClient, isEditMode, context);
    }

    @Override
    public void generateServiceData(HomeVisit homeVisit) {
        if (homeVisit.getHomeVisitId() != null) {
            homeVisitServiceDataModels.clear();
            homeVisitServiceDataModels = ChwApplication.getHomeVisitServiceRepository().getHomeVisitServiceList(homeVisit.getHomeVisitId());
        }
    }
}
