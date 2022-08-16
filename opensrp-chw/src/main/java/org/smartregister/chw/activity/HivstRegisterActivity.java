package org.smartregister.chw.activity;

import org.smartregister.chw.core.activity.CoreHivstRegisterActivity;
import org.smartregister.chw.core.fragment.CoreHivstRegisterFragment;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class HivstRegisterActivity extends CoreHivstRegisterActivity {
    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new CoreHivstRegisterFragment();
    }
}
