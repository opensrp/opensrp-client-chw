package org.smartregister.chw.contract;

import android.content.Context;

import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public interface ChildHomeVisitContract {
    interface View {
        Presenter initializePresenter();

        void startFormActivity(JSONObject jsonForm);

        void updateBirthStatusTick();

        void updateObsIllnessStatusTick();
        Context getContext();
    }

    interface Presenter {
        ChildHomeVisitContract.View getView();

        void getLastEditData();

        void startBirthCertForm(JSONObject previousJson);

        void startObsIllnessCertForm(JSONObject previousJson);

        void generateBirthIllnessForm(String jsonString);

        void saveForm();

        void onDestroy(boolean isChangingConfiguration);
    }

    interface Interactor {
        void getLastEditData(CommonPersonObjectClient childClient, InteractorCallback callback);

        void generateBirthIllnessForm(String jsonString, InteractorCallback callback,boolean isEditMode);

        void saveForm();

        void onDestroy(boolean isChangingConfiguration);
    }

    interface InteractorCallback {
        void updateBirthCertEditData(String json);

        void updateObsIllnessEditData(String json);

        void updateBirthStatusTick();

        void updateObsIllnessStatusTick();
    }
}
