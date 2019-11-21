package org.smartregister.brac.hnpp.model;

import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.chw.core.model.NavigationOption;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.brac.hnpp.R;

import java.util.ArrayList;
import java.util.List;

public class HnppNavigationModel implements org.smartregister.chw.core.model.NavigationModel.Flavor {
    private List<NavigationOption> navigationOptions = new ArrayList<>();

    @Override
    public List<NavigationOption> getNavigationItems() {
        if (navigationOptions.size() == 0) {
            navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, R.string.menu_all_families, CoreConstants.DrawerMenu.ALL_FAMILIES, 0));
            navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, R.string.menu_all_member, CoreConstants.DrawerMenu.ALL_MEMBER, 0));
            //navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, R.string.menu_elco_clients, CoreConstants.DrawerMenu.ELCO_CLIENT, 0));
            navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_anc, R.mipmap.sidemenu_anc_active, R.string.menu_anc_clients, CoreConstants.DrawerMenu.ANC, 0));
            navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_pnc, R.mipmap.sidemenu_pnc_active, R.string.menu_pnc_clients, CoreConstants.DrawerMenu.PNC, 0));
            navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_children, R.mipmap.sidemenu_children_active, R.string.menu_child_clients, CoreConstants.DrawerMenu.CHILD_CLIENTS, 0));
        }

        return navigationOptions;
    }
}
