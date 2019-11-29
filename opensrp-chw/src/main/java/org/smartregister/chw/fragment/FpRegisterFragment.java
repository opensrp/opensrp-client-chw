package org.smartregister.chw.fragment;

import android.view.View;

import org.smartregister.chw.core.fragment.CoreFpRegisterFragment;
import org.smartregister.chw.core.model.CoreFpRegisterFragmentModel;
import org.smartregister.chw.presenter.FpRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.view.activity.BaseRegisterActivity;

public class FpRegisterFragment extends CoreFpRegisterFragment {

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new FpRegisterFragmentPresenter(this, new CoreFpRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {
//        MalariaProfileActivity.startMalariaActivity(getActivity(), new MemberObject(client), client);
    }

    @Override
    protected void openFollowUpVisit(CommonPersonObjectClient client) {
//        MalariaFollowUpVisitActivity.startMalariaRegistrationActivity(getActivity(), client.getCaseId());
    }

    @Override
    protected void toggleFilterSelection(View dueOnlyLayout) {
        super.toggleFilterSelection(dueOnlyLayout);
    }

    @Override
    protected String searchText() {
        return super.searchText();
    }

    @Override
    protected void dueFilter(View dueOnlyLayout) {
        super.dueFilter(dueOnlyLayout);
    }

    @Override
    protected void normalFilter(View dueOnlyLayout) {
        super.normalFilter(dueOnlyLayout);
    }
}


