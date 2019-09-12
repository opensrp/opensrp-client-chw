package org.smartregister.chw.hf.fragment;

import android.view.View;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.fragment.CorePncRegisterFragment;
import org.smartregister.chw.hf.activity.PncMemberProfileActivity;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class PncRegisterFragment extends CorePncRegisterFragment {

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        dueOnlyLayout.setVisibility(View.GONE);
    }

    @Override
    protected void openHomeVisit(CommonPersonObjectClient client) {
        // Overridden
    }

    @Override
    protected void openPncMemberProfile(CommonPersonObjectClient client) {
        PncMemberProfileActivity.startMe(getActivity(), new MemberObject(client), getFamilyName(), getFamilyHeadPhone());
    }
}
