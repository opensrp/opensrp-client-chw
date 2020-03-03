package org.smartregister.chw.presenter;

import androidx.annotation.Nullable;

import org.smartregister.chw.contract.ListContract;
import org.smartregister.chw.domain.ReportType;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Callable;

public class ReportsFragmentPresenter implements ListContract.Presenter<ReportType> {

    @Nullable
    private WeakReference<ListContract.View<ReportType>> weakReference;
    private ListContract.Interactor<ReportType> interactor;
    private ListContract.Model<ReportType> model;

    @Override
    public void fetchList(Callable<List<ReportType>> callable) {
        if (interactor != null) {
            interactor.runRequest(callable, this);
        }
    }

    @Override
    public void onItemsFetched(List<ReportType> identifiables) {
        if (getView() != null) {
            getView().renderData(identifiables);
        }
    }

    @Override
    public ListContract.Presenter<ReportType> with(ListContract.View<ReportType> view) {
        weakReference = new WeakReference<>(view);
        return this;
    }

    @Override
    public ListContract.Presenter<ReportType> using(ListContract.Interactor<ReportType> interactor) {
        this.interactor = interactor;
        return this;
    }

    @Override
    public ListContract.Presenter<ReportType> withModel(ListContract.Model<ReportType> model) {
        this.model = model;
        return this;
    }

    @Nullable
    @Override
    public ListContract.View<ReportType> getView() {
        if (weakReference != null)
            return weakReference.get();

        return null;
    }

}
