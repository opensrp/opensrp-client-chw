package org.smartregister.chw.fragment;

import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.model.ChildRegisterFragmentModel;
import org.smartregister.chw.presenter.ChildRegisterFragmentPresenter;
import org.smartregister.chw.provider.BirthNotificationProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.view.activity.BaseRegisterActivity;
import java.util.Set;

public class BirthNotificationRegisterFragment extends CoreBirthNotificationFragment {

    @Override
    protected void onViewClicked(android.view.View view) {
        super.onViewClicked(view);
    }

    @Override
    public void goToChildDetailActivity(CommonPersonObjectClient patient, boolean launchDialog) {

    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        BirthNotificationProvider childRegisterProvider = new BirthNotificationProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, childRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new ChildRegisterFragmentPresenter(this, new ChildRegisterFragmentModel(), viewConfigurationIdentifier);
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

    @Override
    protected int getToolBarTitle() {
        return R.string.birth_certification;
    }
}
