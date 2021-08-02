package org.smartregister.chw.presenter;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.core.contract.CoreChildRegisterFragmentContract;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.model.CoreDeadClientsFragmentModel;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.family.util.DBConstants;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CoreDeadClientsFragmentPresenter implements CoreChildRegisterFragmentContract.Presenter {

    protected Set<View> visibleColumns = new TreeSet<>();
    private WeakReference<CoreChildRegisterFragmentContract.View> viewReference;
    private CoreChildRegisterFragmentContract.Model model;
    private RegisterConfiguration config;
    private String viewConfigurationIdentifier;

    public CoreDeadClientsFragmentPresenter(CoreChildRegisterFragmentContract.View view, CoreDeadClientsFragmentModel model, String viewConfigurationIdentifier) {
        this.viewReference = new WeakReference<>(view);
        this.model = model;
        this.viewConfigurationIdentifier = viewConfigurationIdentifier;
        this.config = model.defaultRegisterConfiguration();
    }

    @Override
    public void processViewConfigurations() {
        if (StringUtils.isBlank(viewConfigurationIdentifier)) {
            return;
        }

        ViewConfiguration viewConfiguration = model.getViewConfiguration(viewConfigurationIdentifier);
        if (viewConfiguration != null) {
            config = (RegisterConfiguration) viewConfiguration.getMetadata();
            visibleColumns = model.getRegisterActiveColumns(viewConfigurationIdentifier);
        }

        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(org.smartregister.chw.core.R.string.search_name_or_id));
        }
    }

    @Override
    public void initializeQueries(String mainCondition) {
        String countSelect = model.countSelect(CoreConstants.TABLE_NAME.CHILD, mainCondition, CoreConstants.TABLE_NAME.FAMILY_MEMBER);
        String mainSelect = model.mainSelect(CoreConstants.TABLE_NAME.CHILD, CoreConstants.TABLE_NAME.FAMILY, CoreConstants.TABLE_NAME.FAMILY_MEMBER, mainCondition);

        getView().initializeQueryParams(CoreConstants.TABLE_NAME.CHILD, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);

        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

    @Override
    public void startSync() {
    }

    @Override
    public void searchGlobally(String uniqueId) {
        // TODO implement search global
    }

    protected CoreChildRegisterFragmentContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

    @Override
    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        String filterText = model.getFilterText(filterList, getView().getString(org.smartregister.R.string.filter));
        String sortText = model.getSortText(sortField);

        getView().updateFilterAndFilterStatus(filterText, sortText);
    }

    @Override
    public String getMainCondition() {
        return String.format(" %s.%s is null AND %s", CoreConstants.TABLE_NAME.CHILD, DBConstants.KEY.DATE_REMOVED, ChildDBConstants.childAgeLimitFilter());
    }

    @Override
    public String getMainCondition(String tableName) {
        return String.format(" %s is null AND %s", tableName + "." + DBConstants.KEY.DATE_REMOVED, ChildDBConstants.childAgeLimitFilter(tableName));
    }

    @Override
    public String getDefaultSortQuery() {
        return CoreConstants.TABLE_NAME.CHILD + "." + DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ";// AND "+ChildDBConstants.childAgeLimitFilter();
    }

    @Override
    public String getDueFilterCondition() {
        return getMainCondition() + " AND " + ChildDBConstants.childDueFilter();
    }

    public String getDueCondition(String check) {
        String dueCondition = "";
        switch (check){
            case "1":
                dueCondition =  " AND ec_child.received_death_certificate = 'Yes' ";
                break;
            case "2":
                dueCondition =  " AND ec_family_member.received_death_certificate = 'Yes' ";
                break;
            case "3":
                dueCondition =  " WHERE ec_out_of_area_death.received_death_certificate = 'Yes' ";
                break;
        }
        return dueCondition;
    }

    public void setModel(CoreChildRegisterFragmentContract.Model model) {
        this.model = model;
    }
}
