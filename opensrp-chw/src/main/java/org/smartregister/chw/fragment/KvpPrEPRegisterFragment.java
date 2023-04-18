package org.smartregister.chw.fragment;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.KvpPrEPProfileActivity;
import org.smartregister.chw.core.fragment.CoreKvpRegisterFragment;

public class KvpPrEPRegisterFragment extends CoreKvpRegisterFragment {

    @Override
    protected void openProfile(String baseEntityId) {
        KvpPrEPProfileActivity.startProfileActivity(requireActivity(), baseEntityId);
    }

    @Override
    protected int getTitleString() {
        return R.string.menu_kvp;
    }
}
