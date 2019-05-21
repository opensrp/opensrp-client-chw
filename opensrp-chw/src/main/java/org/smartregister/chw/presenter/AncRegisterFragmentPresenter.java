package org.smartregister.chw.presenter;

import org.smartregister.chw.R;
import org.smartregister.chw.anc.contract.AncRegisterFragmentContract;
import org.smartregister.chw.anc.presenter.BaseAncRegisterFragmentPresenter;

public class AncRegisterFragmentPresenter extends BaseAncRegisterFragmentPresenter {
    public AncRegisterFragmentPresenter(AncRegisterFragmentContract.View view, AncRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
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
