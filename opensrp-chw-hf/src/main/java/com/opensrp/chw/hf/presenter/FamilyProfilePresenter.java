package com.opensrp.chw.hf.presenter;

import android.content.Context;
import android.util.Pair;

import com.opensrp.chw.core.contract.CoreChildRegisterContract;
import com.opensrp.chw.core.contract.FamilyProfileExtendedContract;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.presenter.BaseFamilyProfilePresenter;

public class FamilyProfilePresenter extends BaseFamilyProfilePresenter implements FamilyProfileExtendedContract.Presenter, CoreChildRegisterContract.InteractorCallBack , FamilyProfileExtendedContract.PresenterCallBack {

    public FamilyProfilePresenter(FamilyProfileContract.View loginView, FamilyProfileContract.Model model, String familyBaseEntityId, String familyHead, String primaryCaregiver, String familyName) {
        super(loginView, model, familyBaseEntityId, familyHead, primaryCaregiver, familyName);
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId, String familyId) {

    }

    @Override
    public void saveChildRegistration(Pair<Client, Event> pair, String jsonString, boolean isEditMode, CoreChildRegisterContract.InteractorCallBack callBack) {

    }

    @Override
    public void startChildForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {

    }

    @Override
    public void saveChildForm(String jsonString, boolean isEditMode) {

    }

    @Override
    public void verifyHasPhone() {

    }

    @Override
    public void notifyHasPhone(boolean hasPhone) {

    }

    @Override
    public String saveChwFamilyMember(String jsonString) {
        return null;
    }

    @Override
    public boolean updatePrimaryCareGiver(Context context, String jsonString, String familyBaseEntityId, String entityID) {
        return false;
    }
}
