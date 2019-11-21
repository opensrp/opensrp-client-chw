package org.smartregister.brac.hnpp.activity;

import android.content.Intent;

import org.smartregister.brac.hnpp.BuildConfig;
import org.smartregister.brac.hnpp.fragment.HnppAllMemberRegisterFragment;
import org.smartregister.brac.hnpp.listener.HnppFamilyBottomNavListener;
import org.smartregister.chw.core.activity.CoreChildRegisterActivity;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class HnppAllMemberRegisterActivity extends CoreChildRegisterActivity {
    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new HnppAllMemberRegisterFragment();
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
        bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_register);
        bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_job_aids);
        bottomNavigationView.setOnNavigationItemSelectedListener(new HnppFamilyBottomNavListener(this, bottomNavigationView));
    }
    @Override
    protected void onResumption() {
        super.onResumption();
        NavigationMenu menu = NavigationMenu.getInstance(this, null, null);
        if (menu != null) {
            menu.getNavigationAdapter().setSelectedView(CoreConstants.DrawerMenu.ALL_MEMBER);
        }
    }

}
