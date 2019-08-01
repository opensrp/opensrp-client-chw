package org.smartregister.chw.presenter;

import android.util.Pair;

import com.opensrp.chw.core.contract.CoreChildProfileContract;
import com.opensrp.chw.core.presenter.CoreChildProfilePresenter;

import org.smartregister.chw.R;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.chw.interactor.FamilyProfileInteractor;
import org.smartregister.chw.model.ChildRegisterModel;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

import java.lang.ref.WeakReference;

public class ChildProfilePresenter extends CoreChildProfilePresenter {

    public ChildProfilePresenter(CoreChildProfileContract.View childView, CoreChildProfileContract.Model model, String childBaseEntityId) {
        setView(new WeakReference<>(childView));
        setInteractor(new ChildProfileInteractor());
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
        new FamilyProfileInteractor().verifyHasPhone(familyID, this);
    }
}
