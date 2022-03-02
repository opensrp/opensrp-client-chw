package org.smartregister.chw.fragment;

import android.app.Activity;

import org.smartregister.chw.activity.AncHomeVisitActivity;
import org.smartregister.chw.activity.AncMemberProfileActivity;
import org.smartregister.chw.core.fragment.CoreAncRegisterFragment;
import org.smartregister.chw.core.provider.ChwAncRegisterProvider;
import org.smartregister.chw.model.AncRegisterFragmentModel;
import org.smartregister.chw.presenter.ChwAncRegisterFragmentPresenter;
import org.smartregister.chw.provider.AncFollowupRegisterProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.Set;

public class AncPartnerFollowupRegisterFragment extends CoreAncRegisterFragment {
    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        ChwAncRegisterProvider provider = new AncFollowupRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, provider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new ChwAncRegisterFragmentPresenter(this, new AncRegisterFragmentModel(), null);
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {
        AncMemberProfileActivity.startMe(getActivity(), client.getCaseId());
    }

    @Override
    protected void openHomeVisit(CommonPersonObjectClient client) {
        Activity activity = getActivity();
        if (activity == null)
            return;

        AncHomeVisitActivity.startMe(activity, client.getCaseId(), false);
    }
}
