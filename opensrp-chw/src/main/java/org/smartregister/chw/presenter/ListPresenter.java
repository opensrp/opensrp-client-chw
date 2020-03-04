package org.smartregister.chw.presenter;

import androidx.annotation.Nullable;

import org.smartregister.chw.contract.ListContract;
import org.smartregister.chw.interactor.ListInteractor;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author rkodev
 */
public class ListPresenter<T extends ListContract.Identifiable> implements ListContract.Presenter<T> {

    @Nullable
    private WeakReference<ListContract.View<T>> weakReference;
    private ListContract.Interactor<T> interactor = new ListInteractor<>();
    private ListContract.Model<T> model;

    /**
     * Calling the fetch list method directly from the view may lead to memory leaks,
     * Use this method with caution when on the view
     *
     * @param callable
     */
    @Override
    public void fetchList(Callable<List<T>> callable) {
        ListContract.View<T> currentView = getView();
        if (currentView != null)
            currentView.setLoadingState(true);

        if (interactor != null) {
            interactor.runRequest(callable, this);
        }
    }

    @Override
    public void onItemsFetched(List<T> identifiables) {
        ListContract.View<T> currentView = getView();
        if (currentView == null) return;

        currentView.renderData(identifiables);
        currentView.refreshView();
        currentView.setLoadingState(false);
    }

    @Override
    public ListContract.Presenter<T> with(ListContract.View<T> view) {
        weakReference = new WeakReference<>(view);
        return this;
    }

    @Override
    public ListContract.Presenter<T> using(ListContract.Interactor<T> interactor) {
        this.interactor = interactor;
        return this;
    }

    @Override
    public ListContract.Presenter<T> withModel(ListContract.Model<T> model) {
        this.model = model;
        return this;
    }

    @Nullable
    @Override
    public ListContract.View<T> getView() {
        if (weakReference != null)
            return weakReference.get();

        return null;
    }

    @Override
    public ListContract.Model<T> getModel() {
        return model;
    }
}
