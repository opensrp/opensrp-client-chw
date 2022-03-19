package org.smartregister.chw.fragment;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.PmtctRegisterActivity;
import org.smartregister.chw.activity.PmtctcDetailsActivity;
import org.smartregister.chw.core.fragment.CorePmtctRegisterFragment;
import org.smartregister.chw.model.MotherChampionRegisterFragmentModel;
import org.smartregister.chw.presenter.MotherChampionRegisterFragmentPresenter;
import org.smartregister.chw.provider.PmtctRegisterProvider;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.Set;

import timber.log.Timber;

public class MotherChampionRegisterFragment extends CorePmtctRegisterFragment {
    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = null;
        try {
            viewConfigurationIdentifier = ((PmtctRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        } catch (Exception e) {
            Timber.e(e);
        }

        presenter = new MotherChampionRegisterFragmentPresenter(this, new MotherChampionRegisterFragmentModel(), viewConfigurationIdentifier);

    }

    @Override
    protected void openProfile(String baseEntityId) {
        PmtctcDetailsActivity.startPmtctDetailsActivity(getActivity(), baseEntityId);
    }

    @Override
    public void setupViews(android.view.View view) {
        super.setupViews(view);
        titleLabelView.setText(R.string.mother_champion);
    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        PmtctRegisterProvider pmtctRegisterProvider = new PmtctRegisterProvider(getActivity(), paginationViewHandler, registerActionHandler, visibleColumns);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, pmtctRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void openFollowUpVisit(String baseEntityId) {
        //  PmtctFollowUpVisitActivity.startPmtctFollowUpActivity(getActivity(),baseEntityId);
    }

    @Override
    protected void refreshSyncProgressSpinner() {
        if (isSyncing()) {
            if (syncProgressBar != null) {
                syncProgressBar.setVisibility(android.view.View.VISIBLE);
            }
            if (syncButton != null) {
                syncButton.setVisibility(android.view.View.GONE);
            }
        } else {
            if (syncProgressBar != null) {
                syncProgressBar.setVisibility(android.view.View.GONE);
            }
            if (syncButton != null) {
                syncButton.setVisibility(android.view.View.GONE);
            }
        }
    }
}
