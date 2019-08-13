package org.smartregister.chw.activity;

import android.content.Intent;
import android.os.Bundle;

import org.smartregister.chw.core.contract.ChwBottomNavigator;
import org.smartregister.chw.core.custom_views.NavigationMenu;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.fragment.PncRegisterFragment;
import org.smartregister.chw.listener.AncBottomNavigationListener;
import org.smartregister.chw.pnc.activity.BasePncRegisterActivity;
import org.smartregister.chw.util.Constants;
import org.smartregister.view.fragment.BaseRegisterFragment;


public class PncRegisterActivity extends BasePncRegisterActivity implements ChwBottomNavigator {

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new PncRegisterFragment();
    }

    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, FamilyRegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();

        if (!BuildConfig.SUPPORT_QR) {
            bottomNavigationView.getMenu().removeItem(R.id.action_scan_qr);
        }

        AncBottomNavigationListener listener = new AncBottomNavigationListener(this, bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        NavigationMenu.getInstance(this, null, null).getNavigationAdapter()
                .setSelectedView(Constants.DrawerMenu.PNC);
    }

    @Override
    public void startFamilyRegistration() {
        FamilyRegisterActivity.startFamilyRegisterForm(this);
    }
}
