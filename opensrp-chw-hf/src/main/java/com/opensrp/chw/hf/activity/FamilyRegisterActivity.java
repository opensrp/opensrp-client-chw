package com.opensrp.chw.hf.activity;

import android.os.Bundle;

import com.opensrp.chw.core.activity.CoreFamilyRegisterActivity;
import com.opensrp.chw.core.custom_views.NavigationMenu;
import com.opensrp.chw.core.utils.CoreConstants;
import com.opensrp.chw.hf.HealthFacilityApplication;
import com.opensrp.chw.hf.fragement.FamilyRegisterFragment;
import com.opensrp.chw.hf.listener.HfFamilyBottomNavListener;
import com.opensrp.hf.BuildConfig;

import org.smartregister.view.fragment.BaseRegisterFragment;

public class FamilyRegisterActivity extends CoreFamilyRegisterActivity {

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();

        if (!BuildConfig.SUPPORT_QR) {
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_scan_qr);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new HfFamilyBottomNavListener(this, bottomNavigationView));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
        HealthFacilityApplication.getInstance().notifyAppContextChange(); // initialize the language (bug in translation)

        action = getIntent().getStringExtra(CoreConstants.ACTIVITY_PAYLOAD.ACTION);
        if (action != null && action.equals(CoreConstants.ACTION.START_REGISTRATION)) {
            startRegistration();
        }
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new FamilyRegisterFragment();
    }
}
