package org.smartregister.chw.fragment;

import android.content.Intent;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.model.CoreDeadClientsFragmentModel;
import org.smartregister.chw.presenter.DeadClientsFragmentPresenter;
import org.smartregister.chw.provider.DeadClientsProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.view.activity.BaseRegisterActivity;
import java.util.Set;

public class DeadClientsFragment extends CoreDeadClientsFragment {

    protected void onViewClicked(android.view.View view) {
        super.onViewClicked(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Do nothing
    }

    @Override
    public void goToChildDetailActivity(CommonPersonObjectClient patient, boolean launchDialog) {
        // Do nothing
    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        DeadClientsProvider deadClientsProvider = new DeadClientsProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, deadClientsProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new DeadClientsFragmentPresenter(this, new CoreDeadClientsFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    public void setupViews(android.view.View view) {
        super.setupViews(view);

        if (ChwApplication.getApplicationFlavor().hasDefaultDueFilterForChildClient()) {
            android.view.View dueOnlyLayout = view.findViewById(org.smartregister.chw.core.R.id.due_only_layout);
            dueOnlyLayout.setVisibility(android.view.View.VISIBLE);
            dueOnlyLayout.setOnClickListener(registerActionHandler);
            dueOnlyLayout.setTag(null);
            toggleFilterSelection(dueOnlyLayout);
        }
    }

}
