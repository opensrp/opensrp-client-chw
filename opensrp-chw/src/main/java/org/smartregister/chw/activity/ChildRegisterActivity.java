package org.smartregister.chw.activity;


import android.content.Intent;
import android.support.design.bottomnavigation.LabelVisibilityMode;

import com.opensrp.chw.core.activity.CoreChildRegisterActivity;
import com.opensrp.chw.core.contract.CoreChildRegisterContract;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.fragment.ChildRegisterFragment;
import org.smartregister.chw.listener.ChwBottomNavigationListener;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class ChildRegisterActivity extends CoreChildRegisterActivity implements CoreChildRegisterContract.View {

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new ChildRegisterFragment();
    }

    @Override
    protected void registerBottomNavigation() {

        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
            bottomNavigationView.getMenu().removeItem(com.opensrp.chw.core.R.id.action_clients);
            bottomNavigationView.getMenu().removeItem(com.opensrp.chw.core.R.id.action_register);
            bottomNavigationView.getMenu().removeItem(com.opensrp.chw.core.R.id.action_search);
            bottomNavigationView.getMenu().removeItem(com.opensrp.chw.core.R.id.action_library);

            bottomNavigationView.inflateMenu(com.opensrp.chw.core.R.menu.bottom_nav_family_menu);

            bottomNavigationHelper.disableShiftMode(bottomNavigationView);

            ChwBottomNavigationListener childBottomNavigationListener = new ChwBottomNavigationListener(this);
            bottomNavigationView.setOnNavigationItemSelectedListener(childBottomNavigationListener);

        }

        if (!BuildConfig.SUPPORT_QR) {
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_scan_qr);
        }
    }

    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, FamilyRegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
