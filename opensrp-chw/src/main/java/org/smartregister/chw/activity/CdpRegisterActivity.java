package org.smartregister.chw.activity;

import androidx.annotation.MenuRes;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.cdp.util.Constants;
import org.smartregister.chw.core.activity.CoreCdpRegisterActivity;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.chw.fragment.CdpReceiveFromOrganizationsRegisterFragment;
import org.smartregister.chw.fragment.CdpRegisterFragment;
import org.smartregister.chw.fragment.OrdersRegisterFragment;
import org.smartregister.chw.listener.CdpBottomNavigationListener;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.listener.BottomNavigationListener;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class CdpRegisterActivity extends CoreCdpRegisterActivity {

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new CdpRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[]{
                new OrdersRegisterFragment(),
                new CdpReceiveFromOrganizationsRegisterFragment()
        };
    }

    @Override
    public void startOutletForm() {
        JSONObject form = FormUtils.getFormUtils().getFormJson(Constants.FORMS.CDP_OUTLET_REGISTRATION);
        startFormActivity(form, Constants.FORMS.CDP_OUTLET_REGISTRATION);
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
            bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_clients);
            bottomNavigationView.getMenu().removeItem(org.smartregister.cdp.R.id.action_register);
            bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_search);
            bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_library);
            bottomNavigationView.inflateMenu(getMenuResource());
            bottomNavigationHelper.disableShiftMode(bottomNavigationView);
            BottomNavigationListener familyBottomNavigationListener = new CdpBottomNavigationListener(this);
            bottomNavigationView.setOnNavigationItemSelectedListener(familyBottomNavigationListener);
        }
    }

    @Override
    @MenuRes
    public int getMenuResource() {
        return R.menu.cdp_bottom_nav_menu;
    }
}
