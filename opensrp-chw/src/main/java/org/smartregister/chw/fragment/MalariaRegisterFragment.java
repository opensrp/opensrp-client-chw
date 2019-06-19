package org.smartregister.chw.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.custom_view.NavigationMenu;
import org.smartregister.chw.malaria.fragment.BaseMalariaRegisterFragment;
import org.smartregister.chw.model.MalariaRegisterFragmentModel;
import org.smartregister.chw.presenter.MalariaRegisterFragmentPresenter;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.QueryBuilder;
import org.smartregister.chw.util.Utils;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;

public class MalariaRegisterFragment extends BaseMalariaRegisterFragment {

    private View view;
    private View dueOnlyLayout;

    private boolean dueFilterActive = false;
    private static final String DUE_FILTER_TAG = "PRESSED";

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        this.view = view;

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
        searchBarLayout.setBackgroundResource(R.color.chw_primary);
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
    protected void onResumption() {
        if (dueFilterActive && dueOnlyLayout != null) {
            dueFilter(dueOnlyLayout);
        } else {
            super.onResumption();
        }
    }

    @Override
    protected void onViewClicked(View view) {
        super.onViewClicked(view);

        switch (view.getId()) {
            case R.id.due_only_layout:
                toggleFilterSelection(view);
                break;
            default:
                break;
        }
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new MalariaRegisterFragmentPresenter(this, new MalariaRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    public void filter(String filterString, String joinTableString, String mainConditionString, boolean qrCode) {
        this.joinTables = new String[]{Constants.TABLE_NAME.MALARIA_CONFIRMATION};
        super.filter(filterString, joinTableString, mainConditionString, qrCode);
    }

    private void toggleFilterSelection(View dueOnlyLayout) {
        if (dueOnlyLayout != null) {
            if (dueOnlyLayout.getTag() == null) {
                dueFilterActive = true;
                dueFilter(dueOnlyLayout);
            } else if (dueOnlyLayout.getTag().toString().equals(DUE_FILTER_TAG)) {
                dueFilterActive = false;
                normalFilter(dueOnlyLayout);
            }
        }
    }

    //due filter for the client
    private void dueFilter(View dueOnlyLayout) {
        filter(searchText(),"",presenter().getMainCondition(),false);
        dueOnlyLayout.setTag(DUE_FILTER_TAG);
        switchViews(dueOnlyLayout, true);
    }

    //normal search for the client
    private void normalFilter(View dueOnlyLayout) {
        filter(searchText(), "", presenter().getMainCondition(), false);
        dueOnlyLayout.setTag(null);
        switchViews(dueOnlyLayout, false);
    }

    private String searchText() {
        return (getSearchView() == null) ? "" : getSearchView().getText().toString();
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
                sql = sql.replace(CommonFtsObject.searchTableName(Constants.TABLE_NAME.MALARIA_CONFIRMATION), CommonFtsObject.searchTableName(Constants.TABLE_NAME.MALARIA_CONFIRMATION));
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

    private String defaultFilterAndSortQuery() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);

        String query = "";
        StringBuilder customFilter = new StringBuilder();
        if (StringUtils.isNotBlank(filters)) {
            customFilter.append(MessageFormat.format(" and ( {0}.{1} like ''%{2}%'' ", Constants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.FIRST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", Constants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.LAST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", Constants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.MIDDLE_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} = ''{2}'' ) ", Constants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.UNIQUE_ID, filters));

            if (dueFilterActive) {
                customFilter.append(MessageFormat.format(" and ( {0}) ", presenter().getDueFilterCondition()));
            }
        }
        try {
            if (isValidFilterForFts(commonRepository())) {

                String myquery = QueryBuilder.getQuery(joinTables, mainCondition, tablename, customFilter.toString(), clientAdapter, Sortqueries);
                List<String> ids = commonRepository().findSearchIds(myquery);
                query = sqb.toStringFts(ids, tablename, CommonRepository.ID_COLUMN,
                        Sortqueries);
                query = sqb.Endquery(query);
            } else {
                sqb.addCondition(customFilter.toString());
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
                    sql = sql.replace(CommonFtsObject.searchTableName(Constants.TABLE_NAME.MALARIA_CONFIRMATION), CommonFtsObject.searchTableName(Constants.TABLE_NAME.CHILD));
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
        if (id == LOADER_ID) {
            return new CursorLoader(getActivity()) {
                @Override
                public Cursor loadInBackground() {
                    // Count query
                    final String COUNT = "count_execute";
                    if (args != null && args.getBoolean(COUNT)) {
                        countExecute();
                    }
                    String query = (dueFilterActive ? dueFilterAndSortQuery() : defaultFilterAndSortQuery());
                    return commonRepository().rawCustomQueryForAdapter(query);
                }
            };
        }
        return super.onCreateLoader(id, args);
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

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        //TODO
        //Log.d(TAG, "setAdvancedSearchFormData unimplemented");
    }

    @Override
    protected void refreshSyncProgressSpinner() {
        if (syncProgressBar != null) {
            syncProgressBar.setVisibility(View.GONE);
        }
        if (syncButton != null) {
            syncButton.setVisibility(View.GONE);
        }
    }

}


