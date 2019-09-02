package org.smartregister.brac.hnpp.presenter;

import org.smartregister.brac.hnpp.R;
import org.smartregister.chw.core.presenter.FamilyRegisterFragmentPresenter;
import org.smartregister.family.contract.FamilyRegisterFragmentContract;

public class HnppFamilyRegisterFragmentPresenter extends FamilyRegisterFragmentPresenter {
    public HnppFamilyRegisterFragmentPresenter(FamilyRegisterFragmentContract.View view, FamilyRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }
    @Override
    public void processViewConfigurations() {
        super.processViewConfigurations();
        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_name_or_id));
        }
    }
}
