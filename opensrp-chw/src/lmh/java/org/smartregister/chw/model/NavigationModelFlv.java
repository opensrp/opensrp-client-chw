package org.smartregister.chw.model;

import org.smartregister.chw.R;
import org.smartregister.chw.core.model.NavigationOption;
import org.smartregister.chw.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class NavigationModelFlv extends DefaultNavigationModelFlv {

    private final List<NavigationOption> navigationOptions = new ArrayList<>();

    @Override
    public List<NavigationOption> getNavigationItems() {

        if (navigationOptions.size() == 0) {
            navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families, R.string.menu_all_families, Constants.DrawerMenu.ALL_FAMILIES, 0));
            navigationOptions.add(new NavigationOption(R.drawable.ic_add_new_family_white, R.drawable.ic_add_new_family_white, R.string.menu_add_new_family, Constants.DrawerMenu.ADD_NEW_FAMILY, -1));
            navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_children, R.mipmap.sidemenu_children, R.string.menu_child_clients, Constants.DrawerMenu.CHILD_CLIENTS, 0));
            navigationOptions.add(new NavigationOption(R.drawable.ic_reports, R.drawable.ic_reports, R.string.reports_dashboard, Constants.DrawerMenu.REPORTS, -1));
        }

        return navigationOptions;
    }
}