package org.smartgresiter.wcaro.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.RegisterFragmentContract;
import org.smartgresiter.wcaro.custom_view.NavigationMenu;
import org.smartgresiter.wcaro.model.FamilyRegisterFramentModel;
import org.smartgresiter.wcaro.presenter.FamilyRegisterFragmentPresenter;
import org.smartgresiter.wcaro.provider.WcaroRegisterProvider;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.Utils;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.fragment.BaseFamilyRegisterFragment;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.List;
import java.util.Set;

public class FamilyRegisterFragment extends BaseFamilyRegisterFragment {

    private View dueOnlyLayout;
    private View view;

    private boolean dueFilterActive = false;

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        this.view =view;

        Toolbar toolbar = view.findViewById(org.smartregister.R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);

        NavigationMenu.getInstance(getActivity(), null, toolbar);

        View navbarContainer = view.findViewById(R.id.register_nav_bar_container);
        navbarContainer.setFocusable(false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View searchBarLayout = view.findViewById(R.id.search_bar_layout);
        searchBarLayout.setLayoutParams(params);
        searchBarLayout.setBackgroundResource(R.color.wcaro_primary);
        searchBarLayout.setPadding(searchBarLayout.getPaddingLeft(), searchBarLayout.getPaddingTop(), searchBarLayout.getPaddingRight(), (int) Utils.convertDpToPixel(10, getActivity()));

        CustomFontTextView titleView = view.findViewById(R.id.txt_title_label);
        if (titleView != null) {
            titleView.setPadding(0, titleView.getTop(), titleView.getPaddingRight(), titleView.getPaddingBottom());
        }

        View topLeftLayout = view.findViewById(R.id.top_left_layout);
        topLeftLayout.setVisibility(View.GONE);

        View topRightLayout = view.findViewById(R.id.top_right_layout);
        topRightLayout.setVisibility(View.VISIBLE);

        View sortFilterBarLayout = view.findViewById(R.id.register_sort_filter_bar_layout);
        sortFilterBarLayout.setVisibility(View.GONE);

        View filterSortLayout = view.findViewById(R.id.filter_sort_layout);
        filterSortLayout.setVisibility(View.GONE);

        dueOnlyLayout = view.findViewById(R.id.due_only_layout);
        dueOnlyLayout.setVisibility(View.VISIBLE);
        dueOnlyLayout.setOnClickListener(registerActionHandler);

        if (getSearchView() != null) {
            getSearchView().setBackgroundResource(org.smartregister.family.R.color.white);
            getSearchView().setCompoundDrawablesWithIntrinsicBounds(org.smartregister.family.R.drawable.ic_action_search, 0, 0, 0);
            getSearchView().setTextColor(getResources().getColor(R.color.text_black));
        }

    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new FamilyRegisterFragmentPresenter(this, new FamilyRegisterFramentModel(), viewConfigurationIdentifier);
    }

    //TODO need to do only first time when all data sync
    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        super.onSyncComplete(fetchStatus);
        //if(fetchStatus.displayValue().equalsIgnoreCase(FetchStatus.fetched.displayValue())){
        //UpdateVisitServiceJob.scheduleJobImmediately(UpdateVisitServiceJob.TAG);
        //}
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        WcaroRegisterProvider wcaroRegisterProvider = new WcaroRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, wcaroRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected String getMainCondition() {
        return presenter().getMainCondition();
    }


    @Override
    protected String getDefaultSortQuery() {
        return presenter().getDefaultSortQuery();
    }

    @Override
    protected void startRegistration() {
//        ((BaseFamilyRegisterActivity) getActivity()).startFormActivity(Utils.metadata().familyRegister.formName, null, null);
    }

    @Override
    public void filter(String filterString, String joinTableString, String mainConditionString, boolean qrCode) {
        this.joinTables = new String[]{Constants.TABLE_NAME.FAMILY_MEMBER};
        super.filter(filterString, joinTableString, mainConditionString, qrCode);
    }

    private void dueFilter(String mainConditionString) {
        this.joinTables = null;
        super.filter("", "", mainConditionString, false);
    }

    @Override
    protected void onViewClicked(View view) {
        super.onViewClicked(view);

        switch (view.getId()) {
            case R.id.due_only_layout:
                toggleFilterSelection(view);
        }
    }

