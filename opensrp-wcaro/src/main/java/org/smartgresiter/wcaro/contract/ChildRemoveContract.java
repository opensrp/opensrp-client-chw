package org.smartgresiter.wcaro.contract;

import android.content.Context;

import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public interface ChildRemoveContract {
    interface View{
        Presenter getPresenter();
        void startJsonActivity(JSONObject form);
        Context getContext();
        void onChildRemove();
    }
    interface Presenter {
        void removeMember(CommonPersonObjectClient commonPersonObjectClient);
        void processRemoveForm(JSONObject form);
        void onChildRemove();
    }

}
