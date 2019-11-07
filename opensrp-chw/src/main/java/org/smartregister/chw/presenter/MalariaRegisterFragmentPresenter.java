package org.smartregister.chw.presenter;

import org.smartregister.chw.core.presenter.CoreMalariaRegisterFragmentPresenter;
import org.smartregister.chw.malaria.contract.MalariaRegisterFragmentContract;

public class MalariaRegisterFragmentPresenter extends CoreMalariaRegisterFragmentPresenter {

    public MalariaRegisterFragmentPresenter(MalariaRegisterFragmentContract.View view,
                                            MalariaRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

}
