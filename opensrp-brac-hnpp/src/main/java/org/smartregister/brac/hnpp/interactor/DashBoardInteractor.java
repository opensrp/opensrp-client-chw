package org.smartregister.brac.hnpp.interactor;

import org.smartregister.Context;
import org.smartregister.brac.hnpp.contract.DashBoardContract;
import org.smartregister.brac.hnpp.model.DashBoardModel;
import org.smartregister.family.util.AppExecutors;

import java.util.ArrayList;

public class DashBoardInteractor implements DashBoardContract.Interactor {

    private AppExecutors appExecutors;
    private Context context;

    public DashBoardInteractor(AppExecutors appExecutors, Context context){

        this.appExecutors = appExecutors;
        this.context = context;
    }

    @Override
    public void fetchDashBoardData(DashBoardContract.InteractorCallBack callBack) {

        Runnable runnable = () -> {
            ArrayList<DashBoardModel> dashBoardModels = null;



            appExecutors.mainThread().execute(() -> callBack.updateList(dashBoardModels));
        };
        appExecutors.diskIO().execute(runnable);


    }
}
