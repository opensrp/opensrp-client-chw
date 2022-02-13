package org.smartregister.chw.presenter;

import org.smartregister.chw.core.contract.CoreCertificationRegisterFragmentContract;
import org.smartregister.chw.core.presenter.CoreCertificationRegisterFragmentPresenter;
import org.smartregister.chw.core.utils.CoreConstants;

public class BirthCertificationRegisterFragmentPresenter extends CoreCertificationRegisterFragmentPresenter {

    public BirthCertificationRegisterFragmentPresenter(CoreCertificationRegisterFragmentContract.View view, CoreCertificationRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getDueCondition() {
        return " and " + CoreConstants.TABLE_NAME.CHILD + ".birth_cert = 'No'";
    }

    @Override
    public String getFilterString(String filters) {
        return super.getFilterString(filters);
    }

    @Override
    public String getDueFilterCondition() {
        return " and " + CoreConstants.TABLE_NAME.CHILD + ".birth_cert = 'No'";
    }

    @Override
    public String getMainCondition() {
        return super.getMainCondition();
    }

    @Override
    public String getMainCondition(String tableName) {
        return super.getMainCondition(tableName);
    }

    @Override
    public String getDefaultSortQuery() {
        return super.getDefaultSortQuery();
    }

    @Override
    public void initializeQueries(String mainCondition) {
        super.initializeQueries(mainCondition);
    }
}
