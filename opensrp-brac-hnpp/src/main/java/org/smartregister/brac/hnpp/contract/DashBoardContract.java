package org.smartregister.brac.hnpp.contract;

public interface DashBoardContract {

    public interface InteractorCallBack{

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
