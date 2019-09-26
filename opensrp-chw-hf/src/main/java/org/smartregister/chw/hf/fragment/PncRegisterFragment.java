package org.smartregister.chw.hf.fragment;

import android.view.View;

import org.smartregister.chw.core.fragment.CorePncRegisterFragment;
import org.smartregister.chw.core.provider.ChwPncRegisterProvider;
import org.smartregister.chw.hf.activity.PncMemberProfileActivity;
import org.smartregister.chw.hf.provider.HfPncRegisterProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.Set;

public class PncRegisterFragment extends CorePncRegisterFragment {

    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        ChwPncRegisterProvider provider = new HfPncRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, provider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        dueOnlyLayout.setVisibility(View.GONE);
    }

    @Override
    protected void openHomeVisit(CommonPersonObjectClient client) {
        //Overridden
    }

    @Override
    protected void openPncMemberProfile(CommonPersonObjectClient client) {
        PncMemberProfileActivity.startMe(getActivity(), client.getCaseId());
    }
}
