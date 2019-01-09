package org.smartgresiter.wcaro.fragment;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.custom_view.NavigationMenu;
import org.smartgresiter.wcaro.model.FamilyRegisterFramentModel;
import org.smartgresiter.wcaro.presenter.FamilyRegisterFragmentPresenter;
import org.smartgresiter.wcaro.util.Utils;
import org.smartregister.family.fragment.BaseFamilyRegisterFragment;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

public class FamilyRegisterFragment extends BaseFamilyRegisterFragment {

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

        View sortFilterBar = view.findViewById(R.id.register_sort_filter_bar_layout);
        sortFilterBar.setVisibility(View.GONE);

        View filterTextView = view.findViewById(R.id.filter_sort_layout);
        filterTextView.setVisibility(View.GONE);

        View dueOnlyTextView = view.findViewById(R.id.due_only_layout);
        dueOnlyTextView.setVisibility(View.VISIBLE);
        dueOnlyTextView.setOnClickListener(registerActionHandler);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new FamilyRegisterFragmentPresenter(this, new FamilyRegisterFramentModel(), viewConfigurationIdentifier);
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
    protected void onViewClicked(View view) {
        super.onViewClicked(view);
    }
}
