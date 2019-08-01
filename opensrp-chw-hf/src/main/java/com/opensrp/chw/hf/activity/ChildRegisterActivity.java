package com.opensrp.chw.hf.activity;

import android.content.Intent;

import com.opensrp.chw.core.activity.CoreChildRegisterActivity;
import com.opensrp.chw.hf.fragement.ChildRegisterFragment;

import org.smartregister.view.fragment.BaseRegisterFragment;

public class ChildRegisterActivity extends CoreChildRegisterActivity {
    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new ChildRegisterFragment();
    }

    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, FamilyRegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
