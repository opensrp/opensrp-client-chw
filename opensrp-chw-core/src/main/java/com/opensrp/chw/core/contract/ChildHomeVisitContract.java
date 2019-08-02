package com.opensrp.chw.core.contract;

import android.content.Context;

import com.opensrp.chw.core.utils.ServiceTask;

import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;

public interface ChildHomeVisitContract {
    interface View {
        Presenter initializePresenter();

        void startFormActivity(JSONObject jsonForm);

        void updateBirthStatusTick(String jsonString);

        void updateObsIllnessStatusTick(String jsonString);

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

        void generateBirthCertForm(String jsonString);

        //void generateCounselingForm(String jsonString);
        void generateObsIllnessForm(String jsonString);

        void generateTaskService(boolean isEditMode);

        ArrayList<ServiceTask> getServiceTasks();

        void saveForm();

        void onDestroy(boolean isChangingConfiguration);
    }

    interface Interactor {
        void getLastEditData(CommonPersonObjectClient childClient, InteractorCallback callback);

        void generateBirthCertForm(String jsonString, InteractorCallback callback, boolean isEditMode);

        void generateObsIllnessForm(String jsonString, InteractorCallback callback, boolean isEditMode);

        //void generateCounselingForm(String jsonString, InteractorCallback callback, boolean isEditMode);
        void generateTaskService(CommonPersonObjectClient childClient, InteractorCallback callback, Context context, boolean isEditMode);

        void saveForm(CommonPersonObjectClient childClient);

        void onDestroy(boolean isChangingConfiguration);
    }

    interface InteractorCallback {
        void updateBirthCertEditData(String json);

        void updateObsIllnessEditData(String json);

        void updateBirthStatusTick(String jsonString);

        void updateObsIllnessStatusTick(String jsonString);

        void updateTaskAdapter(ArrayList<ServiceTask> serviceTasks);

        //void updateCounselingStatusTick();
    }
}
