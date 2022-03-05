package org.smartregister.chw.fragment;

import org.smartregister.chw.core.fragment.CorePmtctRegisterFragment;
import org.smartregister.chw.activity.PmtctProfileActivity;
import org.smartregister.chw.activity.PmtctRegisterActivity;
import org.smartregister.chw.core.provider.CorePmtctRegisterProvider;
import org.smartregister.chw.model.PmtctRegisterFragmentModel;
import org.smartregister.chw.presenter.PmtctRegisterFragmentPresenter;
import org.smartregister.chw.provider.PmtctRegisterProvider;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.Set;

import timber.log.Timber;

public class PmtctRegisterFragment extends CorePmtctRegisterFragment {
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

        presenter = new PmtctRegisterFragmentPresenter(this, new PmtctRegisterFragmentModel(), viewConfigurationIdentifier);

    }

    @Override
    protected void openProfile(String baseEntityId) {
       PmtctProfileActivity.startPmtctActivity(getActivity(), baseEntityId);
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
}
