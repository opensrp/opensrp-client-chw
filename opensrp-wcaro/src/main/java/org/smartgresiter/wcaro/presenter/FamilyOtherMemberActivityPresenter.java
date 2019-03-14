package org.smartgresiter.wcaro.presenter;

import android.util.Log;

import org.apache.commons.lang3.tuple.Triple;
import org.smartgresiter.wcaro.contract.FamilyOtherMemberProfileExtendedContract;
import org.smartgresiter.wcaro.interactor.FamilyProfileInteractor;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyOtherMemberContract;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.model.BaseFamilyProfileModel;
import org.smartregister.family.presenter.BaseFamilyOtherMemberProfileActivityPresenter;

import java.lang.ref.WeakReference;

public class FamilyOtherMemberActivityPresenter extends BaseFamilyOtherMemberProfileActivityPresenter implements FamilyOtherMemberProfileExtendedContract.Presenter, FamilyProfileContract.InteractorCallBack {
    private static final String TAG = FamilyOtherMemberActivityPresenter.class.getCanonicalName();

    private WeakReference<FamilyOtherMemberProfileExtendedContract.View> viewReference;
    private String familyBaseEntityId;
    private String familyName;

    private FamilyProfileContract.Interactor profileInteractor;
    private FamilyProfileContract.Model profileModel;

    public FamilyOtherMemberActivityPresenter(FamilyOtherMemberProfileExtendedContract.View view, FamilyOtherMemberContract.Model model,
                                              String viewConfigurationIdentifier, String familyBaseEntityId, String baseEntityId,
                                              String familyHead, String primaryCaregiver, String villageTown, String familyName) {
        super(view, model, viewConfigurationIdentifier, baseEntityId, familyHead, primaryCaregiver, villageTown);
        viewReference = new WeakReference<>(view);
        this.familyBaseEntityId = familyBaseEntityId;
        this.familyName = familyName;

        this.profileInteractor = new FamilyProfileInteractor();
        this.profileModel = new BaseFamilyProfileModel(familyName);
    }

    public String getFamilyBaseEntityId() {
        return familyBaseEntityId;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void updateFamilyMember(String jsonString) {

        try {
            getView().showProgressDialog(org.smartregister.family.R.string.saving_dialog_title);

            FamilyEventClient familyEventClient = profileModel.processUpdateMemberRegistration(jsonString, familyBaseEntityId);
            if (familyEventClient == null) {
                return;
            }

            profileInteractor.saveRegistration(familyEventClient, jsonString, true, this);
        } catch (Exception e) {
            getView().hideProgressDialog();
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void startFormForEdit(CommonPersonObjectClient commonPersonObject) {
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
    }

    @Override
    public void onNoUniqueId() {
    }

    @Override
    public void onRegistrationSaved(boolean isEditMode) {
        getView().hideProgressDialog();

        refreshProfileView();

        getView().refreshList();
    }

    public FamilyOtherMemberProfileExtendedContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

}
