package org.smartregister.chw.fragment;

import android.app.Activity;

import org.smartregister.chw.activity.AncHomeVisitActivity;
import org.smartregister.chw.activity.AncMemberProfileActivity;
import org.smartregister.chw.core.fragment.CoreAncRegisterFragment;
import org.smartregister.chw.model.AncRegisterFragmentModel;
import org.smartregister.chw.presenter.ChwAncRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class AncRegisterFragment extends CoreAncRegisterFragment {
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
