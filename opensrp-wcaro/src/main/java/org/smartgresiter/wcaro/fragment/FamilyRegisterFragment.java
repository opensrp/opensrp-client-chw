package org.smartgresiter.wcaro.fragment;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.RegisterFragmentContract;
import org.smartgresiter.wcaro.custom_view.NavigationMenu;
import org.smartgresiter.wcaro.job.UpdateVisitServiceJob;
import org.smartgresiter.wcaro.model.FamilyRegisterFramentModel;
import org.smartgresiter.wcaro.presenter.FamilyRegisterFragmentPresenter;
import org.smartgresiter.wcaro.provider.WcaroRegisterProvider;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.Utils;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.fragment.BaseFamilyRegisterFragment;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.Set;

public class FamilyRegisterFragment extends BaseFamilyRegisterFragment {

    private View dueOnlyLayout;

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

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
         UpdateVisitServiceJob.scheduleJobImmediately(UpdateVisitServiceJob.TAG);
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
                TextView dueOnlyTextView = dueOnlyLayout.findViewById(R.id.due_only_text_view);
                Drawable[] drawables = dueOnlyTextView.getCompoundDrawables();
                Drawable rightDrawable = drawables[2];
                if (rightDrawable != null) {
                    if (Utils.areDrawablesIdentical(rightDrawable, getResources().getDrawable(R.drawable.ic_due_filter_off))) {
                        dueFilter(presenter().getDueFilterCondition());
                        dueOnlyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_due_filter_on, 0);
                    } else {
                        filter("", "", presenter().getMainCondition(), false);
                        dueOnlyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_due_filter_off, 0);
                    }
                }
        }
    }

    @Override
    public RegisterFragmentContract.Presenter presenter() {
        return (RegisterFragmentContract.Presenter) presenter;
    }
}