    private void toggleFilterSelection(View dueOnlyLayout) {
        if (dueOnlyLayout != null) {
            String tagString = "PRESSED";
            if (dueOnlyLayout.getTag() == null) {
                dueFilterActive = true;
                dueFilter(presenter().getDueFilterCondition());
                dueOnlyLayout.setTag(tagString);
                switchViews(dueOnlyLayout, true);
            } else if (dueOnlyLayout.getTag().toString().equals(tagString)) {
                dueFilterActive = false;
                filter("", "", presenter().getMainCondition(), false);
                dueOnlyLayout.setTag(null);
                switchViews(dueOnlyLayout, false);
            }
        }
    }

    private void switchViews(View dueOnlyLayout, boolean isPress) {
        TextView dueOnlyTextView = dueOnlyLayout.findViewById(R.id.due_only_text_view);
        if (isPress) {
            dueOnlyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_due_filter_on, 0);
        } else {
            dueOnlyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_due_filter_off, 0);

        }
    }

    private String dueFilterAndSortQuery() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);

        String query = "";
        try {
            if (isValidFilterForFts(commonRepository())) {
                String sql = sqb
                        .searchQueryFts(tablename, joinTable, mainCondition, filters, Sortqueries,
                                clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset());
                sql = sql.replace(CommonFtsObject.idColumn, CommonFtsObject.relationalIdColumn);
                sql = sql.replace(CommonFtsObject.searchTableName(Constants.TABLE_NAME.FAMILY), CommonFtsObject.searchTableName(Constants.TABLE_NAME.CHILD));
                List<String> ids = commonRepository().findSearchIds(sql);
                query = sqb.toStringFts(ids, tablename, CommonRepository.ID_COLUMN,
                        Sortqueries);
                query = sqb.Endquery(query);
            } else {
                sqb.addCondition(filters);
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(sqb.addlimitandOffset(query, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset()));

            }
        } catch (Exception e) {
            Log.e(getClass().getName(), e.toString(), e);
        }

        return query;
    }

    @Override
    public void countExecute() {
        if (!dueFilterActive) {
            super.countExecute();
        } else {
            Cursor c = null;

            try {
                SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(countSelect);
                String query = "";
                if (isValidFilterForFts(commonRepository())) {
                    String sql = sqb.countQueryFts(tablename, joinTable, mainCondition, filters);
                    sql = sql.replace(CommonFtsObject.idColumn, CommonFtsObject.relationalIdColumn);
                    sql = sql.replace(CommonFtsObject.searchTableName(Constants.TABLE_NAME.FAMILY), CommonFtsObject.searchTableName(Constants.TABLE_NAME.CHILD));
                    sql = sql + " GROUP BY " + CommonFtsObject.relationalIdColumn;
                    Log.i(getClass().getName(), query);

                    clientAdapter.setTotalcount(commonRepository().countSearchIds(sql));
                    Log.v("total count here", "" + clientAdapter.getTotalcount());


                } else {
                    sqb.addCondition(filters);
                    query = sqb.orderbyCondition(Sortqueries);
                    query = sqb.Endquery(query);

                    Log.i(getClass().getName(), query);
                    c = commonRepository().rawCustomQueryForAdapter(query);
                    c.moveToFirst();
                    clientAdapter.setTotalcount(c.getInt(0));
                    Log.v("total count here", "" + clientAdapter.getTotalcount());
                }

                clientAdapter.setCurrentlimit(20);
                clientAdapter.setCurrentoffset(0);


            } catch (Exception e) {
                Log.e(getClass().getName(), e.toString(), e);
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        if (!dueFilterActive) {
            return super.onCreateLoader(id, args);
        } else {
            switch (id) {
                case LOADER_ID:
                    // Returns a new CursorLoader
                    return new CursorLoader(getActivity()) {
                        @Override
                        public Cursor loadInBackground() {
                            // Count query
                            final String COUNT = "count_execute";
                            if (args != null && args.getBoolean(COUNT)) {
                                countExecute();
                            }
                            String query = dueFilterAndSortQuery();
                            return commonRepository().rawCustomQueryForAdapter(query);
                        }
                    };
                default:
                    // An invalid id was passed in
                    return null;
            }
        }
    }

    @Override
    public RegisterFragmentContract.Presenter presenter() {
        return (RegisterFragmentContract.Presenter) presenter;
    }

    @Override
    public void onResume() {
        super.onResume();

        Toolbar toolbar = view.findViewById(org.smartregister.R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);
        NavigationMenu.getInstance(getActivity(), null, toolbar);
    }
}
