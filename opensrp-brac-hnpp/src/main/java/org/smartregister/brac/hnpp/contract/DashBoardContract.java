package org.smartregister.brac.hnpp.contract;

import org.smartregister.brac.hnpp.model.DashBoardModel;

import java.util.ArrayList;

public interface DashBoardContract {

    public interface InteractorCallBack{

        void updateList(ArrayList<DashBoardModel> dashBoardModels);

    }
    public interface Interactor{

        void fetchDashBoardData(InteractorCallBack interactorCallBack);

    }
    public interface Model{

    }
    public interface Presenter{

         void fetchDashBoardData();

         View getView();

    }
    public interface View{

        void updateAdapter();

    }

}
