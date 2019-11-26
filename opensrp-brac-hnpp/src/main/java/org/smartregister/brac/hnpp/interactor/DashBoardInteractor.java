package org.smartregister.brac.hnpp.interactor;

import org.smartregister.brac.hnpp.contract.DashBoardContract;
import org.smartregister.brac.hnpp.utils.DashBoardData;
import org.smartregister.brac.hnpp.model.DashBoardModel;
import org.smartregister.family.util.AppExecutors;

import java.util.ArrayList;

public class DashBoardInteractor implements DashBoardContract.Interactor {

    private AppExecutors appExecutors;
    private  DashBoardContract.Model model;

    public DashBoardInteractor(AppExecutors appExecutors, DashBoardContract.Model model){
        this.model = model;
        this.appExecutors = appExecutors;
    }

    @Override
    public void fetchDashBoardData(String toDate, String fromDate , DashBoardContract.InteractorCallBack callBack) {

        Runnable runnable = () -> {
            ArrayList<DashBoardData> dashBoardData = getModel().getDashData(fromDate,toDate);

            appExecutors.mainThread().execute(() -> callBack.updateList(dashBoardData));
        };
        appExecutors.diskIO().execute(runnable);


    }

    public DashBoardModel getModel() {
        return (DashBoardModel)model;
    }
}
