package org.smartregister.chw.core.presenter;

import org.smartregister.chw.anc.contract.BaseAncRegisterFragmentContract;
import org.smartregister.chw.anc.presenter.BaseAncRegisterFragmentPresenter;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.utils.CoreConstants;

public class AncRegisterFragmentPresenter extends BaseAncRegisterFragmentPresenter {
    public AncRegisterFragmentPresenter(BaseAncRegisterFragmentContract.View view, BaseAncRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getMainCondition() {
        return " " + CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DATE_REMOVED + " is null " +
                "AND " + CoreConstants.TABLE_NAME.ANC_MEMBER + "." + DBConstants.KEY.IS_CLOSED + " is 0 ";
    }

    @Override
    public String getMainTable() {
        return CoreConstants.TABLE_NAME.ANC_MEMBER;
    }

    @Override
    public void processViewConfigurations() {
        super.processViewConfigurations();
        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_name_or_id));
        }
    }
}
