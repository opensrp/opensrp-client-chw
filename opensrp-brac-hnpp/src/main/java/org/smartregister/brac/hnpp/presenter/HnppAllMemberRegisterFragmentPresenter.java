package org.smartregister.brac.hnpp.presenter;

import org.smartregister.brac.hnpp.model.HnppAllMemberRegisterFragmentModel;
import org.smartregister.chw.core.contract.CoreChildRegisterFragmentContract;
import org.smartregister.chw.core.presenter.CoreChildRegisterFragmentPresenter;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.family.util.DBConstants;

import java.util.List;

public class HnppAllMemberRegisterFragmentPresenter extends CoreChildRegisterFragmentPresenter {

    private HnppAllMemberRegisterFragmentModel model;

    public HnppAllMemberRegisterFragmentPresenter(CoreChildRegisterFragmentContract.View view, CoreChildRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
        this.model = (HnppAllMemberRegisterFragmentModel)model;
    }

    @Override
    public void initializeQueries(String mainCondition) {
        String countSelect = model.countSelect(CoreConstants.TABLE_NAME.FAMILY_MEMBER, mainCondition);
        String mainSelect = model.mainSelect(null,CoreConstants.TABLE_NAME.FAMILY, CoreConstants.TABLE_NAME.FAMILY_MEMBER, mainCondition);
        getView().initializeQueryParams(CoreConstants.TABLE_NAME.FAMILY_MEMBER, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);

        getView().countExecute();

        getView().filterandSortInInitializeQueries();
    }
    @Override
    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        String filterText = model.getFilterText(filterList, getView().getString(org.smartregister.R.string.filter));
        String sortText = model.getSortText(sortField);

        getView().updateFilterAndFilterStatus(filterText, sortText);
    }

    @Override
    public String getMainCondition() {
        return String.format(" %s is null ", DBConstants.KEY.DATE_REMOVED);
    }

    @Override
    public String getMainCondition(String tableName) {
        return String.format(" %s is null ", tableName + "." + DBConstants.KEY.DATE_REMOVED);
    }
}
