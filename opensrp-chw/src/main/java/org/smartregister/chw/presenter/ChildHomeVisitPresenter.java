package org.smartregister.chw.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.ChildHomeVisitContract;
import org.smartregister.chw.domain.HomeVisit;
import org.smartregister.chw.interactor.ChildHomeVisitInteractor;
import org.smartregister.chw.model.BirthIllnessModel;
import org.smartregister.chw.model.ChildRegisterModel;
import org.smartregister.chw.util.BirthIllnessData;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;
import org.smartregister.util.FormUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class ChildHomeVisitPresenter implements ChildHomeVisitContract.Presenter, ChildHomeVisitContract.InteractorCallback {
    private WeakReference<ChildHomeVisitContract.View> view;
    private ChildHomeVisitContract.Interactor interactor;
    private CommonPersonObjectClient childClient;
    private FormUtils formUtils = null;

    public ChildHomeVisitPresenter(ChildHomeVisitContract.View view) {
        this.view = new WeakReference<>(view);
        interactor = new ChildHomeVisitInteractor();
    }

    public void setChildClient(CommonPersonObjectClient childClient) {
        this.childClient = childClient;

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
            if(previousJson!=null){
                getView().startFormActivity(new JSONObject(previousJson.getString("birtCert")));
            }else{
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
            if(previousJson!=null){
                getView().startFormActivity(new JSONObject(previousJson.getString("obsIllness")));
            }else{
                JSONObject revForm = JsonFormUtils.getOnsIllnessFormAsJson(form, childClient.getCaseId(), "", dobString);
                getView().startFormActivity(revForm);

            }

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    @Override
    public void updateBirthStatusTick() {
        getView().updateBirthStatusTick();

    }

    @Override
    public void updateObsIllnessStatusTick() {
        getView().updateObsIllnessStatusTick();

    }

    @Override
    public void generateBirthIllnessForm(String jsonString) {
        interactor.generateBirthIllnessForm(jsonString, this,false);

    }

    @Override
    public void updateBirthCertEditData(String jsonString) {
        interactor.generateBirthIllnessForm(jsonString, this,true);
    }

    @Override
    public void updateObsIllnessEditData(String json) {
        interactor.generateBirthIllnessForm(json,this,true);
    }

    @Override
    public void saveForm() {
        interactor.saveForm();
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
        interactor.getLastEditData(childClient,this);

    }

    public ArrayList<BirthIllnessData> getIllnessDataList(){
        return ((ChildHomeVisitInteractor)interactor).getIllnessDataList();
    }
    public ArrayList<BirthIllnessData> getBirthCertDataList(){
        return ((ChildHomeVisitInteractor)interactor).getBirthCertDataList();
    }

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
