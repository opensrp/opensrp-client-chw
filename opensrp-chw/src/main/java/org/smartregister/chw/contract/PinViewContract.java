package org.smartregister.chw.contract;

public interface PinViewContract {

    interface Controller {

        void navigateToFragment(String destinationFragment);

        void startPasswordLogin();
    }
}
