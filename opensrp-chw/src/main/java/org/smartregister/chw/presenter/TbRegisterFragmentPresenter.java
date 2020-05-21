package org.smartregister.chw.presenter;

import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.R;
import org.smartregister.chw.tb.contract.BaseTbRegisterFragmentContract;
import org.smartregister.chw.tb.presenter.BaseTbRegisterFragmentPresenter;
import org.smartregister.chw.tb.util.Constants.Tables;
import org.smartregister.chw.tb.util.DBConstants;
import org.smartregister.chw.util.Constants;

public class TbRegisterFragmentPresenter extends BaseTbRegisterFragmentPresenter {

    public TbRegisterFragmentPresenter(BaseTbRegisterFragmentContract.View view, BaseTbRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    @NotNull
    public String getMainCondition() {
        return " " + Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.Key.DATE_REMOVED + " is null " +
                "AND " + Tables.TB + "." + DBConstants.Key.IS_CLOSED + " = '0' ";

    }

    @Override
    @NotNull
    public String getDueFilterCondition() {
        return " " + Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.Key.DATE_REMOVED + " is null " +
                "AND " + Tables.TB + "." + DBConstants.Key.IS_CLOSED + " = '0' ";

    }

    @Override
    public void processViewConfigurations() {
        super.processViewConfigurations();
        if (getConfig().getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_name_or_id));
        }
    }

    @Override
    public String getMainTable() {
        return Tables.TB;
    }
}
