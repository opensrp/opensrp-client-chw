package org.smartregister.chw.activity;

import org.smartregister.chw.core.activity.CoreChildRegisterActivity;
import org.smartregister.chw.fragment.ChwChildRegisterFragment;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class ChwChildRegisterActivity extends CoreChildRegisterActivity {

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new ChwChildRegisterFragment();
    }
}
