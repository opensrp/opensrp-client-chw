package org.smartgresiter.wcaro.presenter;

import android.util.Log;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.ChildRegisterContract;
import org.smartgresiter.wcaro.contract.FamilyProfileExtendedContract;
import org.smartgresiter.wcaro.interactor.ChildRegisterInteractor;
import org.smartgresiter.wcaro.model.ChildRegisterModel;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.presenter.BaseFamilyProfilePresenter;
import org.smartregister.family.util.Utils;
import org.smartregister.util.FormUtils;

import java.lang.ref.WeakReference;

public class FamilyProfilePresenter extends BaseFamilyProfilePresenter implements FamilyProfileExtendedContract.Presenter, ChildRegisterContract.InteractorCallBack {

    private FormUtils formUtils = null;
    private static final String TAG = FamilyProfilePresenter.class.getCanonicalName();

    private WeakReference<FamilyProfileExtendedContract.View> viewReference;
    private ChildRegisterInteractor childRegisterInteractor;


    public FamilyProfilePresenter(FamilyProfileExtendedContract.View view, FamilyProfileContract.Model model, String familyBaseEntityId) {
        super(view, model, familyBaseEntityId);
        viewReference = new WeakReference<>(view);
        childRegisterInteractor = new ChildRegisterInteractor();
    }

    @Override
    public void startFormForEdit(CommonPersonObjectClient client) {
        JSONObject form = JsonFormUtils.getAutoPopulatedJsonEditFormString(Constants.JSON_FORM.FAMILY_DETAILS_REGISTER, getView().getApplicationContext(), client);
        try {
            getView().startFormActivity(form);
        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
        }
    }

    // Child form

    @Override
    public void startChildForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {
        if (StringUtils.isBlank(entityId)) {
            Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);
            childRegisterInteractor.getNextUniqueId(triple, this, familyBaseEntityId);
            return;
        }

        JSONObject form = getFormAsJson(formName, entityId, currentLocationId, familyBaseEntityId);
        getView().startFormActivity(form);

    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId, String familyId) {
        try {
            startChildForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight());
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            getView().displayToast(R.string.error_unable_to_start_form);
        }
    }

    @Override
    public void saveChildRegistration(Pair<Client, Event> pair, String jsonString, boolean isEditMode, ChildRegisterContract.InteractorCallBack callBack) {
        childRegisterInteractor.saveRegistration(pair, jsonString, isEditMode, this);
    }

    @Override
    public void saveChildForm(String jsonString, boolean isEditMode) {
        ChildRegisterModel model = new ChildRegisterModel();
        try {

            getView().showProgressDialog(R.string.saving_dialog_title);

            Pair<Client, Event> pair = model.processRegistration(jsonString);
            if (pair == null) {
                return;
            }
            saveChildRegistration(pair, jsonString, isEditMode, this);

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId, String familyID) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        return JsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId, familyID);
    }

    private FormUtils getFormUtils() {

        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(Utils.context().applicationContext());
            } catch (Exception e) {
                Log.e(ChildRegisterModel.class.getCanonicalName(), e.getMessage(), e);
            }
        }
        return formUtils;
    }

    public FamilyProfileExtendedContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }
}
