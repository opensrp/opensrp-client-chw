package org.smartregister.chw.contract;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.chw.adapter.ListableAdapter;
import org.smartregister.chw.viewholder.ListableViewHolder;

import java.util.List;
import java.util.concurrent.Callable;

public interface ListContract {

    interface Model<T extends ListContract.Identifiable> {

    }

    interface View<T extends ListContract.Identifiable> {

        void bindLayout();

        void renderData(List<T> identifiables);

        void refreshView();

        void setLoadingState(boolean loadingState);

        void onListItemClicked(T t, @IdRes int layoutID);

        @NonNull
        ListableAdapter<T, ListableViewHolder<T>> adapter();

        @NonNull
        Presenter<T> loadPresenter();
    }

    interface Presenter<T extends ListContract.Identifiable> {

        void fetchList(Callable<List<T>> callable);

        void onItemsFetched(List<T> identifiables);

        /**
         * binds the view
         *
         * @param view
         */
        Presenter<T> with(View<T> view);

        /**
         * binds interactor
         *
         * @param interactor
         * @return
         */
        Presenter<T> using(Interactor<T> interactor);

        /**
         * binds a views model
         *
         * @param model
         * @return
         */
        Presenter<T> withModel(Model<T> model);

        @Nullable
        View<T> getView();

        Model<T> getModel();
    }

    interface Interactor<T extends ListContract.Identifiable> {

        /**
         * @param callable
         * @param presenter
         */
        void runRequest(Callable<List<T>> callable, Presenter<T> presenter);
    }

    interface Identifiable {
        String getID();
    }


    interface AdapterViewHolder<T extends ListContract.Identifiable> {

        /**
         * bind view to object
         *
         * @param t
         */
        void bindView(T t, ListContract.View<T> view);

        /**
         * reset the view details
         */
        void resetView();
    }

}
