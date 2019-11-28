package org.smartregister.brac.hnpp.contract;

import android.content.Context;

import org.smartregister.brac.hnpp.utils.MemberHistoryData;

import java.util.ArrayList;

public interface MemberHistoryContract {

    interface View{
        void showProgressBar();
        void hideProgressBar();
        void updateAdapter();
        void initializePresenter();
        Presenter getPresenter();
    }
    interface Presenter{
        void fetchData(String baseEntityId);
        ArrayList<MemberHistoryData> getMemberHistory();
        View getView();
    }
    interface InteractorCallBack{
        void onUpdateList(ArrayList<MemberHistoryData> list);
    }
    interface Interactor{
        void fetchData(Context context, String baseEntityId, MemberHistoryContract.InteractorCallBack callBack);
    }
}
