package org.smartgresiter.wcaro.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.activity.ChildProfileActivity;
import org.smartgresiter.wcaro.activity.ChildRegisterActivity;
import org.smartgresiter.wcaro.contract.ChildRegisterFragmentContract;
import org.smartgresiter.wcaro.custom_view.NavigationMenu;
import org.smartgresiter.wcaro.model.ChildRegisterFragmentModel;
import org.smartgresiter.wcaro.presenter.ChildRegisterFragmentPresenter;
import org.smartgresiter.wcaro.provider.ChildRegisterProvider;
import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.fragment.NoMatchDialogFragment;
import org.smartregister.family.util.DBConstants;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ChildRegisterFragment extends BaseRegisterFragment implements ChildRegisterFragmentContract.View {

    private static final String TAG = ChildRegisterFragment.class.getCanonicalName();
    public static final String CLICK_VIEW_NORMAL = "click_view_normal";
    public static final String CLICK_VIEW_DOSAGE_STATUS = "click_view_dosage_status";
    View view;

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new ChildRegisterFragmentPresenter(this, new ChildRegisterFragmentModel(), viewConfigurationIdentifier);

    }

    @Override
    public void filter(String filterString, String joinTableString, String mainConditionString, boolean qrCode) {
        this.joinTables = new String[]{Constants.TABLE_NAME.FAMILY_MEMBER};
        super.filter(filterString, joinTableString, mainConditionString, qrCode);
    }

    protected void filter(String filterString, String joinTableString, String mainConditionString) {
        filters = filterString;
        joinTable = joinTableString;
        mainCondition = mainConditionString;
        filterandSortExecute(countBundle());
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        ChildRegisterProvider childRegisterProvider = new ChildRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, childRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        this.view = view;

        Toolbar toolbar = view.findViewById(org.smartregister.R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);
        NavigationMenu.getInstance(getActivity(), null, toolbar);
        // Update top left icon
        qrCodeScanImageView = view.findViewById(org.smartregister.family.R.id.scanQrCode);
        if (qrCodeScanImageView != null) {
            qrCodeScanImageView.setVisibility(View.GONE);
        }

        // Update Search bar
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View searchBarLayout = view.findViewById(R.id.search_bar_layout);
        searchBarLayout.setLayoutParams(params);
        searchBarLayout.setBackgroundResource(R.color.wcaro_primary);
        searchBarLayout.setPadding(searchBarLayout.getPaddingLeft(), searchBarLayout.getPaddingTop(), searchBarLayout.getPaddingRight(), (int) org.smartgresiter.wcaro.util.Utils.convertDpToPixel(10, getActivity()));


        if (getSearchView() != null) {
            getSearchView().setBackgroundResource(org.smartregister.family.R.color.white);
            getSearchView().setCompoundDrawablesWithIntrinsicBounds(org.smartregister.family.R.drawable.ic_action_search, 0, 0, 0);
            getSearchView().setTextColor(getResources().getColor(R.color.text_black));
        }

        // Update title name
        ImageView logo = view.findViewById(org.smartregister.family.R.id.opensrp_logo_image_view);
        if (logo != null) {
            logo.setVisibility(View.GONE);
        }

        CustomFontTextView titleView = view.findViewById(org.smartregister.family.R.id.txt_title_label);
        if (titleView != null) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(getString(R.string.child_register_title));
            titleView.setFontVariant(FontVariant.REGULAR);
            titleView.setPadding(0, titleView.getTop(), titleView.getPaddingRight(), titleView.getPaddingBottom());
        }

        View navbarContainer = view.findViewById(R.id.register_nav_bar_container);
        navbarContainer.setFocusable(false);

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
    }

    @Override
    protected void refreshSyncProgressSpinner() {
        super.refreshSyncProgressSpinner();
        if (syncButton != null) {
            syncButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void startRegistration() {
        ((ChildRegisterActivity) getActivity()).startFormActivity(Constants.JSON_FORM.CHILD_REGISTER, null, null);
        //getActivity().startFormActivity(Utils.metadata().familyRegister.formName, null, null);
    }

    @Override
    public void showNotFoundPopup(String uniqueId) {
        if (getActivity() == null) {
            return;
        }
        NoMatchDialogFragment.launchDialog((BaseRegisterActivity) getActivity(), DIALOG_TAG, uniqueId);
    }

    @Override
    public void setUniqueID(String s) {
        if (getSearchView() != null) {
            getSearchView().setText(s);
        }
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
    protected void onViewClicked(View view) {

        if (getActivity() == null) {
            return;
        }

        if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_NORMAL) {
            if (view.getTag() instanceof CommonPersonObjectClient) {
                goToChildDetailActivity((CommonPersonObjectClient) view.getTag(), false);
            }
        } else if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_DOSAGE_STATUS) {
            if (view.getTag() instanceof CommonPersonObjectClient) {
                CommonPersonObjectClient pc = (CommonPersonObjectClient) view.getTag();
                String baseEntityId = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, true);

                if (StringUtils.isNotBlank(baseEntityId)) {
                    ChildHomeVisitFragment childHomeVisitFragment = ChildHomeVisitFragment.newInstance();
                    childHomeVisitFragment.setContext(getActivity());
                    childHomeVisitFragment.setChildClient(pc);
                    childHomeVisitFragment.show(getActivity().getFragmentManager(), ChildHomeVisitFragment.DIALOG_TAG);
                }
            }
        } else if (view.getId() == R.id.due_only_layout) {
            toggleFilterSelection(view);
        }
    }

    private void toggleFilterSelection(View dueOnlyLayout) {
        if (dueOnlyLayout != null) {
            String tagString = "PRESSED";
            if (dueOnlyLayout.getTag() == null) {
                filter("", "", presenter().getDueFilterCondition());
                dueOnlyLayout.setTag(tagString);
                switchViews(dueOnlyLayout, true);
            } else if (dueOnlyLayout.getTag().toString().equals(tagString)) {
                filter("", "", presenter().getMainCondition());
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

    private void goToChildDetailActivity(CommonPersonObjectClient patient,
                                         boolean launchDialog) {
        if (launchDialog) {
            Log.i(ChildRegisterFragment.TAG, patient.name);
        }

        Intent intent = new Intent(getActivity(), ChildProfileActivity.class);
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        startActivity(intent);
    }

    @Override
    public ChildRegisterFragmentContract.Presenter presenter() {
        return (ChildRegisterFragmentContract.Presenter) presenter;
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

    private String filterandSortQuery() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);

        String query = "";
        try {
            if (isValidFilterForFts(commonRepository())) {
                String sql = ChildDBConstants.childMainFilter(mainCondition, filters, Sortqueries, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset());
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (StringUtils.isBlank(filters)) {
            return super.onCreateLoader(id, args);
        } else {
            switch (id) {
                case LOADER_ID:
                    // Returns a new CursorLoader
                    return new CursorLoader(getActivity()) {
                        @Override
                        public Cursor loadInBackground() {
                            // Count query
                            String query = "";
                            // Select register query
                            query = filterandSortQuery();
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
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
    }
}
