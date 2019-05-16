package org.smartregister.chw.contract;

import org.json.JSONObject;

public interface MalariaConfirmationContract {
    interface View {
        void startFormActivity(JSONObject form);
    }

    interface Presenter {
        MalariaConfirmationContract.View getView();

        void startMalariaConfirmationForm();

    }

//    interface Interactor {
//
//    }
//
//    interface InteractorCallback {
//
//    }
}
