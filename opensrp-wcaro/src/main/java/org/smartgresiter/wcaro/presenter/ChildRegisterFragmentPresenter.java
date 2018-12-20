package org.smartgresiter.wcaro.presenter;

import org.apache.commons.lang3.StringUtils;
import org.smartgresiter.wcaro.contract.ChildRegisterFragmentContract;
import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.family.util.DBConstants;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ChildRegisterFragmentPresenter implements ChildRegisterFragmentContract.Presenter {


    protected Set<View> visibleColumns = new TreeSet<>();
    private WeakReference<ChildRegisterFragmentContract.View> viewReference;
    private ChildRegisterFragmentContract.Model model;
    private RegisterConfiguration config;
    private String viewConfigurationIdentifier;

    public ChildRegisterFragmentPresenter(ChildRegisterFragmentContract.View view, ChildRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
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
            setVisibleColumns(model.getRegisterActiveColumns(viewConfigurationIdentifier));
        }

        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(config.getSearchBarText());
        }
    }

    @Override
    public void initializeQueries(String mainCondition) {

        String countSelect = model.countSelect(ChildDBConstants.KEY.TABLE_NAME, mainCondition);
        String mainSelect = model.mainSelect(ChildDBConstants.KEY.TABLE_NAME, ChildDBConstants.KEY.PARENT_TABLE_NAME, mainCondition);

        getView().initializeQueryParams(ChildDBConstants.KEY.TABLE_NAME, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);

        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

    @Override
    public void startSync() {
        //ServiceTools.startSyncService(getActivity());
    }

    @Override
    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        String filterText = model.getFilterText(filterList, getView().getString(org.smartregister.R.string.filter));
        String sortText = model.getSortText(sortField);

        getView().updateFilterAndFilterStatus(filterText, sortText);
    }

    @Override
    public void searchGlobally(String uniqueId) {
        // TODO implement search global
    }

    protected ChildRegisterFragmentContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    private void setVisibleColumns(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        this.visibleColumns = visibleColumns;
    }

    public void setModel(ChildRegisterFragmentContract.Model model) {
        this.model = model;
    }

    @Override
    public String getMainCondition() {
        return "";
    }

    @Override
    public String getDefaultSortQuery() {
        return ChildDBConstants.KEY.TABLE_NAME + "." + DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ";
    }
}
