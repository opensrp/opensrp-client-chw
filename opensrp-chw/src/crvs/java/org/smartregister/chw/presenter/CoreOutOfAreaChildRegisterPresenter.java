package org.smartregister.chw.presenter;

import static org.smartregister.chw.util.CrvsConstants.OUT_OF_AREA_ENCOUNTER_TYPE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.chw.activity.OutOfAreaChildActivity;
import org.smartregister.chw.activity.OutOfAreaChildUpdateActivity;
import org.smartregister.chw.contract.CoreOutOfAreaChildRegisterContract;
import org.smartregister.chw.core.presenter.CoreChildRegisterPresenter;
import org.smartregister.chw.interactor.CoreOutOfAreaChildRegisterInteractor;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.model.BaseFamilyRegisterModel;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class CoreOutOfAreaChildRegisterPresenter implements CoreOutOfAreaChildRegisterContract.Presenter, CoreOutOfAreaChildRegisterContract.InteractorCallBack {
    public static final String TAG = CoreChildRegisterPresenter.class.getName();
    private WeakReference<CoreOutOfAreaChildRegisterContract.View> viewReference;
    private CoreOutOfAreaChildRegisterContract.Interactor interactor;
    private CoreOutOfAreaChildRegisterContract.Model model;

    public CoreOutOfAreaChildRegisterPresenter(CoreOutOfAreaChildRegisterContract.View view, CoreOutOfAreaChildRegisterContract.Model model) {
        viewReference = new WeakReference<>(view);
        interactor = new CoreOutOfAreaChildRegisterInteractor(Objects.requireNonNull(getView()).getContext());
        this.model = model;
    }

    public void setModel(CoreOutOfAreaChildRegisterContract.Model model) {
        this.model = model;
    }

    public void setInteractor(CoreOutOfAreaChildRegisterContract.Interactor interactor) {
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
        if (initials != null && getView() != null) {
            getView().updateInitialsText(initials);
        }
    }

    private CoreOutOfAreaChildRegisterContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

    @Override
    public void saveLanguage(String language) {
        model.saveLanguage(language);
        Objects.requireNonNull(getView()).displayToast(language + " selected");
    }

    @Override
    public void startForm(String formName, String entityId, String metadata, String currentLocationId, String familyId) throws Exception {

        if (StringUtils.isBlank(entityId)) {
            Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);
            interactor.getNextUniqueId(triple, this, familyId);
            return;
        }
        if (TextUtils.isEmpty(familyId)) {
            JSONObject form = new BaseFamilyRegisterModel().getFormAsJson(formName, entityId, currentLocationId);
            if (getView() != null)
                getView().startFormActivity(form);
        } else {
            JSONObject form = model.getFormAsJson(formName, entityId, currentLocationId, familyId);
            if (getView() != null)
                getView().startFormActivity(form);
        }
    }

    @Override
    public void saveOutOfAreaForm(String jsonString, boolean isEditMode) {

        try {
            if (getView() != null)
                getView().showProgressDialog(org.smartregister.chw.core.R.string.saving_dialog_title);
            JSONObject form = new JSONObject(jsonString);
            if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(OUT_OF_AREA_ENCOUNTER_TYPE)) {

                Pair<Client, Event> clientEventPair = model.processRegistration(jsonString);
                if (clientEventPair == null) {
                    return;
                }

                new CoreOutOfAreaChildRegisterInteractor(getView().getContext()).saveRegistration(clientEventPair, jsonString, isEditMode, new CoreOutOfAreaChildRegisterContract.InteractorCallBack() {
                    @Override
                    public void onNoUniqueId() {
                        // Do nothing
                    }

                    @Override
                    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId, String familyId) {
                        // Do nothing
                    }

                    @Override
                    public void onRegistrationSaved(boolean editMode, boolean isSaved, FamilyEventClient familyEventClient) {
                        getView().hideProgressDialog();
                        getView().openFamilyListView();
                        if (!isSaved && getView().getContext() != null) {
                            Toast.makeText(getView().getContext(), "Saving failed", Toast.LENGTH_SHORT).show();
                        } else {
                            getView().getContext().startActivity(new Intent(getView().getContext(), OutOfAreaChildActivity.class));
                            ((Activity) getView().getContext()).finish();
                        }
                    }
                });

            } else {

                Pair<Client, Event> pair = model.processRegistration(jsonString);
                if (pair == null) {
                    return;
                }

                interactor.saveRegistration(pair, jsonString, isEditMode, this);
            }


        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void closeFamilyRecord(String jsonString) {

        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getView()).getContext());
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

            Timber.d("JSONResult : %s", jsonString);

            interactor.removeChildFromRegister(jsonString, allSharedPreferences.fetchRegisteredANM());

        } catch (Exception e) {
            Timber.e(e);

        }
    }

    @Override
    public void addOutOfAreaChild(Class activity) {
        Intent intent = new Intent(Objects.requireNonNull(getView()).getContext(), OutOfAreaChildActivity.class);
        getView().getContext().startActivity(intent);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void registerFloatingActionButton(View v, int visibility) {
        FloatingActionButton addClientsFab = v.findViewById(org.smartregister.chw.core.R.id.add_clients_fab);
        addClientsFab.setVisibility(View.VISIBLE);

        addClientsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interactor.openActivityOnFloatingButtonClick(Objects.requireNonNull(getView()).getContext(), OutOfAreaChildUpdateActivity.class);
            }
        });
    }

    @Override
    public void onNoUniqueId() {
        Objects.requireNonNull(getView()).displayShortToast(org.smartregister.chw.core.R.string.no_unique_id);
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId, String familyId) {
        try {
            startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight(), familyId);
        } catch (Exception e) {
            Timber.e(e);
            if (getView() != null)
                getView().displayToast(org.smartregister.chw.core.R.string.error_unable_to_start_form);
        }
    }

    @Override
    public void onRegistrationSaved(boolean editMode, boolean isSaved, FamilyEventClient familyEventClient) {
        if (getView() != null) {
            getView().refreshList(FetchStatus.fetched);
            getView().hideProgressDialog();
        }
    }

}
