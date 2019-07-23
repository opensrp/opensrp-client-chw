package org.smartregister.chw.fragment;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.AncHomeVisitActivity;
import org.smartregister.chw.activity.AncMemberProfileActivity;
import org.smartregister.chw.activity.PncHomeVisitActivity;
import org.smartregister.chw.activity.PncMemberProfileActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.custom_view.NavigationMenu;
import org.smartregister.chw.model.PncRegisterFragmentModel;
import org.smartregister.chw.pnc.fragment.BasePncRegisterFragment;
import org.smartregister.chw.pnc.presenter.BasePncRegisterFragmentPresenter;
import org.smartregister.chw.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.HashMap;

public class PncRegisterFragment extends BasePncRegisterFragment {

    private View view;

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new BasePncRegisterFragmentPresenter(this, new PncRegisterFragmentModel(), null);
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        this.view = view;

        Toolbar toolbar = view.findViewById(org.smartregister.R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);
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

       View dueOnlyLayout = view.findViewById(R.id.due_only_layout);
       dueOnlyLayout.setVisibility(View.VISIBLE);
       dueOnlyLayout.setOnClickListener(registerActionHandler);

        if (getSearchView() != null) {
            getSearchView().setBackgroundResource(org.smartregister.family.R.color.white);
            getSearchView().setCompoundDrawablesWithIntrinsicBounds(org.smartregister.family.R.drawable.ic_action_search, 0, 0, 0);
            getSearchView().setTextColor(getResources().getColor(R.color.text_black));
        }

        NavigationMenu.getInstance(getActivity(), null, toolbar);
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
    protected void refreshSyncProgressSpinner() {
        if (syncProgressBar != null) {
            syncProgressBar.setVisibility(View.GONE);
        }
        if (syncButton != null) {
            syncButton.setVisibility(View.GONE);
        }
    }


    @Override
    protected void openHomeVisit(CommonPersonObjectClient client) {
        PncHomeVisitActivity.startMe(getActivity(), new MemberObject(client), false);
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {

        HashMap<String, String> detailsMap = ChwApplication.ancRegisterRepository().getFamilyNameAndPhone(Utils.getValue(client.getColumnmaps(), org.smartregister.family.util.DBConstants.KEY.FAMILY_HEAD, false));

        String familyName = "";
        String familyHeadPhone = "";
        if (detailsMap != null) {
            familyName = detailsMap.get(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_NAME);
            familyHeadPhone = detailsMap.get(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_PHONE);
        }

        PncMemberProfileActivity.startMe(getActivity(), new MemberObject(client), familyName, familyHeadPhone);
    }
}
