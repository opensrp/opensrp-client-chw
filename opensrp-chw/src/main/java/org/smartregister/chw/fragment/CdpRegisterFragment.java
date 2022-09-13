package org.smartregister.chw.fragment;

import org.smartregister.chw.activity.CdpProfileActivity;
import org.smartregister.chw.core.fragment.CoreCdpRegisterFragment;

public class CdpRegisterFragment extends CoreCdpRegisterFragment {

    @Override
    protected void openProfile(String baseEntityId) {
        CdpProfileActivity.startProfile(getActivity(), baseEntityId);
    }
}
