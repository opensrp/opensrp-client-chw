package org.smartregister.chw.fragment;

import org.smartregister.chw.activity.PncHomeVisitActivity;
import org.smartregister.chw.activity.PncMemberProfileActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.fragment.CorePncRegisterFragment;
import org.smartregister.chw.model.ChwPncRegisterFragmentModel;
import org.smartregister.chw.presenter.PncRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class PncRegisterFragment extends CorePncRegisterFragment {

    @Override
    protected void openHomeVisit(CommonPersonObjectClient client) {
        PncHomeVisitActivity.startMe(getActivity(), new MemberObject(client), false);
    }

    @Override
    protected void openPncMemberProfile(CommonPersonObjectClient client) {
        MemberObject memberObject = new MemberObject(client);
        PncMemberProfileActivity.startMe(getActivity(), memberObject.getBaseEntityId());
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new PncRegisterFragmentPresenter(this, new ChwPncRegisterFragmentModel(), null);
    }

}
