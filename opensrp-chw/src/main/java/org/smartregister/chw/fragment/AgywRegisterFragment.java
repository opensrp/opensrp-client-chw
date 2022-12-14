package org.smartregister.chw.fragment;

import org.smartregister.chw.activity.AgywProfileActivity;
import org.smartregister.chw.core.fragment.CoreAgywRegisterFragment;

public class AgywRegisterFragment extends CoreAgywRegisterFragment {

    @Override
    protected void openProfile(String baseEntityId) {
        AgywProfileActivity.startProfile(requireActivity(), baseEntityId);
    }
}
