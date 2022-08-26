package org.smartregister.chw.activity;

import org.smartregister.chw.core.activity.CoreCdpRegisterActivity;
import org.smartregister.chw.core.fragment.CoreCdpRegisterFragment;
import org.smartregister.chw.core.fragment.CoreOrdersRegisterFragment;
import org.smartregister.view.fragment.BaseRegisterFragment;

import androidx.fragment.app.Fragment;

public class CdpRegisterActivity extends CoreCdpRegisterActivity {

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new CoreCdpRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[]{
                new CoreOrdersRegisterFragment()
        };
    }
}
