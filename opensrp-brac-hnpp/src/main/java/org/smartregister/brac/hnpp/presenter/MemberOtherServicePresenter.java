package org.smartregister.brac.hnpp.presenter;

import org.smartregister.brac.hnpp.contract.OtherServiceContract;
import org.smartregister.brac.hnpp.fragment.MemberOtherServiceFragment;
import org.smartregister.brac.hnpp.interactor.MemberOtherServiceInteractor;
import org.smartregister.brac.hnpp.utils.OtherServiceData;
import org.smartregister.family.util.AppExecutors;

import java.util.ArrayList;

public class MemberOtherServicePresenter implements OtherServiceContract.Presenter, OtherServiceContract.InteractorCallBack {


    private OtherServiceContract.View view;
    private ArrayList<OtherServiceData> data;
    private OtherServiceContract.Interactor interactor;

    public MemberOtherServicePresenter(OtherServiceContract.View view){
        this.view = view;
        interactor = new MemberOtherServiceInteractor(new AppExecutors());
    }
    @Override
    public void fetchData() {
        interactor.fetchData(getView().getContext(),this);
    }

    @Override
    public ArrayList<OtherServiceData> getData() {
        return data;

    }

    @Override
    public void onUpdateList(ArrayList<OtherServiceData> otherServiceData) {
        this.data = otherServiceData;
        if(getView() != null) getView().updateView();

    }

    @Override
    public MemberOtherServiceFragment getView() {
        return (MemberOtherServiceFragment) view;
    }
}
