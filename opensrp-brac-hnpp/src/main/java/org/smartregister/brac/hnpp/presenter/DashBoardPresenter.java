package org.smartregister.brac.hnpp.presenter;

import org.smartregister.brac.hnpp.contract.DashBoardContract;
import org.smartregister.brac.hnpp.fragment.HnppDashBoardFragment;
import org.smartregister.brac.hnpp.interactor.DashBoardInteractor;
import org.smartregister.brac.hnpp.model.DashBoardData;
import org.smartregister.brac.hnpp.model.DashBoardModel;
import org.smartregister.family.util.AppExecutors;

import java.util.ArrayList;

public class DashBoardPresenter implements DashBoardContract.Presenter,DashBoardContract.InteractorCallBack {
    private DashBoardContract.View view;
    private DashBoardContract.Interactor interactor;
    private DashBoardModel model;


    public DashBoardPresenter(DashBoardContract.View view){
        this.view = view;
        this.model = new DashBoardModel();
        interactor = new DashBoardInteractor(new AppExecutors(), model);
    }


    @Override
    public void fetchDashBoardData(String fromDate, String toDate) {
        getView().showProgressBar();
        interactor.fetchDashBoardData(fromDate,toDate,this);

    }

    public ArrayList<DashBoardData> getDashBoardDataArrayList() {
        return model.getDashBoardDataArrayList();
    }

    @Override
    public HnppDashBoardFragment getView() {
        return (HnppDashBoardFragment) view;
    }

    @Override
    public void updateList(ArrayList<DashBoardData> dashBoardData) {
        getView().hideProgressBar();
        getView().updateAdapter();
    }
}
