package org.smartregister.chw.core.activity;

import android.content.Intent;
import android.os.Bundle;

import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.helper.BottomNavigationHelper;

public abstract class CorePncRegisterActivity extends CoreAncRegisterActivity {

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
    }


    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, getFamilyRegisterActivity());
        startActivity(intent);
        finish();
    }

    protected abstract Class<? extends CoreFamilyRegisterActivity> getFamilyRegisterActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        NavigationMenu menu = NavigationMenu.getInstance(this, null, null);
        if (menu != null) {
            menu.getNavigationAdapter()
                    .setSelectedView(CoreConstants.DrawerMenu.PNC);
        }
    }
}
