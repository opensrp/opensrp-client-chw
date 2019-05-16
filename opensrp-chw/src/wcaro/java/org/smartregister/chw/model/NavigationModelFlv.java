package org.smartregister.chw.model;

import org.smartregister.chw.R;
import org.smartregister.chw.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class NavigationModelFlv {

    private static List<NavigationOption> navigationOptions = new ArrayList<>();

    public static List<NavigationOption> getNavigationItems() {

        if (navigationOptions.size() == 0) {
            NavigationOption op1 = new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, R.string.menu_all_families, Constants.DrawerMenu.ALL_FAMILIES, 0);
            NavigationOption op2 = new NavigationOption(R.mipmap.sidemenu_children, R.mipmap.sidemenu_children_active, R.string.menu_child_clients, Constants.DrawerMenu.CHILD_CLIENTS, 0);

            navigationOptions.addAll(asList(op1, op2));
        }

        return navigationOptions;
    }
}
