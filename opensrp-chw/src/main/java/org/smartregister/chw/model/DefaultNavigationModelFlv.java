package org.smartregister.chw.model;


import com.opensrp.chw.core.R;
import com.opensrp.chw.core.model.NavigationModel;
import com.opensrp.chw.core.model.NavigationOption;
import com.opensrp.chw.core.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultNavigationModelFlv implements NavigationModel.Flavor {

    private List<NavigationOption> navigationOptions = new ArrayList<>();

    @Override
    public List<NavigationOption> getNavigationItems() {

        if (navigationOptions.size() == 0) {
            navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, R.string.menu_all_families, Constants.DrawerMenu.ALL_FAMILIES, 0));
            navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_anc, R.mipmap.sidemenu_anc_active, R.string.menu_anc, Constants.DrawerMenu.ANC, 0));
            navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_pnc, R.mipmap.sidemenu_pnc_active, R.string.menu_pnc, Constants.DrawerMenu.PNC, 0));
            navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_children, R.mipmap.sidemenu_children_active, R.string.menu_child_clients, Constants.DrawerMenu.CHILD_CLIENTS, 0));
        }

        return navigationOptions;
    }
}
