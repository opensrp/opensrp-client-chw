package org.smartregister.chw.presenter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.contract.ChildRegisterContract;
import org.smartregister.chw.interactor.ChildRegisterInteractor;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.contract.FamilyRegisterContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.interactor.FamilyRegisterInteractor;
import org.smartregister.family.model.BaseFamilyRegisterModel;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;

import java.lang.ref.WeakReference;
import java.util.List;

public class ChildRegisterPresenter implements ChildRegisterContract.Presenter, ChildRegisterContract.InteractorCallBack {
    public static final String TAG = ChildRegisterPresenter.class.getName();

    private WeakReference<ChildRegisterContract.View> viewReference;
    private ChildRegisterContract.Interactor interactor;
    private ChildRegisterContract.Model model;

    public ChildRegisterPresenter(ChildRegisterContract.View view, ChildRegisterContract.Model model) {
        viewReference = new WeakReference<>(view);
        interactor = new ChildRegisterInteractor();
        this.model = model;
    }

    public void setModel(ChildRegisterContract.Model model) {
        this.model = model;
    }

    public void setInteractor(ChildRegisterContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        model.registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        model.unregisterViewConfiguration(viewIdentifiers);
    }

    @Override
    public void saveLanguage(String language) {
        model.saveLanguage(language);
        getView().displayToast(language + " selected");
    }

    @Override
    public void startForm(String formName, String entityId, String metadata, String currentLocationId, String familyId) throws Exception {

        if (StringUtils.isBlank(entityId)) {
            Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);
            interactor.getNextUniqueId(triple, this, familyId);
            return;
        }
        if(TextUtils.isEmpty(familyId)){
            JSONObject form=  new BaseFamilyRegisterModel().getFormAsJson(formName, entityId, currentLocationId);
            getView().startFormActivity(form);
        }else{
            JSONObject form = model.getFormAsJson(formName, entityId, currentLocationId, familyId);
            getView().startFormActivity(form);
        }



    }

    @Override
    public void closeFamilyRecord(String jsonString) {

        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getView().getContext());
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

            Log.d("JSONResult", jsonString);
            //getView().showProgressDialog(jsonString.contains(Constants.EventType.CLOSE) ? R.string.removing_dialog_title : R.string.saving_dialog_title);

            interactor.removeChildFromRegister(jsonString, allSharedPreferences.fetchRegisteredANM());

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));

        }
    }

    @Override
    public void saveForm(String jsonString, boolean isEditMode) {

        try {

            getView().showProgressDialog(R.string.saving_dialog_title);
            JSONObject form = new JSONObject(jsonString);
            if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyRegister.registerEventType))
            {

                List<FamilyEventClient> fevent = new BaseFamilyRegisterModel().processRegistration(jsonString);
                if (fevent == null) {
                    return;
                }
                new FamilyRegisterInteractor().saveRegistration(fevent, jsonString, isEditMode, new FamilyRegisterContract.InteractorCallBack() {
                    @Override
                    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {

                    }

                    @Override
                    public void onNoUniqueId() {

                    }

                    @Override
                    public void onRegistrationSaved(boolean isEdit) {
                        getView().hideProgressDialog();
                        getView().openFamilyListView();


                    }
                });

            }else{

                Pair<Client, Event> pair = model.processRegistration(jsonString);
                if (pair == null) {
                    return;
                }

                interactor.saveRegistration(pair, jsonString, isEditMode, this);
            }


        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void onNoUniqueId() {
        getView().displayShortToast(R.string.no_unique_id);
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId, String familyId) {
        try {
            startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight(), familyId);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            getView().displayToast(R.string.error_unable_to_start_form);
        }
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        getView().refreshList(FetchStatus.fetched);
        getView().hideProgressDialog();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

        viewReference = null;//set to null on destroy
        // Inform interactor
        interactor.onDestroy(isChangingConfiguration);
        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            interactor = null;
            model = null;
        }
    }

    @Override
    public void updateInitials() {
        String initials = model.getInitials();
        if (initials != null) {
            getView().updateInitialsText(initials);
        }
    }

    private ChildRegisterContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }
}
