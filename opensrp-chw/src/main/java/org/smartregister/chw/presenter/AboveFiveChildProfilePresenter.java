package org.smartregister.chw.presenter;

import android.app.Activity;

import org.smartregister.chw.activity.AboveFiveChildProfileActivity;
import org.smartregister.chw.core.contract.CoreChildProfileContract;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.util.Utils;

import java.lang.ref.WeakReference;
import java.util.List;

public class AboveFiveChildProfilePresenter extends ChildProfilePresenter {

    private List<ReferralTypeModel> referralTypeModels;

    public AboveFiveChildProfilePresenter(CoreChildProfileContract.View childView, CoreChildProfileContract.Model model, String childBaseEntityId) {
        super(childView, model, childBaseEntityId);
        setView(new WeakReference<>(childView));
        setInteractor(new ChildProfileInteractor());
        getInteractor().setChildBaseEntityId(childBaseEntityId);
        setModel(model);
    }

    public void referToFacility() {
        referralTypeModels = ((AboveFiveChildProfileActivity) getView()).getReferralTypeModels();
        if (referralTypeModels.size() == 1) {
            startSickChildReferralForm();
        } else {
            Utils.launchClientReferralActivity((Activity) getView(), referralTypeModels, childBaseEntityId);
        }
    }

}
