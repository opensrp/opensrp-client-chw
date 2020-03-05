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
    public <X extends ListContract.Presenter<T>, V extends ListContract.View<T>> X with(V view) {
        weakReference = new WeakReference<>(view);
        return (X) this;
    }

    @Override
    public <X extends ListContract.Presenter<T>, I extends ListContract.Interactor<T>> X using(I interactor) {
        this.interactor = interactor;
        return (X) this;
    }

    @Override
    public <X extends ListContract.Presenter<T>, M extends ListContract.Model<T>> X withModel(M model) {
        this.model = model;
        return (X) this;
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
