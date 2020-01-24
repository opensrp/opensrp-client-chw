package org.smartregister.chw.contract;


import android.content.Context;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;

import java.util.List;

public interface SickFormMedicalHistoryContract {

    interface View {

        void initializePresenter();

        Presenter getPresenter();

        void displayLoadingState(boolean state);

        void refreshVisits(List<Visit> serviceList);

        Context getContext();

        void onAdapterInteraction(Visit visit);
    }

    interface Presenter {

        void initialize();

        View getView();
    }

    interface Interactor {

        void getUpComingServices(MemberObject memberObject, Context context, InteractorCallBack callBack);

    }

    interface InteractorCallBack {

        void onDataFetched(List<Visit> serviceList);

    }
}
