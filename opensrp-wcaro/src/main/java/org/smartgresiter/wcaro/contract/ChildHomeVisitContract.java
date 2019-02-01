package org.smartgresiter.wcaro.contract;

import org.json.JSONObject;

public interface ChildHomeVisitContract {
    interface View {
        Presenter initializePresenter();

        void startFormActivity(JSONObject jsonForm);

        void updateBirthStatusTick();

        void updateObsIllnessStatusTick();
    }

    interface Presenter {
        ChildHomeVisitContract.View getView();

        void startBirthCertForm();

        void startObsIllnessCertForm();

        void generateBirthIllnessForm(String jsonString);

        void saveForm();

        void onDestroy(boolean isChangingConfiguration);
    }

    interface Interactor {
        void generateBirthIllnessForm(String jsonString, InteractorCallback callback);

        void saveForm();

        void onDestroy(boolean isChangingConfiguration);
    }

    interface InteractorCallback {
        void updateBirthStatusTick();

        void updateObsIllnessStatusTick();
    }
}
