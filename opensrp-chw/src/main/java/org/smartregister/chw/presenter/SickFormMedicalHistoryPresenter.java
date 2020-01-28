package org.smartregister.chw.presenter;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.contract.SickFormMedicalHistoryContract;

import java.lang.ref.WeakReference;
import java.util.List;

public class SickFormMedicalHistoryPresenter implements SickFormMedicalHistoryContract.Presenter, SickFormMedicalHistoryContract.InteractorCallBack {

    private SickFormMedicalHistoryContract.Interactor interactor;
    private WeakReference<SickFormMedicalHistoryContract.View> view;
    private MemberObject memberObject;

    public SickFormMedicalHistoryPresenter(MemberObject memberObject, SickFormMedicalHistoryContract.Interactor interactor, SickFormMedicalHistoryContract.View view) {
        this.interactor = interactor;
        this.view = new WeakReference<>(view);
        this.memberObject = memberObject;

        initialize();
    }

    @Override
    public void initialize() {
        if (getView() != null) {
            getView().displayLoadingState(true);
            interactor.getUpComingServices(memberObject, getView().getContext(), this);
        }
    }

    @Override
    public SickFormMedicalHistoryContract.View getView() {
        if (view != null && view.get() != null) {
            return view.get();
        }
        return null;
    }

    @Override
    public void onDataFetched(List<Visit> serviceList) {
        if (getView() != null) {
            getView().displayLoadingState(false);
            getView().refreshVisits(serviceList);
        }
    }
}
