package org.smartregister.chw.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.activity.PathfinderFamilyPlanningFollowUpVisitActivity;
import org.smartregister.chw.activity.PathfinderFamilyPlanningMemberProfileActivity;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.QueryGenerator;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.fp_pathfinder.dao.FpDao;
import org.smartregister.chw.fp_pathfinder.fragment.BaseFpRegisterFragment;
import org.smartregister.chw.fp_pathfinder.util.FamilyPlanningConstants;
import org.smartregister.chw.model.PathfinderFamilyPlanningRegisterFragmentModel;
import org.smartregister.chw.presenter.PathfinderFamilyPlanningRegisterFragmentPresenter;
import org.smartregister.chw.provider.PathfinderFpProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

public class PathfinderFamilyPlanningRegisterFragment extends BaseFpRegisterFragment {

    private static final String DUE_FILTER_TAG = "PRESSED";
    private View view;
    private View dueOnlyLayout;
    private boolean dueFilterActive = false;

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new PathfinderFamilyPlanningRegisterFragmentPresenter(this, new PathfinderFamilyPlanningRegisterFragmentModel());
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {
        PathfinderFamilyPlanningMemberProfileActivity.startFpMemberProfileActivity(getActivity(), FpDao.getMember(client.getCaseId()));
    }

    @Override
    protected void openFollowUpVisit(CommonPersonObjectClient client) {
        PathfinderFamilyPlanningFollowUpVisitActivity.startMe(getActivity(), FpDao.getMember(client.getCaseId()), false);
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        PathfinderFpProvider fpRegisterProvider = new PathfinderFpProvider(getActivity(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, fpRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        this.view = view;

        Toolbar toolbar = view.findViewById(org.smartregister.chw.core.R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);

        NavigationMenu.getInstance(getActivity(), null, toolbar);

        View navbarContainer = view.findViewById(org.smartregister.chw.core.R.id.register_nav_bar_container);
        navbarContainer.setFocusable(false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View searchBarLayout = view.findViewById(org.smartregister.chw.core.R.id.search_bar_layout);
        searchBarLayout.setLayoutParams(params);
        searchBarLayout.setBackgroundResource(org.smartregister.chw.core.R.color.chw_primary);
        searchBarLayout.setPadding(searchBarLayout.getPaddingLeft(), searchBarLayout.getPaddingTop(), searchBarLayout.getPaddingRight(), (int) Utils.convertDpToPixel(10, getActivity()));

        CustomFontTextView titleView = view.findViewById(org.smartregister.chw.core.R.id.txt_title_label);
        if (titleView != null) {
            titleView.setPadding(0, titleView.getTop(), titleView.getPaddingRight(), titleView.getPaddingBottom());
        }

        View topLeftLayout = view.findViewById(org.smartregister.chw.core.R.id.top_left_layout);
        topLeftLayout.setVisibility(View.GONE);

        View topRightLayout = view.findViewById(org.smartregister.chw.core.R.id.top_right_layout);
        topRightLayout.setVisibility(View.VISIBLE);

        View sortFilterBarLayout = view.findViewById(org.smartregister.chw.core.R.id.register_sort_filter_bar_layout);
        sortFilterBarLayout.setVisibility(View.GONE);

        View filterSortLayout = view.findViewById(org.smartregister.chw.core.R.id.filter_sort_layout);
        filterSortLayout.setVisibility(View.GONE);

        dueOnlyLayout = view.findViewById(org.smartregister.chw.core.R.id.due_only_layout);
        dueOnlyLayout.setVisibility(View.VISIBLE);
        dueOnlyLayout.setOnClickListener(registerActionHandler);

        if (getSearchView() != null) {
            getSearchView().setBackgroundResource(org.smartregister.family.R.color.white);
            getSearchView().setCompoundDrawablesWithIntrinsicBounds(org.smartregister.family.R.drawable.ic_action_search, 0, 0, 0);
            getSearchView().setTextColor(getResources().getColor(org.smartregister.chw.core.R.color.text_black));
        }
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        //TODO
        //Log.d(TAG, "setAdvancedSearchFormData unimplemented");
    }

    @Override
    protected void onViewClicked(View view) {
        super.onViewClicked(view);

        if (view.getId() == org.smartregister.chw.core.R.id.due_only_layout) {
            toggleFilterSelection(view);
        }
    }

    protected void toggleFilterSelection(View dueOnlyLayout) {
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

    protected String searchText() {
        return (getSearchView() == null) ? "" : getSearchView().getText().toString();
    }

    private void switchViews(View dueOnlyLayout, boolean isPress) {
        TextView dueOnlyTextView = dueOnlyLayout.findViewById(org.smartregister.chw.core.R.id.due_only_text_view);
        if (isPress) {
            dueOnlyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, org.smartregister.chw.core.R.drawable.ic_due_filter_on, 0);
        } else {
            dueOnlyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, org.smartregister.chw.core.R.drawable.ic_due_filter_off, 0);
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
    public void onResume() {
        super.onResume();

        Toolbar toolbar = view.findViewById(org.smartregister.chw.core.R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);
        NavigationMenu.getInstance(getActivity(), null, toolbar);
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

    @Nullable
    private String defaultFilterAndSortQuery() {
        try {
            QueryGenerator generator = new QueryGenerator()
                    .withMainSelect(mainSelect)
                    .withWhereClause(presenter().getMainCondition())
                    .withSortColumn(Sortqueries)
                    .withLimitClause(clientAdapter.getCurrentoffset(), clientAdapter.getCurrentlimit());

            if (dueFilterActive)
                generator.withWhereClause(getDueCondition());

            if (StringUtils.isNotBlank(filters))
                generator.withWhereClause(getSearchFilter(filters));

            return generator.generateQuery();
        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }

    private String getSearchFilter(String search) {
        return MessageFormat.format(" {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.FIRST_NAME, search) +
                MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.LAST_NAME, search) +
                MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.MIDDLE_NAME, search) +
                MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.UNIQUE_ID, search);
    }

    @Override
    public void countExecute() {
        Cursor cursor = null;
        try {
            String mainTable = presenter().getMainTable();

            QueryGenerator generator = new QueryGenerator()
                    .withMainTable(mainTable)
                    .withColumn("count(*)")
                    .withJoinClause("INNER JOIN " + CoreConstants.TABLE_NAME.FAMILY_MEMBER + " ON "
                            + mainTable + "." + DBConstants.KEY.BASE_ENTITY_ID + " = "
                            + CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.BASE_ENTITY_ID)

                    .withWhereClause(presenter().getMainCondition());

            if (dueFilterActive)
                generator.withWhereClause(getDueCondition());

            if (StringUtils.isNotBlank(filters))
                generator.withWhereClause(getSearchFilter(filters));

            cursor = commonRepository().rawCustomQueryForAdapter(generator.generateQuery());
            cursor.moveToFirst();
            clientAdapter.setTotalcount(cursor.getInt(0));
            Timber.v("total count here %d", clientAdapter.getTotalcount());

            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
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

                    String query = defaultFilterAndSortQuery();
                    return commonRepository().rawCustomQueryForAdapter(query);
                }
            };
        }
        return super.onCreateLoader(id, args);
    }

    public String getDueCondition() {
        return FamilyPlanningConstants.DBConstants.FAMILY_PLANNING_TABLE + ".base_entity_id in (select base_entity_id from schedule_service where strftime('%Y-%m-%d') BETWEEN due_date and ifnull(expiry_date,strftime('%Y-%m-%d')) and schedule_name = '" + CoreConstants.SCHEDULE_TYPES.FP_VISIT + "' and ifnull(not_done_date,'') = '' and ifnull(completion_date,'') = '' )  ";
    }

    protected void dueFilter(View dueOnlyLayout) {
        filterDue(searchText(), "", presenter().getDueFilterCondition());
        dueOnlyLayout.setTag(DUE_FILTER_TAG);
        switchViews(dueOnlyLayout, true);
    }

    protected void normalFilter(View dueOnlyLayout) {
        filterDue(searchText(), "", presenter().getMainCondition());
        dueOnlyLayout.setTag(null);
        switchViews(dueOnlyLayout, false);
    }

    protected void filterDue(String filterString, String joinTableString, String mainConditionString) {
        filters = filterString;
        joinTable = joinTableString;
        mainCondition = mainConditionString;
        filterandSortExecute(countBundle());
    }


}


