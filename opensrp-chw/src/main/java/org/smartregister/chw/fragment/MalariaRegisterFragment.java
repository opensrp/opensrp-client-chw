package org.smartregister.chw.fragment;

import android.view.View;

import org.smartregister.chw.activity.MalariaFollowUpVisitActivity;
import org.smartregister.chw.activity.MalariaProfileActivity;
import org.smartregister.chw.core.fragment.CoreMalariaRegisterFragment;
import org.smartregister.chw.core.model.CoreMalariaRegisterFragmentModel;
import org.smartregister.chw.malaria.domain.MemberObject;
import org.smartregister.chw.presenter.MalariaRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.view.activity.BaseRegisterActivity;

public class MalariaRegisterFragment extends CoreMalariaRegisterFragment {

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new MalariaRegisterFragmentPresenter(this, new CoreMalariaRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {
        MalariaProfileActivity.startMalariaActivity(getActivity(), new MemberObject(client), client);
    }

    @Override
    protected void openFollowUpVisit(CommonPersonObjectClient client) {
        MalariaFollowUpVisitActivity.startMalariaFollowUpActivity(getActivity(), client.getCaseId());
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


