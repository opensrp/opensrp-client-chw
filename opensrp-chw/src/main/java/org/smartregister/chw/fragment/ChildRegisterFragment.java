package org.smartregister.chw.fragment;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.ChildHomeVisitActivity;
import org.smartregister.chw.activity.ChildProfileActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.fragment.CoreChildRegisterFragment;
import org.smartregister.chw.provider.ChildRegisterProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.Set;

import timber.log.Timber;

public class ChildRegisterFragment extends CoreChildRegisterFragment {

    @Override
    protected void onViewClicked(android.view.View view) {
        super.onViewClicked(view);
        if (view.getTag() instanceof CommonPersonObjectClient
                && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_DOSAGE_STATUS) {
            CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
            ChildHomeVisitActivity.startMe(getActivity(), new MemberObject(client), false, ChildHomeVisitActivity.class);
        }
    }

    @Override
    public void goToChildDetailActivity(CommonPersonObjectClient patient,
                                        boolean launchDialog) {
        if (launchDialog) {
            Timber.i(patient.name);
        }

        ChildProfileActivity.startMe(getActivity(), false, new MemberObject(patient), ChildProfileActivity.class);
    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        ChildRegisterProvider childRegisterProvider = new ChildRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, childRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }
}
