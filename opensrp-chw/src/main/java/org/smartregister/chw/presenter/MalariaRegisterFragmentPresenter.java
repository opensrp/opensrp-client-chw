package org.smartregister.chw.presenter;

import org.smartregister.chw.core.presenter.CoreMalariaRegisterFragmentPresenter;
import org.smartregister.chw.malaria.contract.MalariaRegisterFragmentContract;

public class MalariaRegisterFragmentPresenter extends CoreMalariaRegisterFragmentPresenter {

    public MalariaRegisterFragmentPresenter(MalariaRegisterFragmentContract.View view,
                                            MalariaRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    public String getMainCondition() {
        return " ec_family_member.date_removed is null AND datetime('NOW') <= datetime(ec_malaria_confirmation.last_interacted_with/1000, 'unixepoch', 'localtime','+15 days') AND ec_malaria_confirmation.is_closed = 0";
    }

}
