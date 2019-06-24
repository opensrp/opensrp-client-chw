package org.smartregister.chw.presenter;

import android.util.Log;

import org.json.JSONObject;
import org.smartregister.chw.contract.ChildHomeVisitContract;
import org.smartregister.chw.interactor.ChildHomeVisitInteractor;
import org.smartregister.chw.model.ChildRegisterModel;
import org.smartregister.chw.util.BirthIllnessData;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.chw.util.ServiceTask;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;
import org.smartregister.util.FormUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ChildHomeVisitPresenter implements ChildHomeVisitContract.Presenter, ChildHomeVisitContract.InteractorCallback {
    private WeakReference<ChildHomeVisitContract.View> view;
    private ChildHomeVisitContract.Interactor interactor;
    private CommonPersonObjectClient childClient;
    private FormUtils formUtils = null;
    private String editedBirthCertFormJson, editedIllnessJson, editedCounselingJson;
    private ArrayList<ServiceTask> serviceTasks = new ArrayList<>();

    public ChildHomeVisitPresenter(ChildHomeVisitContract.View view) {
        this.view = new WeakReference<>(view);
        interactor = new ChildHomeVisitInteractor();
    }

    public void setChildClient(CommonPersonObjectClient childClient) {
        this.childClient = childClient;

    }

    public String getEditedBirthCertFormJson() {
        return editedBirthCertFormJson;
    }

    public String getEditedIllnessJson() {
        return editedIllnessJson;
    }

    public String getEditedCounselingJson() {
        return editedCounselingJson;
    }
    public ArrayList<ServiceTask> getServiceTasks() {
        return serviceTasks;
    }

    public int getSaveSize() {
        return ((ChildHomeVisitInteractor) interactor).getSaveSize();
    }

    @Override
    public void startBirthCertForm(JSONObject previousJson) {
        try {
            JSONObject form = getFormUtils().getFormJson(Constants.JSON_FORM.BIRTH_CERTIFICATION);
            String dobString = org.smartregister.family.util.Utils.getValue
                    (childClient.getColumnmaps(), DBConstants.KEY.DOB, false);
            if (previousJson != null) {
                getView().startFormActivity(new JSONObject(previousJson.getString("birtCert")));
            } else {
                JSONObject revForm = JsonFormUtils.getBirthCertFormAsJson(form, childClient.getCaseId(), "", dobString);
                getView().startFormActivity(revForm);

            }

        } catch (Exception e) {

        }

    }

    @Override
    public void startObsIllnessCertForm(JSONObject previousJson) {
        try {

            JSONObject form = getFormUtils().getFormJson(Constants.JSON_FORM.OBS_ILLNESS);
            String dobString = org.smartregister.family.util.Utils.getValue
                    (childClient.getColumnmaps(), DBConstants.KEY.DOB, false);
            if (previousJson != null) {
                getView().startFormActivity(new JSONObject(previousJson.getString("obsIllness")));
            } else {
                JSONObject revForm = JsonFormUtils.getOnsIllnessFormAsJson(form, childClient.getCaseId(), "", dobString);
                getView().startFormActivity(revForm);

            }

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    /*
    @Override
    public void startCounselingForm(JSONObject previousJson) {
        try {

            JSONObject form = getFormUtils().getFormJson(Constants.JSON_FORM.HOME_VISIT_COUNSELLING);
            if (previousJson != null && previousJson.getString("counseling") != null) {
                getView().startFormActivity(new JSONObject(previousJson.getString("counseling")));
            } else {
                getView().startFormActivity(form);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    */

    @Override
    public void updateBirthStatusTick() {
        getView().updateBirthStatusTick();
    }

    @Override
    public void updateObsIllnessStatusTick() {
        getView().updateObsIllnessStatusTick();

    }

    /*
    @Override
    public void updateCounselingStatusTick() {
        getView().updateCounselingStatusTick();
    }
    */

    @Override
    public void generateBirthIllnessForm(String jsonString) {
        interactor.generateBirthIllnessForm(jsonString, this, false);
    }

    /*
    @Override
    public void generateCounselingForm(String jsonString) {
        interactor.generateCounselingForm(jsonString, this, false);
    }
    */

    @Override
    public void updateBirthCertEditData(String jsonString) {
        editedBirthCertFormJson = jsonString;
        interactor.generateBirthIllnessForm(jsonString, this, true);
    }

    @Override
    public void updateObsIllnessEditData(String json) {
        editedIllnessJson = json;
        interactor.generateBirthIllnessForm(json, this, true);
    }

    @Override
    public void saveForm() {
        interactor.saveForm(childClient);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        view = null;//set to null on destroy

        // Inform interactor
        interactor.onDestroy(isChangingConfiguration);

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            interactor = null;
        }
    }

    @Override
    public ChildHomeVisitContract.View getView() {
        if (view != null) {
            return view.get();
        } else {
            return null;
        }
    }

    @Override
    public void getLastEditData() {
        interactor.getLastEditData(childClient, this);

    }

    public ArrayList<BirthIllnessData> getIllnessDataList() {
        return ((ChildHomeVisitInteractor) interactor).getIllnessDataList();
    }

    public ArrayList<BirthIllnessData> getBirthCertDataList() {
        return ((ChildHomeVisitInteractor) interactor).getBirthCertDataList();
    }

    /*
    public ArrayList<BirthIllnessData> getCounselingDataList() {
        return ((ChildHomeVisitInteractor) interactor).getCounselingDataList();
    }
    */


    private FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(org.smartregister.family.util.Utils.context().applicationContext());
            } catch (Exception e) {
                Log.e(ChildRegisterModel.class.getCanonicalName(), e.getMessage(), e);
            }
        }
        return formUtils;
    }


}
