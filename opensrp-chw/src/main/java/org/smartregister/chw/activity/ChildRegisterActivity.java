package org.smartregister.chw.activity;

import org.smartregister.chw.core.activity.CoreChildRegisterActivity;
import org.smartregister.chw.core.contract.CoreChildRegisterContract;
import org.smartregister.chw.fragment.ChildRegisterFragment;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class ChildRegisterActivity extends CoreChildRegisterActivity implements CoreChildRegisterContract.View {

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
        FamilyRegisterActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new ChildRegisterFragment();
    }

}
