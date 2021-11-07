package org.smartregister.chw.presenter;

import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.R;
import org.smartregister.chw.hiv.contract.BaseHivRegisterFragmentContract;
import org.smartregister.chw.hiv.presenter.BaseHivIndexContactsRegisterFragmentPresenter;
import org.smartregister.chw.hiv.util.Constants.Tables;
import org.smartregister.chw.hiv.util.DBConstants;

public class HivIndexContactsContactsRegisterFragmentPresenter extends BaseHivIndexContactsRegisterFragmentPresenter {

    public HivIndexContactsContactsRegisterFragmentPresenter(BaseHivRegisterFragmentContract.View view, BaseHivRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    @NotNull
    public String getMainCondition() {
        return " " +
                Tables.HIV_INDEX + "." + DBConstants.Key.TEST_RESULTS + " IS NULL AND " +
                Tables.HIV_INDEX + "." + DBConstants.Key.REFER_TO_CHW + " = 'Yes'  AND " +
                Tables.HIV_INDEX + "." + DBConstants.Key.HOW_TO_NOTIFY_CONTACT_CLIENT + " <> 'na'";

    }

    @Override
    @NotNull
    public String getDueFilterCondition() {
        return " " +
                Tables.HIV_INDEX + "." + DBConstants.Key.FOLLOWED_UP_BY_CHW + " IS NULL";
    }

    @Override
    public void processViewConfigurations() {
        super.processViewConfigurations();
        if (getConfig().getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_name_or_id));
        }


        if (getConfig().getFilterFields() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_name_or_id));
        }
    }

    @Override
    public String getMainTable() {
        return Tables.HIV_INDEX;
    }
}
