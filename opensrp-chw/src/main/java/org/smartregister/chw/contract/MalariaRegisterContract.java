package org.smartregister.chw.contract;

import android.util.Pair;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.view.contract.BaseRegisterContract;

import java.util.List;

/**
 * Created by keyamn on 12/11/2018.
 */
public interface MalariaRegisterContract {

    interface View extends BaseRegisterContract.View {
        MalariaRegisterContract.Presenter presenter();
//        void openFamilyListView();
    }

    interface Presenter extends BaseRegisterContract.Presenter {

//        void saveLanguage(String language);
//
//        void startForm(String formName, String entityId, String metadata, String currentLocationId, String familyID) throws Exception;
//
//        void saveForm(String jsonString, boolean isEditMode);
//
//        void closeFamilyRecord(String jsonString);

    }

    interface Model {

        void registerViewConfigurations(List<String> viewIdentifiers);

        void unregisterViewConfiguration(List<String> viewIdentifiers);

        void saveLanguage(String language);

        String getLocationId(String locationName);

        Pair<Client, Event> processRegistration(String jsonString);

        Pair<Client, Event> processMalariaRegistration(String jsonString);

        JSONObject getFormAsJson(String formName, String entityId,
                                 String currentLocationId, String familyId) throws Exception;

        String getInitials();

    }

    interface Interactor {

//        void onDestroy(boolean isChangingConfiguration);
//
//        void getNextUniqueId(Triple<String, String, String> triple, MalariaRegisterContract.InteractorCallBack callBack, String familyID);
//
//        void saveRegistration(final Pair<Client, Event> pair, final String jsonString, final boolean isEditMode, final MalariaRegisterContract.InteractorCallBack callBack);
//
//        void removeChildFromRegister(String closeFormJsonString, String providerId);

    }

    interface InteractorCallBack {

//        void onNoUniqueId();
//
//        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId, String familyId);
//
//        void onRegistrationSaved(boolean isEdit);

    }
}
