package org.smartregister.chw.presenter;

import org.smartregister.chw.R;
import org.smartregister.chw.malaria.contract.MalariaRegisterFragmentContract;
import org.smartregister.chw.malaria.presenter.BaseMalariaRegisterFragmentPresenter;
import org.smartregister.chw.malaria.util.DBConstants;
import org.smartregister.chw.util.Constants;

public class MalariaRegisterFragmentPresenter extends BaseMalariaRegisterFragmentPresenter {

    public MalariaRegisterFragmentPresenter(MalariaRegisterFragmentContract.View view, MalariaRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getMainCondition() {
        return " " + Constants.TABLE_NAME.FAMILY_MEMBER + "." + org.smartregister.chw.malaria.util.DBConstants.KEY.DATE_REMOVED + " is null " +
                "AND " + Constants.TABLE_NAME.MALARIA_CONFIRMATION + "." + DBConstants.KEY.MALARIA + " = 1 ";
    }

    @Override
    public void processViewConfigurations() {
        super.processViewConfigurations();
        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_name_or_id));
        }
    }

    @Override
    public String getMainTable() {
        return Constants.TABLE_NAME.MALARIA_CONFIRMATION;
    }
}
