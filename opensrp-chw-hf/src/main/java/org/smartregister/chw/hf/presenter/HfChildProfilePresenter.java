package org.smartregister.chw.hf.presenter;

import android.util.Pair;

import org.smartregister.chw.core.contract.CoreChildProfileContract;
import org.smartregister.chw.core.presenter.CoreChildProfilePresenter;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.interactor.HfChildProfileInteractor;
import org.smartregister.chw.hf.interactor.HfFamilyProfileInteractor;
import org.smartregister.chw.hf.model.ChildRegisterModel;
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
    public void verifyHasPhone() {
        new HfFamilyProfileInteractor().verifyHasPhone(familyID, this);
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
