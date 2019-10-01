package org.smartregister.brac.hnpp.activity;

import android.content.Intent;

import org.smartregister.brac.hnpp.BuildConfig;
import org.smartregister.brac.hnpp.listener.HfFamilyBottomNavListener;
import org.smartregister.chw.core.activity.CoreChildRegisterActivity;
import org.smartregister.brac.hnpp.fragment.HnppChildRegisterFragment;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class ChildRegisterActivity extends CoreChildRegisterActivity {
    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new HnppChildRegisterFragment();
    }

    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, FamilyRegisterActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();

        if (!BuildConfig.SUPPORT_QR) {
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_scan_qr);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new HfFamilyBottomNavListener(this, bottomNavigationView));
    }
}
