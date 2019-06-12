package org.smartregister.chw.presenter;

import org.smartregister.chw.malaria.contract.MalariaRegisterFragmentContract;
import org.smartregister.chw.malaria.presenter.BaseMalariaRegisterFragmentPresenter;
import org.smartregister.chw.util.Constants;

public class MalariaRegisterFragmentPresenter extends BaseMalariaRegisterFragmentPresenter {

    public MalariaRegisterFragmentPresenter(MalariaRegisterFragmentContract.View view, MalariaRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getMainTable() {
        return Constants.TABLE_NAME.MALARIA_CONFIRMATION;
    }

}
