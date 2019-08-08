package com.opensrp.chw.hf.presenter;

import android.util.Pair;

import com.opensrp.chw.core.contract.CoreChildProfileContract;
import com.opensrp.chw.core.presenter.CoreChildProfilePresenter;
import com.opensrp.chw.hf.interactor.HfChildProfileInteractor;
import com.opensrp.chw.hf.interactor.HfFamilyProfileInteractor;
import com.opensrp.chw.hf.model.ChildRegisterModel;
import com.opensrp.hf.R;

import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

import java.lang.ref.WeakReference;

public class HfChildProfilePresenter extends CoreChildProfilePresenter {

    public HfChildProfilePresenter(CoreChildProfileContract.View childView, CoreChildProfileContract.Model model, String childBaseEntityId) {
        setView(new WeakReference<>(childView));
        setInteractor(new HfChildProfileInteractor());
        setModel(model);
        setChildBaseEntityId(childBaseEntityId);
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

    @Override
    public void verifyHasPhone() {
        new HfFamilyProfileInteractor().verifyHasPhone(familyID, this);
    }
}
