package org.smartregister.chw.hf.presenter;

import org.smartregister.chw.anc.contract.BaseAncRegisterFragmentContract;
import org.smartregister.chw.pnc.presenter.BasePncRegisterFragmentPresenter;

public class PncRegisterFragmentPresenter extends BasePncRegisterFragmentPresenter {
    public PncRegisterFragmentPresenter(BaseAncRegisterFragmentContract.View view, BaseAncRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public void processViewConfigurations() {
        super.processViewConfigurations();
        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(org.smartregister.chw.core.R.string.search_name_or_id));
        }
    }
}
