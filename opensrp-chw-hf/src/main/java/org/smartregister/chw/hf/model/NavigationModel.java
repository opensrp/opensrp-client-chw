package org.smartregister.chw.hf.model;

import org.smartregister.chw.core.model.NavigationOption;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.ArrayList;
import java.util.List;

public class NavigationModel implements org.smartregister.chw.core.model.NavigationModel.Flavor {
    private List<NavigationOption> navigationOptions = new ArrayList<>();

    @Override
    public List<NavigationOption> getNavigationItems() {
        if (navigationOptions.size() == 0) {
            navigationOptions.add(new NavigationOption(com.opensrp.chw.core.R.mipmap.sidemenu_families, com.opensrp.chw.core.R.mipmap.sidemenu_families_active, com.opensrp.chw.core.R.string.menu_all_clients, CoreConstants.DrawerMenu.ALL_CLIENTS, 0));
            navigationOptions.add(new NavigationOption(com.opensrp.chw.core.R.mipmap.sidemenu_families, com.opensrp.chw.core.R.mipmap.sidemenu_families_active, com.opensrp.chw.core.R.string.menu_all_families, CoreConstants.DrawerMenu.ALL_FAMILIES, 0));
            navigationOptions.add(new NavigationOption(com.opensrp.chw.core.R.mipmap.sidemenu_anc, com.opensrp.chw.core.R.mipmap.sidemenu_anc_active, com.opensrp.chw.core.R.string.menu_anc, CoreConstants.DrawerMenu.ANC, 0));
            navigationOptions.add(new NavigationOption(com.opensrp.chw.core.R.mipmap.sidemenu_pnc, com.opensrp.chw.core.R.mipmap.sidemenu_pnc_active, com.opensrp.chw.core.R.string.menu_pnc, CoreConstants.DrawerMenu.PNC, 0));
            navigationOptions.add(new NavigationOption(com.opensrp.chw.core.R.mipmap.sidemenu_children, com.opensrp.chw.core.R.mipmap.sidemenu_children_active, com.opensrp.chw.core.R.string.menu_child_clients, CoreConstants.DrawerMenu.CHILD_CLIENTS, 0));
            navigationOptions.add(new NavigationOption(com.opensrp.chw.core.R.mipmap.sidemenu_fp, com.opensrp.chw.core.R.mipmap.sidemenu_fp_active, com.opensrp.chw.core.R.string.menu_family_planning, CoreConstants.DrawerMenu.FAMILY_PLANNING, 0));
            navigationOptions.add(new NavigationOption(com.opensrp.chw.core.R.mipmap.sidemenu_malaria, com.opensrp.chw.core.R.mipmap.sidemenu_malaria_active, com.opensrp.chw.core.R.string.menu_malaria, CoreConstants.DrawerMenu.MALARIA, 0));
            navigationOptions.add(new NavigationOption(com.opensrp.chw.core.R.mipmap.sidemenu_referrals, com.opensrp.chw.core.R.mipmap.sidemenu_referrals_active, com.opensrp.chw.core.R.string.menu_referrals, CoreConstants.DrawerMenu.REFERRALS, 0));
        }

        return navigationOptions;
    }
}
