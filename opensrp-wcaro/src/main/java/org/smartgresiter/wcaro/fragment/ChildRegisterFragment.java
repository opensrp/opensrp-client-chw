package org.smartgresiter.wcaro.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.activity.ChildRegisterActivity;
import org.smartgresiter.wcaro.contract.ChildRegisterFragmentContract;
import org.smartgresiter.wcaro.domain.ChildMetadata;
import org.smartgresiter.wcaro.model.ChildRegisterFragmentModel;
import org.smartgresiter.wcaro.presenter.ChildRegisterFragmentPresenter;
import org.smartgresiter.wcaro.provider.ChildRegisterProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.family.contract.FamilyRegisterFragmentContract;
import org.smartregister.family.fragment.NoMatchDialogFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Set;

public class ChildRegisterFragment extends BaseRegisterFragment implements ChildRegisterFragmentContract.View {

    private static final String TAG = ChildRegisterFragment.class.getCanonicalName();
    public static final String CLICK_VIEW_NORMAL = "click_view_normal";
    public static final String CLICK_VIEW_DOSAGE_STATUS = "click_view_dosage_status";
    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new ChildRegisterFragmentPresenter(this, new ChildRegisterFragmentModel(), viewConfigurationIdentifier);

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

        // Update top left icon
        qrCodeScanImageView = view.findViewById(org.smartregister.family.R.id.scanQrCode);
        if (qrCodeScanImageView != null) {
            qrCodeScanImageView.setVisibility(View.GONE);
        }

        ImageView leftMenu = view.findViewById(org.smartregister.family.R.id.left_menu);
        if (leftMenu != null) {
            leftMenu.setVisibility(View.VISIBLE);
        }

        // Update Search bar
        View searchBarLayout = view.findViewById(org.smartregister.family.R.id.search_bar_layout);
        searchBarLayout.setBackgroundResource(org.smartregister.family.R.color.customAppThemeBlue);

        if (getSearchView() != null) {
            getSearchView().setBackgroundResource(org.smartregister.family.R.color.white);
            getSearchView().setCompoundDrawablesWithIntrinsicBounds(org.smartregister.family.R.drawable.ic_action_search, 0, 0, 0);
        }

        // Update sort filter
        TextView filterView = view.findViewById(org.smartregister.family.R.id.filter_text_view);
        if (filterView != null) {
            filterView.setText(getString(org.smartregister.family.R.string.sort));
        }

        // Update title name
        ImageView logo = view.findViewById(org.smartregister.family.R.id.opensrp_logo_image_view);
        if (logo != null) {
            logo.setVisibility(View.GONE);
        }

        CustomFontTextView titleView = view.findViewById(org.smartregister.family.R.id.txt_title_label);
        if (titleView != null) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(getString(org.smartregister.family.R.string.all_families));
            titleView.setFontVariant(FontVariant.REGULAR);
        }
    }
    @Override
    protected void refreshSyncProgressSpinner() {
        super.refreshSyncProgressSpinner();
        if(syncButton != null) {
            syncButton.setVisibility(View.GONE);
        }
    }
    @Override
    protected void startRegistration() {
        //TODO need to change the form name.
        ((ChildRegisterActivity)getActivity()).startFormActivity("child_enrollment",null,null);
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
            goToChildDetailActivity((CommonPersonObjectClient) view.getTag(), false);
        } else if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_DOSAGE_STATUS) {
            CommonPersonObjectClient pc = (CommonPersonObjectClient) view.getTag();
            String baseEntityId = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, true);

            if (StringUtils.isNotBlank(baseEntityId)) {
                // TODO Proceed to dose status
            }
        }
    }
    private void goToChildDetailActivity(CommonPersonObjectClient patient,
                                           boolean launchDialog) {
        if (launchDialog) {
            Log.i(ChildRegisterFragment.TAG, patient.name);
        }

        Intent intent = new Intent(getActivity(), org.smartregister.family.util.Utils.metadata().profileActivity);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        startActivity(intent);
    }

    @Override
    public FamilyRegisterFragmentContract.Presenter presenter() {
        return (FamilyRegisterFragmentContract.Presenter)presenter;
    }
}
