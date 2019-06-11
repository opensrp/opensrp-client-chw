package org.smartregister.chw.fragment;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.MalariaProfileActivity;
import org.smartregister.chw.custom_view.NavigationMenu;
import org.smartregister.chw.malaria.fragment.BaseMalariaRegisterFragment;
import org.smartregister.chw.malaria.provider.MalariaRegisterProvider;
import org.smartregister.chw.model.MalariaRegisterFragmentModel;
import org.smartregister.chw.presenter.MalariaRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.family.util.DBConstants;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;

import java.util.Set;

public class MalariaRegisterFragment extends BaseMalariaRegisterFragment {
    private static final String TAG = MalariaRegisterFragment.class.getCanonicalName();
    private View view;
    private View dueOnlyLayout;

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new MalariaRegisterFragmentPresenter(this, new MalariaRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        MalariaRegisterProvider malariaRegisterProvider = new MalariaRegisterProvider(getActivity(), paginationViewHandler, registerActionHandler, visibleColumns, commonRepository());
        clientAdapter = new RecyclerViewPaginatedAdapter(null, malariaRegisterProvider, this.context().commonrepository(tablename));
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
        searchBarLayout.setBackgroundResource(R.color.chw_primary);
        searchBarLayout.setPadding(searchBarLayout.getPaddingLeft(), searchBarLayout.getPaddingTop(), searchBarLayout.getPaddingRight(), (int) org.smartregister.chw.util.Utils.convertDpToPixel(10, getActivity()));


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
            titleView.setText(getString(R.string.malaria_registration));
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

        dueOnlyLayout = view.findViewById(R.id.due_only_layout);
        dueOnlyLayout.setVisibility(View.VISIBLE);
        dueOnlyLayout.setOnClickListener(registerActionHandler);
    }
    @Override
    protected void onViewClicked(View view) {

        if (getActivity() == null) {
            return;
        }

        if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_NORMAL) {
            if (view.getTag() instanceof CommonPersonObjectClient) {
                goToPatientDetailActivity((CommonPersonObjectClient) view.getTag(), false);
            }
        } else if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_DOSAGE_STATUS) {
            if (view.getTag() instanceof CommonPersonObjectClient) {
                Toast.makeText(getActivity().getApplicationContext(), CLICK_VIEW_DOSAGE_STATUS, Toast.LENGTH_LONG).show();
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
//            toggleFilterSelection(view);
        }
    }


    private void goToPatientDetailActivity(CommonPersonObjectClient patient,
                                           boolean goToDuePage) {

        Intent intent = new Intent(getActivity(), MalariaProfileActivity.class);
        intent.putExtra("base_entity_id", patient.getCaseId());
//        intent.putExtra(Constants.FAMILY_HEAD, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.FAMILY_HEAD, false));
//        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.PRIMARY_CAREGIVER, false));
//        intent.putExtra(Constants.INTENT_KEY.VILLAGE_TOWN, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.VILLAGE_TOWN, false));
//        intent.putExtra(Constants.INTENT_KEY.FAMILY_NAME, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.FIRST_NAME, false));
//        intent.putExtra(Constants.INTENT_KEY.GO_TO_DUE_PAGE, goToDuePage);

        startActivity(intent);
    }

}


