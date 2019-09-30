package org.smartregister.chw.fragment;

import org.smartregister.chw.activity.PncHomeVisitActivity;
import org.smartregister.chw.activity.PncMemberProfileActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.fragment.CorePncRegisterFragment;
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
    public void initializePresenter() {
        super.initializePresenter();
    }
}
