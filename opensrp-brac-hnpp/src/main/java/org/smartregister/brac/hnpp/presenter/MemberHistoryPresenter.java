package org.smartregister.brac.hnpp.presenter;

import org.smartregister.brac.hnpp.contract.MemberHistoryContract;
import org.smartregister.brac.hnpp.contract.OtherServiceContract;
import org.smartregister.brac.hnpp.fragment.MemberHistoryFragment;
import org.smartregister.brac.hnpp.fragment.MemberOtherServiceFragment;
import org.smartregister.brac.hnpp.interactor.MemberHistoryInteractor;
import org.smartregister.brac.hnpp.interactor.MemberOtherServiceInteractor;
import org.smartregister.brac.hnpp.utils.MemberHistoryData;
import org.smartregister.brac.hnpp.utils.OtherServiceData;
import org.smartregister.family.util.AppExecutors;

import java.util.ArrayList;

public class MemberHistoryPresenter implements MemberHistoryContract.Presenter, MemberHistoryContract.InteractorCallBack {


    private MemberHistoryContract.View view;
    private ArrayList<MemberHistoryData> data;
    private MemberHistoryContract.Interactor interactor;

    public MemberHistoryPresenter(MemberHistoryContract.View view){
        this.view = view;
        interactor = new MemberHistoryInteractor(new AppExecutors());
    }

    @Override
    public void fetchData(String baseEntityId) {
        interactor.fetchData(getView().getContext(),baseEntityId,this);
    }

    @Override
    public ArrayList<MemberHistoryData> getMemberHistory() {
        return data;
    }

    @Override
    public void onUpdateList(ArrayList<MemberHistoryData> list) {
        this.data = list;
        if(getView() != null) getView().updateAdapter();
    }

    @Override
    public MemberHistoryFragment getView() {
        return (MemberHistoryFragment) view;
    }
}
