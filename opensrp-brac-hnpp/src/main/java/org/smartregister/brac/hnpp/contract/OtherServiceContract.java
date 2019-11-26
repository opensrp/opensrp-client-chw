package org.smartregister.brac.hnpp.contract;

import android.content.Context;

import org.smartregister.brac.hnpp.utils.OtherServiceData;

import java.util.ArrayList;

public interface OtherServiceContract {

    interface View{
        void showProgressBar();
        void hideProgressBar();
        void updateView();
        Presenter getPresenter();
        Context getContext();
    }
    interface Model{
        ArrayList<OtherServiceData> getData();
        void loadData();
    }

    interface Presenter{
        void fetchData();
        ArrayList<OtherServiceData> getData();
        View getView();
    }
    interface Interactor{
        void fetchData(Context context, InteractorCallBack callBack);
    }

    interface InteractorCallBack{
        void onUpdateList(ArrayList<OtherServiceData> otherServiceData);
    }
}
