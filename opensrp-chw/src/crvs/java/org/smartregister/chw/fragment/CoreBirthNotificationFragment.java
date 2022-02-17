package org.smartregister.chw.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.R;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.activity.CoreChildRegisterActivity;
import org.smartregister.chw.core.contract.CoreChildRegisterFragmentContract;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.fragment.BaseChwRegisterFragment;
import org.smartregister.chw.core.model.CoreChildRegisterFragmentModel;
import org.smartregister.chw.core.presenter.CoreChildRegisterFragmentPresenter;
import org.smartregister.chw.core.provider.CoreBirthNotificationProvider;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.fragment.NoMatchDialogFragment;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;


public class CoreBirthNotificationFragment extends BaseChwRegisterFragment implements CoreChildRegisterFragmentContract.View {

    public static final String CLICK_VIEW_NORMAL = "click_view_normal";
    private static final String DUE_FILTER_TAG = "PRESSED";
    protected View view;
    protected View dueOnlyLayout;
    protected TextView dueOnlyFilter;
    protected boolean dueFilterActive = false;

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new CoreChildRegisterFragmentPresenter(this, new CoreChildRegisterFragmentModel(), viewConfigurationIdentifier);

    }

    @Override
    public void setUniqueID(String s) {
        if (getSearchView() != null) {
            getSearchView().setText(s);
        }
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        //// TODO: 15/08/19  
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
    protected String getMainCondition() {
        return presenter().getMainCondition();
    }

    @Override
    protected String getDefaultSortQuery() {
        return presenter().getDefaultSortQuery();
    }

    @Override
    protected void startRegistration() {
        ((CoreChildRegisterActivity) requireActivity()).startFormActivity(CoreConstants.JSON_FORM.getChildRegister(), null, "");
    }

    @Override
    protected void onViewClicked(View view) {
        if (getActivity() == null) {
            return;
        }

        if (view.getTag() != null && view.getTag(org.smartregister.chw.core.R.id.VIEW_ID) == CLICK_VIEW_NORMAL) {
            if (view.getTag() instanceof CommonPersonObjectClient) {
                goToChildDetailActivity((CommonPersonObjectClient) view.getTag(), false);
            }
        } else if (view.getId() == org.smartregister.chw.core.R.id.due_only_layout) {
            toggleFilterSelection(view);
        }
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        if (!SyncStatusBroadcastReceiver.getInstance().isSyncing() && (FetchStatus.fetched.equals(fetchStatus) || FetchStatus.nothingFetched.equals(fetchStatus)) && dueFilterActive && dueOnlyLayout != null) {
            dueFilter(dueOnlyLayout);
            Utils.showShortToast(getActivity(), getString(org.smartregister.chw.core.R.string.sync_complete));
            refreshSyncProgressSpinner();
        } else {
            super.onSyncInProgress(fetchStatus);
        }
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        if (!SyncStatusBroadcastReceiver.getInstance().isSyncing() && (FetchStatus.fetched.equals(fetchStatus) || FetchStatus.nothingFetched.equals(fetchStatus)) && (dueFilterActive && dueOnlyLayout != null)) {
            dueFilter(dueOnlyLayout);
            Utils.showShortToast(getActivity(), getString(org.smartregister.chw.core.R.string.sync_complete));
            refreshSyncProgressSpinner();
        } else {
            super.onSyncComplete(fetchStatus);
        }

        if (syncProgressBar != null) {
            syncProgressBar.setVisibility(View.GONE);
        }
        if (syncButton != null) {
            syncButton.setVisibility(View.GONE);
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
        super.refreshSyncProgressSpinner();
        if (syncButton != null) {
            syncButton.setVisibility(View.GONE);
        }
    }

    public void goToChildDetailActivity(CommonPersonObjectClient patient,
                                        boolean launchDialog) {
        if (launchDialog) {
            Timber.i(patient.name);
        }

        Intent intent = new Intent(getActivity(), CoreChildProfileActivity.class);
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        startActivity(intent);
    }

    public void toggleFilterSelection(View dueOnlyLayout) {
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

    private void normalFilter(View dueOnlyLayout) {
        filter(searchText(), "", presenter().getMainCondition());
        dueOnlyLayout.setTag(null);
        switchViews(dueOnlyLayout, false);
    }

    protected String getDueFilterCondition() {
        return presenter().getDueFilterCondition();
    }

    private void dueFilter(View dueOnlyLayout) {
        filter(searchText(), "", getDueFilterCondition());
        dueOnlyLayout.setTag(DUE_FILTER_TAG);
        switchViews(dueOnlyLayout, true);
    }

    protected void filterAndSortExecute() {
        filterandSortExecute(countBundle());
    }

    protected void filter(String filterString, String joinTableString, String mainConditionString) {
        filters = filterString;
        joinTable = joinTableString;
        mainCondition = mainConditionString;
        filterAndSortExecute();
    }

    private String searchText() {
        return (getSearchView() == null) ? "" : getSearchView().getText().toString();
    }

    protected TextView getDueOnlyTextView(View dueOnlyLayout) {
        return dueOnlyLayout.findViewById(org.smartregister.chw.core.R.id.due_only_text_view);
    }

    private void switchViews(View dueOnlyLayout, boolean isPress) {
        if (isPress) {
            getDueOnlyTextView(dueOnlyLayout).setCompoundDrawablesWithIntrinsicBounds(0, 0, org.smartregister.chw.core.R.drawable.ic_due_filter_on, 0);
        } else {
            getDueOnlyTextView(dueOnlyLayout).setCompoundDrawablesWithIntrinsicBounds(0, 0, org.smartregister.chw.core.R.drawable.ic_due_filter_off, 0);

        }
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        CoreBirthNotificationProvider childRegisterProvider = new CoreBirthNotificationProvider(requireActivity(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, childRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public CoreChildRegisterFragmentContract.Presenter presenter() {
        return (CoreChildRegisterFragmentContract.Presenter) presenter;
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        this.view = view;
        dueOnlyFilter = view.findViewById(org.smartregister.chw.core.R.id.due_only_text_view);
        dueOnlyFilter.setText(getResources().getString(R.string.birth_summary_toggle));
        dueOnlyLayout = view.findViewById(org.smartregister.chw.core.R.id.due_only_layout);
        dueOnlyLayout.setVisibility(View.VISIBLE);
        dueOnlyLayout.setOnClickListener(registerActionHandler);
    }

    @Override
    protected int getToolBarTitle() {
        return org.smartregister.chw.core.R.string.child_register_title;
    }

    @Override
    public void showNotFoundPopup(String uniqueId) {
        if (getActivity() == null) {
            return;
        }
        NoMatchDialogFragment.launchDialog((BaseRegisterActivity) getActivity(), DIALOG_TAG, uniqueId);
    }

    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {// Returns a new CursorLoader
            return new CursorLoader(requireActivity()) {
                @Override
                public Cursor loadInBackground() {
                    // Count query
                    String query = filterandSortQuery();
                    return commonRepository().rawCustomQueryForAdapter(query);
                }
            };
        }// An invalid id was passed in
        return null;
    }


    @Override
    public void countExecute() {
        try (Cursor c = commonRepository().rawCustomQueryForAdapter(getCountSelect())) {
            c.moveToFirst();
            clientAdapter.setTotalcount(c.getInt(0));

            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private String getCountSelect() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(countSelect);

        String query = countSelect;
        try {
            if (StringUtils.isNotBlank(filters))
                query = sqb.addCondition(((CoreChildRegisterFragmentPresenter) presenter()).getFilterString(filters));

            if (dueFilterActive)
                query = sqb.addCondition(((CoreChildRegisterFragmentPresenter) presenter()).getDueCondition());
            query = sqb.Endquery(query);
        } catch (Exception e) {
            Timber.e(e);
        }

        return query;
    }

    private String filterandSortQuery() {
        String query = "";
        try {
            if (StringUtils.isNotBlank(filters)) {
                query = "Select ec_child.id as _id , ec_child.relational_id as relationalid , ec_child.last_interacted_with , ec_child.base_entity_id , ec_child.first_name , ec_child.middle_name , ec_family_member.first_name as family_first_name , ec_family_member.last_name as family_last_name , ec_family_member.middle_name as family_middle_name , ec_family_member.phone_number as family_member_phone_number , ec_family_member.other_phone_number as family_member_phone_number_other , ec_family.village_town as family_home_address , ec_child.last_name , ec_child.unique_id , ec_child.gender , ec_child.dob , ec_child.dob_unknown , ec_child.last_home_visit , ec_child.visit_not_done , ec_child.early_bf_1hr , ec_child.physically_challenged , ec_child.birth_cert , ec_child.birth_cert_issue_date , ec_child.birth_cert_num , ec_child.system_birth_notification  , ec_child.birth_reg_type , ec_child.informant_reason, ec_child.birth_notification , ec_child.date_of_illness , ec_child.illness_description , ec_child.date_created , ec_child.action_taken , ec_child.vaccine_card, ec_child.birth_registration, 'birth' as clienttype FROM ec_child LEFT JOIN ec_family ON  ec_child.relational_id = ec_family.id COLLATE NOCASE  LEFT JOIN ec_family_member ON ec_family_member.base_entity_id = ec_family.primary_caregiver COLLATE NOCASE  LEFT JOIN (select base_entity_id , max(visit_date) visit_date from visits GROUP by base_entity_id) VISIT_SUMMARY ON VISIT_SUMMARY.base_entity_id = ec_child.base_entity_id WHERE  ec_child.date_removed is null AND ((( julianday('now') - julianday(ec_child.dob))/365.25) <5)   and (( ifnull(ec_child.entry_point,'') <> 'PNC' ) or (ifnull(ec_child.entry_point,'') = 'PNC' and ( date(ec_child.dob, '+28 days') <= date() and ((SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 0))) or (ifnull(ec_child.entry_point,'') = 'PNC'  and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 1)) and ((( julianday('now') - julianday(ec_child.dob))/365.25) < 5) " + ((CoreChildRegisterFragmentPresenter) presenter()).getFilterString(filters) + " Union Select ec_out_of_area_child.id as _id , '' as relationalid , ec_out_of_area_child.last_interacted_with , ec_out_of_area_child.base_entity_id , ec_out_of_area_child.first_name , ec_out_of_area_child.middle_name , ec_out_of_area_child.mother_name as family_first_name , '' as family_last_name , '' as family_middle_name , '' as family_member_phone_number , '' as family_member_phone_number_other , '' as family_home_address , ec_out_of_area_child.middle_name as last_name , ec_out_of_area_child.unique_id , ec_out_of_area_child.gender , ec_out_of_area_child.dob , ec_out_of_area_child.dob_unknown , '' as last_home_visit , '' as visit_not_done , '' as early_bf_1hr , '' as physically_challenged , ec_out_of_area_child.birth_cert , ec_out_of_area_child.birth_cert_issue_date , ec_out_of_area_child.birth_cert_num ,ec_out_of_area_child.system_birth_notification,ec_out_of_area_child.birth_reg_type,ec_out_of_area_child.informant_reason, ec_out_of_area_child.birth_notification , '' as date_of_illness , '' as illness_description , ec_out_of_area_child.date_created , '' as action_taken , '' as vaccine_card, ec_out_of_area_child.birth_registration, 'outOfArea' as clienttype FROM ec_out_of_area_child WHERE " + getOutOfAreaFilterString(filters) + " ORDER BY last_interacted_with DESC " + " LIMIT " + clientAdapter.getCurrentoffset() + "," + clientAdapter.getCurrentlimit();
            } else if (StringUtils.isBlank(filters) && !dueFilterActive) {
                query = "Select ec_child.id as _id , ec_child.relational_id as relationalid , ec_child.last_interacted_with , ec_child.base_entity_id , ec_child.first_name , ec_child.middle_name , ec_family_member.first_name as family_first_name , ec_family_member.last_name as family_last_name , ec_family_member.middle_name as family_middle_name , ec_family_member.phone_number as family_member_phone_number , ec_family_member.other_phone_number as family_member_phone_number_other , ec_family.village_town as family_home_address , ec_child.last_name , ec_child.unique_id , ec_child.gender , ec_child.dob , ec_child.dob_unknown , ec_child.last_home_visit , ec_child.visit_not_done , ec_child.early_bf_1hr , ec_child.physically_challenged , ec_child.birth_cert , ec_child.birth_cert_issue_date , ec_child.birth_cert_num , ec_child.system_birth_notification , ec_child.birth_reg_type  , ec_child.informant_reason, ec_child.birth_notification, ec_child.date_of_illness , ec_child.illness_description , ec_child.date_created , ec_child.action_taken , ec_child.vaccine_card, ec_child.birth_registration, 'birth' as clienttype FROM ec_child LEFT JOIN ec_family ON  ec_child.relational_id = ec_family.id COLLATE NOCASE  LEFT JOIN ec_family_member ON ec_family_member.base_entity_id = ec_family.primary_caregiver COLLATE NOCASE  LEFT JOIN (select base_entity_id , max(visit_date) visit_date from visits GROUP by base_entity_id) VISIT_SUMMARY ON VISIT_SUMMARY.base_entity_id = ec_child.base_entity_id WHERE  ec_child.date_removed is null AND ((( julianday('now') - julianday(ec_child.dob))/365.25) <5)   and (( ifnull(ec_child.entry_point,'') <> 'PNC' ) or (ifnull(ec_child.entry_point,'') = 'PNC' and ( date(ec_child.dob, '+28 days') <= date() and ((SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 0))) or (ifnull(ec_child.entry_point,'') = 'PNC'  and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 1)) and ((( julianday('now') - julianday(ec_child.dob))/365.25) < 5) Union Select ec_out_of_area_child.id as _id , '' as relationalid , ec_out_of_area_child.last_interacted_with , ec_out_of_area_child.base_entity_id , ec_out_of_area_child.first_name , ec_out_of_area_child.middle_name , ec_out_of_area_child.mother_name as family_first_name , '' as family_last_name , '' as family_middle_name , '' as family_member_phone_number , '' as family_member_phone_number_other , '' as family_home_address , ec_out_of_area_child.middle_name as last_name , ec_out_of_area_child.unique_id , ec_out_of_area_child.gender , ec_out_of_area_child.dob , ec_out_of_area_child.dob_unknown , '' as last_home_visit , '' as visit_not_done , '' as early_bf_1hr , '' as physically_challenged , ec_out_of_area_child.birth_cert , ec_out_of_area_child.birth_cert_issue_date , ec_out_of_area_child.birth_cert_num ,ec_out_of_area_child.system_birth_notification,ec_out_of_area_child.birth_reg_type,ec_out_of_area_child.informant_reason, ec_out_of_area_child.birth_notification , '' as date_of_illness , '' as illness_description , ec_out_of_area_child.date_created , '' as action_taken , '' as vaccine_card, ec_out_of_area_child.birth_registration, 'outOfArea' as clienttype FROM ec_out_of_area_child" + " ORDER BY last_interacted_with DESC " + " LIMIT " + clientAdapter.getCurrentoffset() + "," + clientAdapter.getCurrentlimit();
            }

            if (dueFilterActive) {
                query = "Select ec_child.id as _id , ec_child.relational_id as relationalid , ec_child.last_interacted_with , ec_child.base_entity_id , ec_child.first_name , ec_child.middle_name , ec_family_member.first_name as family_first_name , ec_family_member.last_name as family_last_name , ec_family_member.middle_name as family_middle_name , ec_family_member.phone_number as family_member_phone_number , ec_family_member.other_phone_number as family_member_phone_number_other , ec_family.village_town as family_home_address , ec_child.last_name , ec_child.unique_id , ec_child.gender , ec_child.dob , ec_child.dob_unknown , ec_child.last_home_visit , ec_child.visit_not_done , ec_child.early_bf_1hr , ec_child.physically_challenged , ec_child.birth_cert , ec_child.birth_cert_issue_date , ec_child.birth_cert_num , ec_child.system_birth_notification , ec_child.birth_reg_type  , ec_child.informant_reason, ec_child.birth_notification, ec_child.date_of_illness , ec_child.illness_description , ec_child.date_created , ec_child.action_taken , ec_child.vaccine_card, ec_child.birth_registration, 'birth' as clienttype FROM ec_child LEFT JOIN ec_family ON  ec_child.relational_id = ec_family.id COLLATE NOCASE  LEFT JOIN ec_family_member ON ec_family_member.base_entity_id = ec_family.primary_caregiver COLLATE NOCASE  LEFT JOIN (select base_entity_id , max(visit_date) visit_date from visits GROUP by base_entity_id) VISIT_SUMMARY ON VISIT_SUMMARY.base_entity_id = ec_child.base_entity_id WHERE  ec_child.date_removed is null AND ((( julianday('now') - julianday(ec_child.dob))/365.25) <5)   and (( ifnull(ec_child.entry_point,'') <> 'PNC' ) or (ifnull(ec_child.entry_point,'') = 'PNC' and ( date(ec_child.dob, '+28 days') <= date() and ((SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 0))) or (ifnull(ec_child.entry_point,'') = 'PNC'  and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 1)) and ((( julianday('now') - julianday(ec_child.dob))/365.25) < 5) " + getDueCondition() + " Union Select ec_out_of_area_child.id as _id , '' as relationalid , ec_out_of_area_child.last_interacted_with , ec_out_of_area_child.base_entity_id , ec_out_of_area_child.first_name , ec_out_of_area_child.middle_name , ec_out_of_area_child.mother_name as family_first_name , '' as family_last_name , '' as family_middle_name , '' as family_member_phone_number , '' as family_member_phone_number_other , '' as family_home_address , ec_out_of_area_child.middle_name as last_name , ec_out_of_area_child.unique_id , ec_out_of_area_child.gender , ec_out_of_area_child.dob , ec_out_of_area_child.dob_unknown , '' as last_home_visit , '' as visit_not_done , '' as early_bf_1hr , '' as physically_challenged , ec_out_of_area_child.birth_cert , ec_out_of_area_child.birth_cert_issue_date , ec_out_of_area_child.birth_cert_num ,ec_out_of_area_child.system_birth_notification,ec_out_of_area_child.birth_reg_type,ec_out_of_area_child.informant_reason, ec_out_of_area_child.birth_notification , '' as date_of_illness , '' as illness_description , ec_out_of_area_child.date_created , '' as action_taken , '' as vaccine_card, ec_out_of_area_child.birth_registration, 'outOfArea' as clienttype FROM ec_out_of_area_child " + getOutOfAreaDueCondition() + " ORDER BY last_interacted_with DESC " + " LIMIT " + clientAdapter.getCurrentoffset() + "," + clientAdapter.getCurrentlimit();
            }

        } catch (Exception e) {
            Timber.e(e);
        }

        return query;
    }

    public String getOutOfAreaFilterString(String filters) {

        StringBuilder customFilter = new StringBuilder();
        if (StringUtils.isNotBlank(filters)) {
            customFilter.append(" ( ");
            customFilter.append(MessageFormat.format(" {0}.{1} like ''%{2}%'' ", "ec_out_of_area_child", org.smartregister.chw.anc.util.DBConstants.KEY.FIRST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", "ec_out_of_area_child", org.smartregister.chw.anc.util.DBConstants.KEY.MIDDLE_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", "ec_out_of_area_child", org.smartregister.chw.anc.util.DBConstants.KEY.UNIQUE_ID, filters));

            customFilter.append(" ) ");
        }

        return customFilter.toString();
    }

    public String getDueCondition() {
        return " and " + CoreConstants.TABLE_NAME.CHILD + ".birth_cert = 'Yes'";
    }

    public String getOutOfAreaDueCondition() {
        return " WHERE ec_out_of_area_child.birth_cert = 'Yes'";
    }
}
