package com.opensrp.chw.hf.presenter;

import android.content.Context;
import android.util.Pair;

import com.opensrp.chw.core.contract.ChildRegisterContract;
import com.opensrp.chw.core.contract.FamilyProfileExtendedContract;
import com.opensrp.chw.core.presenter.CoreFamilyProfilePresenter;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.family.contract.FamilyProfileContract;

public class FamilyProfilePresenter extends CoreFamilyProfilePresenter{

    public FamilyProfilePresenter(FamilyProfileExtendedContract.View loginView, FamilyProfileContract.Model model, String familyBaseEntityId, String familyHead, String primaryCaregiver, String familyName) {
        super(loginView, model, familyBaseEntityId, familyHead, primaryCaregiver, familyName);
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId, String familyId) {

    }

    @Override
    public void saveChildRegistration(Pair<Client, Event> pair, String jsonString, boolean isEditMode, ChildRegisterContract.InteractorCallBack callBack) {

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
