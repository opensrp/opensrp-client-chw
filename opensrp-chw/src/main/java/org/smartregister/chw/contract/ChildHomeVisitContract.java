package org.smartregister.chw.contract;

import android.content.Context;

import org.json.JSONObject;
import org.smartregister.chw.util.ServiceTask;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;

public interface ChildHomeVisitContract {
    interface View {
        Presenter initializePresenter();

        void startFormActivity(JSONObject jsonForm);

        void updateBirthStatusTick();

        void updateObsIllnessStatusTick();

        void updateTaskService();

        // void updateCounselingStatusTick();

        Context getContext();
    }

    interface Presenter {
        ChildHomeVisitContract.View getView();

        void getLastEditData();

        void startBirthCertForm(JSONObject previousJson);

        void startObsIllnessCertForm(JSONObject previousJson);

        //void startCounselingForm(JSONObject previousJson);

        void generateBirthIllnessForm(String jsonString);

        //void generateCounselingForm(String jsonString);

        void generateTaskService(boolean isEditMode);

        ArrayList<ServiceTask> getServiceTasks();

        void saveForm();

        void onDestroy(boolean isChangingConfiguration);
    }

    interface Interactor {
        void getLastEditData(CommonPersonObjectClient childClient, InteractorCallback callback);

        void generateBirthIllnessForm(String jsonString, InteractorCallback callback, boolean isEditMode);

        //void generateCounselingForm(String jsonString, InteractorCallback callback, boolean isEditMode);
        void generateTaskService(CommonPersonObjectClient childClient, InteractorCallback callback, boolean isEditMode);

        void saveForm(CommonPersonObjectClient childClient);

        void onDestroy(boolean isChangingConfiguration);
    }

    interface InteractorCallback {
        void updateBirthCertEditData(String json);

        void updateObsIllnessEditData(String json);

        void updateBirthStatusTick();

        void updateObsIllnessStatusTick();

        void updateTaskAdapter(ArrayList<ServiceTask> serviceTasks);

        //void updateCounselingStatusTick();
    }
}
