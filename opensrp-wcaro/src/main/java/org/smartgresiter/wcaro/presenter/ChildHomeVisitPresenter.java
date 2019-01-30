package org.smartgresiter.wcaro.presenter;

import android.util.Log;

import org.json.JSONObject;
import org.smartgresiter.wcaro.contract.ChildHomeVisitContract;
import org.smartgresiter.wcaro.interactor.ChildHomeVisitInteractor;
import org.smartgresiter.wcaro.model.ChildRegisterModel;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.JsonFormUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;
import org.smartregister.util.FormUtils;

import java.lang.ref.WeakReference;

public class ChildHomeVisitPresenter implements ChildHomeVisitContract.Presenter,ChildHomeVisitContract.InteractorCallback {
    private WeakReference<ChildHomeVisitContract.View> view;
    private ChildHomeVisitContract.Interactor interactor;
    private CommonPersonObjectClient childClient;
    private FormUtils formUtils = null;
    public ChildHomeVisitPresenter(ChildHomeVisitContract.View view) {
        this.view = new WeakReference<>(view);
        interactor = new ChildHomeVisitInteractor();
    }
    public void setChildClient(CommonPersonObjectClient childClient){
        this.childClient=childClient;

    }
    public int getSaveSize(){
        return ((ChildHomeVisitInteractor)interactor).getSaveSize();
    }

    @Override
    public void startBirthCertForm()  {
        try{
            JSONObject form = getFormUtils().getFormJson(Constants.JSON_FORM.BIRTH_CERTIFICATION);
            String dobString = org.smartregister.family.util.Utils.getDuration(org.smartregister.family.util.Utils.getValue
                    (childClient.getColumnmaps(), DBConstants.KEY.DOB, false));

            JSONObject revForm=JsonFormUtils.getBirthCertFormAsJson(form,childClient.getCaseId(),"",dobString);
            getView().startFormActivity(revForm);
        }catch (Exception e){

        }

    }
    @Override
    public void startObsIllnessCertForm()  {
        try{

            JSONObject form = getFormUtils().getFormJson(Constants.JSON_FORM.OBS_ILLNESS);
            String dobString = org.smartregister.family.util.Utils.getDuration(org.smartregister.family.util.Utils.getValue
                    (childClient.getColumnmaps(), DBConstants.KEY.DOB, false));

            JSONObject revForm=JsonFormUtils.getOnsIllnessFormAsJson(form,childClient.getCaseId(),"",dobString);
            getView().startFormActivity(revForm);
        }catch (Exception e){

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
        interactor.generateBirthIllnessForm(jsonString,this);

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
