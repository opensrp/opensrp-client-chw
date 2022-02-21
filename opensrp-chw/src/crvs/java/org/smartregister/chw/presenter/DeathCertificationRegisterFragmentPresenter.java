package org.smartregister.chw.presenter;

import org.smartregister.chw.core.contract.CoreCertificationRegisterFragmentContract;
import org.smartregister.chw.core.contract.CoreChildRegisterFragmentContract;
import org.smartregister.chw.core.presenter.CoreCertificationRegisterFragmentPresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.model.DeathCertificationRegisterFragmentModel;
import org.smartregister.family.util.DBConstants;

public class DeathCertificationRegisterFragmentPresenter extends CoreCertificationRegisterFragmentPresenter {

    private CoreCertificationRegisterFragmentContract.Model model;

    public DeathCertificationRegisterFragmentPresenter(CoreCertificationRegisterFragmentContract.View view, CoreChildRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
        this.model = model;
    }

    @Override
    public String getDueCondition() {
        return " and received_death_certificate = 'Yes'";
    }

    @Override
    public String getFilterString(String filters) {
        return ""; // Defined in Model
    }

    @Override
    public String getDueFilterCondition() {
        return " and received_death_certificate = 'Yes'";
    }

    @Override
    public String getMainCondition() {
        return ""; // Defined in Model
    }

    @Override
    public String getMainCondition(String tableName) {
        return "";
    }


    public String getOutOfCatchmentSortQueries() {
        return DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ";
    }

    @Override
    public String getDefaultSortQuery() {
        return ""; //super.getDefaultSortQuery();
    }

    @Override
    public void initializeQueries(String mainCondition) {
        String countSelect = getCountSelectString(mainCondition);
        String mainSelect = getMainSelectString(mainCondition);

        getView().initializeQueryParams(CoreConstants.TABLE_NAME.FAMILY_MEMBER, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);

        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

    @Override
    public String getCountSelectString(String condition) {
        return model.countSelect("", condition, CoreConstants.TABLE_NAME.FAMILY_MEMBER);
    }

    @Override
    public String getMainSelectString(String condition) {
        return model.mainSelect("", CoreConstants.TABLE_NAME.FAMILY, CoreConstants.TABLE_NAME.FAMILY_MEMBER, condition);
    }

    public String getCustomSelectString(String condition, String filters, String SortQueries, boolean isDueActive) {
        return ((DeathCertificationRegisterFragmentModel) model).getCustomSelectString(condition, filters, SortQueries, isDueActive);
    }
}
