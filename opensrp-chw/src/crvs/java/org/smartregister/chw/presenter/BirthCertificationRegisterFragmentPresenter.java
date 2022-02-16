package org.smartregister.chw.presenter;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.core.contract.CoreCertificationRegisterFragmentContract;
import org.smartregister.chw.core.presenter.CoreCertificationRegisterFragmentPresenter;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.model.BirthCertificationRegisterFragmentModel;
import org.smartregister.family.util.DBConstants;

import java.text.MessageFormat;

public class BirthCertificationRegisterFragmentPresenter extends CoreCertificationRegisterFragmentPresenter {

    private CoreCertificationRegisterFragmentContract.Model model;

    public BirthCertificationRegisterFragmentPresenter(CoreCertificationRegisterFragmentContract.View view, CoreCertificationRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
        this.model = model;
    }

    @Override
    public String getDueCondition() {
        return " and birth_cert = 'Yes'";
    }

    @Override
    public String getFilterString(String filters) {
        StringBuilder customFilter = new StringBuilder();
        if (StringUtils.isNotBlank(filters)) {
            customFilter.append(" and ( ");
            customFilter.append(MessageFormat.format(" {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, org.smartregister.chw.anc.util.DBConstants.KEY.FIRST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, org.smartregister.chw.anc.util.DBConstants.KEY.LAST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, org.smartregister.chw.anc.util.DBConstants.KEY.MIDDLE_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, org.smartregister.chw.anc.util.DBConstants.KEY.UNIQUE_ID, filters));

            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.CHILD, org.smartregister.chw.anc.util.DBConstants.KEY.FIRST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.CHILD, org.smartregister.chw.anc.util.DBConstants.KEY.LAST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.CHILD, org.smartregister.chw.anc.util.DBConstants.KEY.MIDDLE_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.CHILD, org.smartregister.chw.anc.util.DBConstants.KEY.UNIQUE_ID, filters));

            customFilter.append(" ) ");
        }

        return customFilter.toString();
    }

    @Override
    public String getOutOfCatchmentFilterString(String filters) {
        StringBuilder customFilter = new StringBuilder();
        if (StringUtils.isNotBlank(filters)) {
            customFilter.append(" and ( ");
            customFilter.append(MessageFormat.format(" {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.EC_OUT_OF_AREA_CHILD, org.smartregister.chw.anc.util.DBConstants.KEY.FIRST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.EC_OUT_OF_AREA_CHILD, org.smartregister.chw.anc.util.DBConstants.KEY.MIDDLE_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.EC_OUT_OF_AREA_CHILD, org.smartregister.chw.anc.util.DBConstants.KEY.UNIQUE_ID, filters));

            customFilter.append(" ) ");
        }

        return customFilter.toString();
    }

    @Override
    public String getDueFilterCondition() {
        return " and birth_cert = 'Yes'";
    }

    @Override
    public String getMainCondition() {
        return super.getMainCondition();
    }

    @Override
    public String getMainCondition(String tableName) {
        return super.getMainCondition(tableName);
    }

    public String getOutOfCatchmentMainCondition() {
        return String.format(" %s", ChildDBConstants.outOfCatchmentChildAgeLimitFilter());
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
        String countSelect = getCountSelectString(mainCondition) + getOutOfCatchmentSelectString(mainCondition);
        String mainSelect = getMainSelectString(mainCondition) + getOutOfCatchmentSelectString(mainCondition);

        getView().initializeQueryParams(CoreConstants.TABLE_NAME.CHILD, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);

        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

    @Override
    public String getCountSelectString(String condition) {
        return model.countSelect(CoreConstants.TABLE_NAME.CHILD, condition, CoreConstants.TABLE_NAME.FAMILY_MEMBER);
    }

    @Override
    public String getMainSelectString(String condition) {
        return model.mainSelect(CoreConstants.TABLE_NAME.CHILD, CoreConstants.TABLE_NAME.FAMILY, CoreConstants.TABLE_NAME.FAMILY_MEMBER, condition);
    }

    @Override
    public String getOutOfCatchmentSelectString(String condition) {
        return ((BirthCertificationRegisterFragmentModel) model).outOfAreaSelect(condition);
    }
}
