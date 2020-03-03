package org.smartregister.chw.contract;

import androidx.annotation.LayoutRes;

import java.util.List;

public interface ListContract<T extends ListContract.Identifiable> {

    interface Model<T> {

    }

    interface View<T> {

        void renderData(List<T> identifiables);

        void refreshView();

        void setLoadingState(boolean loadingState);

        void onListItemClicked(T t, @LayoutRes int layoutID);

    }

    interface Presenter<T> {

        void fetchList(String baseEntityID);

        void onItemsFetched(List<Identifiable> identifiables);

    }

    interface Interactor<T> {

        /**
         *
         * @param identifiable
         * @param presenter
         */
        void runRequest(Identifiable identifiable, Presenter<T> presenter);
    }

    interface Identifiable {
        String getID();
    }


    interface AdapterViewHolder<T> {

        /**
         * bind view to object
         * @param t
         */
        void bindView(T t);

        /**
         * reset the view details
         */
        void resetView();
    }

}
