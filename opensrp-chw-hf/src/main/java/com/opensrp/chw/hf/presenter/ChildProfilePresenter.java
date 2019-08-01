package com.opensrp.chw.hf.presenter;

import android.util.Pair;

import com.opensrp.chw.core.contract.CoreChildProfileContract;
import com.opensrp.chw.core.presenter.CoreChildProfilePresenter;
import com.opensrp.chw.hf.model.ChildRegisterModel;
import com.opensrp.hf.R;

import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

public class ChildProfilePresenter extends CoreChildProfilePresenter {

    public ChildProfilePresenter(CoreChildProfileContract.View childView, CoreChildProfileContract.Model model, String childBaseEntityId) {
       super(childView,model,childBaseEntityId);
    }


    @Override
    public void updateChildProfile(String jsonString) {
        getView().showProgressDialog(R.string.updating);
        Pair<Client, Event> pair = new ChildRegisterModel().processRegistration(jsonString);
        if (pair == null) {
            return;
        }

        getInteractor().saveRegistration(pair, jsonString, true, this);
    }
}
