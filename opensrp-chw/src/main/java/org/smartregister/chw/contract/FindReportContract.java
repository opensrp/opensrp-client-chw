package org.smartregister.chw.contract;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

public interface FindReportContract {

    interface Model {

        void getLocationFilter();

    }

    interface View {

        void bindLayout();

        void runReport();

        @NonNull
        void loadPresenter();

        void startResultsView(Bundle bundle);
    }

    interface Presenter {

        void runReport(Map<String, String> parameters);

        void initalizeParams();

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

        @Nullable
        View getView();
    }
}
