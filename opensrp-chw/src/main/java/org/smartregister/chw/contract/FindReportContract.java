package org.smartregister.chw.contract;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public interface FindReportContract {

    interface Model {

        @NonNull
        LinkedHashMap<String, String> getAllLocations();

    }

    interface View {

        void setLoadingState(boolean loadingState);

        void bindLayout();

        void onLocationDataLoaded(Map<String, String> locationData);

        void runReport();

        @NonNull
        void loadPresenter();

        void startResultsView(Bundle bundle);
    }

    interface Presenter {

        void runReport(Map<String, String> parameters);

        void initializeViews();

        void onReportHierarchyLoaded(Map<String, String> locationData);

        /**
         * binds the view
         *
         * @param view
         */
        Presenter with(View view);

        /**
         * binds a views model
         *
         * @param model
         * @return
         */
        Presenter withModel(Model model);

        /**
         * binds an interactor
         *
         * @param interactor
         * @return
         */
        Presenter withInteractor(Interactor interactor);

        @Nullable
        View getView();
    }

    interface Interactor {

        void processAvailableLocations(LinkedHashMap<String, String> locations, Presenter presenter);
    }
}
