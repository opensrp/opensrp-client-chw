package org.smartregister.chw.fragment;

import org.smartregister.chw.activity.KvpPrEPProfileActivity;
import org.smartregister.chw.core.fragment.CoreKvpRegisterFragment;

public class KvpPrEPRegisterFragment extends CoreKvpRegisterFragment {

    @Override
    protected void openProfile(String baseEntityId) {
        KvpPrEPProfileActivity.startProfileActivity(requireActivity(), baseEntityId);
    }
}
