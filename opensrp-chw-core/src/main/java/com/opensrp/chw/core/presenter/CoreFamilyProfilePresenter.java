package com.opensrp.chw.core.presenter;

import android.content.Context;
import android.util.Pair;

import com.opensrp.chw.core.R;
import com.opensrp.chw.core.contract.CoreChildRegisterContract;
import com.opensrp.chw.core.contract.FamilyProfileExtendedContract;
import com.opensrp.chw.core.domain.FamilyMember;
import com.opensrp.chw.core.interactor.CoreChildRegisterInteractor;
import com.opensrp.chw.core.interactor.CoreFamilyProfileInteractor;
import com.opensrp.chw.core.model.CoreChildProfileModel;
import com.opensrp.chw.core.model.CoreChildRegisterModel;
import com.opensrp.chw.core.utils.CoreConstants;
import com.opensrp.chw.core.utils.CoreJsonFormUtils;
import com.opensrp.chw.core.utils.Utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.presenter.BaseFamilyProfilePresenter;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.view.LocationPickerView;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public abstract class CoreFamilyProfilePresenter extends BaseFamilyProfilePresenter implements FamilyProfileExtendedContract.Presenter, CoreChildRegisterContract.InteractorCallBack, FamilyProfileExtendedContract.PresenterCallBack {

    private WeakReference<FamilyProfileExtendedContract.View> viewReference;
    private CoreChildRegisterInteractor childRegisterInteractor;
    private CoreChildProfileModel childProfileModel;


    public CoreFamilyProfilePresenter(FamilyProfileExtendedContract.View view, FamilyProfileContract.Model model, String familyBaseEntityId, String familyHead, String primaryCaregiver, String familyName) {
        super(view, model, familyBaseEntityId, familyHead, primaryCaregiver, familyName);
        viewReference = new WeakReference<>(view);
        childRegisterInteractor = new CoreChildRegisterInteractor();
        childProfileModel = new CoreChildProfileModel(familyName);
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId, String familyId) {
        try {
            startChildForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight());
        } catch (Exception e) {
            Timber.e(e);
            getView().displayToast(R.string.error_unable_to_start_form);
        }
    }

    @Override
    public void startChildForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {
        if (StringUtils.isBlank(entityId)) {
            Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);
            childRegisterInteractor.getNextUniqueId(triple, this, familyBaseEntityId);
            return;
        }

        JSONObject form = childProfileModel.getFormAsJson(formName, entityId, currentLocationId, familyBaseEntityId);
        getView().startFormActivity(form);

    }

    @Override
    public void saveChildRegistration(Pair<Client, Event> pair, String jsonString, boolean isEditMode, CoreChildRegisterContract.InteractorCallBack callBack) {
        childRegisterInteractor.saveRegistration(pair, jsonString, isEditMode, this);
    }


    @Override
    public void saveChildForm(String jsonString, boolean isEditMode) {
        try {

            getView().showProgressDialog(R.string.saving_dialog_title);

            Pair<Client, Event> pair = getChildRegisterModel().processRegistration(jsonString);
            if (pair == null) {
                return;
            }
            saveChildRegistration(pair, jsonString, isEditMode, this);

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    protected abstract CoreChildRegisterModel getChildRegisterModel();

    @Override
    public void verifyHasPhone() {
        ((CoreFamilyProfileInteractor) interactor).verifyHasPhone(familyBaseEntityId, this);
    }

    @Override
    public String saveChwFamilyMember(String jsonString) {
        try {
            getView().showProgressDialog(org.smartregister.family.R.string.saving_dialog_title);

            FamilyEventClient familyEventClient = model.processMemberRegistration(jsonString, familyBaseEntityId);
            if (familyEventClient == null) {
                return null;
            }

            interactor.saveRegistration(familyEventClient, jsonString, false, this);
            return familyEventClient.getClient().getBaseEntityId();
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    public boolean updatePrimaryCareGiver(Context context, String jsonString, String familyBaseEntityId, String entityID) {

        boolean res = false;
        try {
            FamilyMember member = CoreJsonFormUtils.getFamilyMemberFromRegistrationForm(jsonString, familyBaseEntityId, entityID);
            if (member != null && member.getPrimaryCareGiver()) {
                LocationPickerView lpv = new LocationPickerView(context);
                lpv.init();
                String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());

             //   new CoreFamilyChangeContractInteractor().updateFamilyRelations(context, member, lastLocationId);
                res = true;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return res;
    }

    @Override
    public void notifyHasPhone(boolean hasPhone) {
        if (viewReference.get() != null) {
            viewReference.get().updateHasPhone(hasPhone);
        }
    }

    public FamilyProfileExtendedContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

    @Override
    public void startFormForEdit(CommonPersonObjectClient client) {
        JSONObject form = CoreJsonFormUtils.getAutoPopulatedJsonEditFormString(CoreConstants.JSON_FORM.getFamilyDetailsRegister(), getView().getApplicationContext(), client, Utils.metadata().familyRegister.updateEventType);
        try {
            getView().startFormActivity(form);
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
