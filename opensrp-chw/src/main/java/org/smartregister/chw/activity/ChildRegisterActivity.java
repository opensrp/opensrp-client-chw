package org.smartregister.chw.activity;


import android.content.Intent;
import android.support.design.bottomnavigation.LabelVisibilityMode;

import com.opensrp.chw.core.activity.CoreChildRegisterActivity;
import com.opensrp.chw.core.contract.CoreChildRegisterContract;
import com.opensrp.chw.core.custom_views.NavigationMenu;
import com.opensrp.chw.core.utils.Utils;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.fragment.ChildRegisterFragment;
import org.smartregister.chw.listener.ChwBottomNavigationListener;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Arrays;
import java.util.List;

public class ChildRegisterActivity extends CoreChildRegisterActivity implements CoreChildRegisterContract.View {

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new ChildRegisterFragment();
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
        FamilyRegisterActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
    }

}
