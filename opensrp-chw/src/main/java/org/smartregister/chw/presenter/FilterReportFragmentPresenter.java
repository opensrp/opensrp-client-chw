package org.smartregister.chw.presenter;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.smartregister.chw.contract.FindReportContract;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;

public class FilterReportFragmentPresenter implements FindReportContract.Presenter {

    @Nullable
    private WeakReference<FindReportContract.View> weakReference;
    @Nullable
    private FindReportContract.Model model;
    @Nullable
    private FindReportContract.Interactor interactor;

    @Override
    public void runReport(Map<String, String> parameters) {
        FindReportContract.View view = getView();
        if (view == null) return;

        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }

        view.startResultsView(bundle);
    }

    @Override
    public void initializeViews() {
        FindReportContract.View view = getView();
        if (model != null && interactor != null && view != null) {
            view.setLoadingState(true);
            LinkedHashMap<String, String> locations = model.getAllLocations();
            interactor.processAvailableLocations(locations, this);
        }
    }

    @Override
    public void onReportHierarchyLoaded(Map<String, String> locationData) {
        FindReportContract.View view = getView();
        if (view == null) return;

        view.setLoadingState(false);
        view.onLocationDataLoaded(locationData);
    }

    @Override
    public FindReportContract.Presenter with(FindReportContract.View view) {
        weakReference = new WeakReference<>(view);
        return this;
    }

    @Override
    public FindReportContract.Presenter withModel(FindReportContract.Model model) {
        this.model = model;
        return this;
    }

    @Override
    public FindReportContract.Presenter withInteractor(FindReportContract.Interactor interactor) {
        this.interactor = interactor;
        return this;
    }

    @Nullable
    @Override
    public FindReportContract.View getView() {
        if (weakReference != null)
            return weakReference.get();

        return null;
    }
}
