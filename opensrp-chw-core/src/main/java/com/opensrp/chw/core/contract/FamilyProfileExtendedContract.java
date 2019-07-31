package com.opensrp.chw.core.contract;

import android.content.Context;
import android.util.Pair;

import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.family.contract.FamilyProfileContract;

public interface FamilyProfileExtendedContract {

    interface Presenter extends FamilyProfileContract.Presenter {

        void saveChildRegistration(final Pair<Client, Event> pair, final String jsonString, final boolean isEditMode, final CoreChildRegisterContract.InteractorCallBack callBack);

        void startChildForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception;

        void saveChildForm(String jsonString, boolean isEditMode);

        void verifyHasPhone();

        String saveChwFamilyMember(String jsonString);

        boolean updatePrimaryCareGiver(Context context, String jsonString, String familyBaseEntityId, String entityID);

    }

    interface View extends FamilyProfileContract.View {

        void startChildForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception;

        void updateHasPhone(boolean hasPhone);

    }

    interface PresenterCallBack {

        void verifyHasPhone();

        void notifyHasPhone(boolean hasPhone);
    }
}
