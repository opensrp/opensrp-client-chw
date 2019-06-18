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
public interface AncRegisterContract {


    interface Interactor {

        void getNextUniqueId(Triple<String, String, String> triple, AncRegisterContract.InteractorCallBack callBack, String familyID);

    }
    interface InteractorCallBack {

        void onNoUniqueId();

        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId, String familyId);

        void onRegistrationSaved(boolean isEdit);

    }


}
