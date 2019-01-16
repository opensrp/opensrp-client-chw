package org.smartgresiter.wcaro.contract;

import android.util.Pair;

import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.family.contract.FamilyProfileContract;

public interface FamilyProfileExtendedContract {

    interface Presenter extends FamilyProfileContract.Presenter {

        void saveChildRegistration(final Pair<Client, Event> pair, final String jsonString, final boolean isEditMode, final ChildRegisterContract.InteractorCallBack callBack);

        void startChildForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception;

        void saveChildForm(String jsonString, boolean isEditMode);
    }

    interface View extends FamilyProfileContract.View {

        void startChildForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception;

    }
}
