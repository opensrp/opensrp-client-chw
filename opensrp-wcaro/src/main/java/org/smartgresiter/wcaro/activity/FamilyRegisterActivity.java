package org.smartgresiter.wcaro.activity;

import android.support.v4.app.Fragment;

import org.smartgresiter.wcaro.BuildConfig;
import org.smartgresiter.wcaro.fragment.FamilyRegisterFragment;
import org.smartgresiter.wcaro.model.FamilyRegisterModel;
import org.smartgresiter.wcaro.presenter.FamilyRegisterPresenter;
import org.smartregister.family.activity.BaseFamilyRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class FamilyRegisterActivity extends BaseFamilyRegisterActivity {

    @Override
    protected void initializePresenter() {
        presenter = new FamilyRegisterPresenter(this, new FamilyRegisterModel());
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

        if (!BuildConfig.SCAN_QR_CODE) {
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_scan_qr);
        }
    }

}
