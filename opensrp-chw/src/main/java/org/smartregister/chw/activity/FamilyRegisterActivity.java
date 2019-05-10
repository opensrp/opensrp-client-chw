package org.smartregister.chw.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.custom_view.NavigationMenu;
import org.smartregister.chw.fragment.FamilyRegisterFragment;
import org.smartregister.chw.listener.FamilyBottomNavigationListener;
import org.smartregister.chw.util.Constants;
import org.smartregister.family.activity.BaseFamilyRegisterActivity;
import org.smartregister.family.model.BaseFamilyRegisterModel;
import org.smartregister.family.presenter.BaseFamilyRegisterPresenter;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class FamilyRegisterActivity extends BaseFamilyRegisterActivity {

    @Override
    protected void initializePresenter() {
        presenter = new BaseFamilyRegisterPresenter(this, new BaseFamilyRegisterModel());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new FamilyRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();

        if (!BuildConfig.SUPPORT_QR) {
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_scan_qr);
        }

        FamilyBottomNavigationListener familyBottomNavigationListener = new FamilyBottomNavigationListener(this, bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(familyBottomNavigationListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
    }


    @Override
    protected void onResumption() {
        super.onResumption();
        NavigationMenu.getInstance(this, null, null).getNavigationAdapter()
                .setSelectedView(Constants.DrawerMenu.ALL_FAMILIES);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.RQ_CODE.STORAGE_PERMISIONS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            NavigationMenu navigationMenu = NavigationMenu.getInstance(this, null, null);
            if (navigationMenu != null) {
                navigationMenu.startP2PActivity(this);
            }
        }
    }
}
