package org.smartregister.chw.contract;

import org.smartregister.chw.pinlogin.PinLogger;

public interface PinViewContract {

    interface Controller {

        void navigateToFragment(String destinationFragment);

        void startPasswordLogin();

        void startHomeActivity();

        PinLogger getPinLogger();
    }
}
