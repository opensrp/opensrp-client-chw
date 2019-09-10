package org.smartregister.chw.hf.fragment;

import org.smartregister.chw.core.fragment.CorePncRegisterFragment;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class PncRegisterFragment extends CorePncRegisterFragment {

    @Override
    protected void openHomeVisit(CommonPersonObjectClient client) {
      //  PncHomeVisitActivity.startMe(getActivity(), new MemberObject(client), false);
    }

    @Override
    protected void openPncMemberProfile(CommonPersonObjectClient client) {
//        PncMemberProfileActivity.startMe(getActivity(), new MemberObject(client), getFamilyName(), getFamilyHeadPhone());
    }

}
