package org.smartregister.chw.contract;


import android.content.Context;

import org.smartregister.chw.anc.domain.MemberObject;

import java.util.List;

public interface SickFormMedicalHistoryContract {

    interface View {

        void initializePresenter();

        Presenter getPresenter();

        void displayLoadingState(boolean state);

        void refreshVisits(List<String> serviceList);

        Context getContext();
    }

    interface Presenter {

        void initialize();

        View getView();
    }

    interface Interactor {

        void getUpComingServices(MemberObject memberObject, Context context, InteractorCallBack callBack);

    }

    interface InteractorCallBack {

        void onDataFetched(List<String> serviceList);

    }
}
