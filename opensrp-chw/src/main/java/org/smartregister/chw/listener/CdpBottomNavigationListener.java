package org.smartregister.chw.listener;

import android.app.Activity;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import org.smartregister.chw.R;
import org.smartregister.chw.cdp.activity.BaseCdpRegisterActivity;
import org.smartregister.chw.cdp.listener.BaseCdpBottomNavigationListener;

public class CdpBottomNavigationListener extends BaseCdpBottomNavigationListener {
    private final Activity context;

    public CdpBottomNavigationListener(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        super.onNavigationItemSelected(item);

        BaseCdpRegisterActivity baseRegisterActivity = (BaseCdpRegisterActivity) context;
        int itemId = item.getItemId();
        if (itemId == R.id.action_home) {
            baseRegisterActivity.switchToBaseFragment();
        } else if (itemId == R.id.action_order_receive) {
            baseRegisterActivity.switchToFragment(1);
        } else if (itemId == R.id.action_receive_from_msd) {
            baseRegisterActivity.switchToFragment(2);
        } else if (itemId == R.id.action_add_outlet) {
            baseRegisterActivity.startOutletForm();
            baseRegisterActivity.switchToBaseFragment();
        }

        return true;
    }
}
