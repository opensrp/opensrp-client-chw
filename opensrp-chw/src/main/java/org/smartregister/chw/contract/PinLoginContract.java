package org.smartregister.chw.contract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.chw.pinlogin.PinLogger;

public interface PinLoginContract {

    interface View {

        /**
         * disable all views to prevent
         */
        void onLoginInitiated(@NonNull PinLogger.EventListener eventListener);

        void onLoginAttemptFailed(String error);

        void onLoginCompleted();
    }

    interface Presenter {

        void localLogin(@NonNull String pin);

        @Nullable View getView();
    }

    interface Interactor {
        void authenticateUser(String userName, String password, @NonNull PinLogger.EventListener eventListener);
    }
}
