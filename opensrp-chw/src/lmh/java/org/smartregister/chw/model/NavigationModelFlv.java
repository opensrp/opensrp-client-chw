package org.smartregister.chw.model;

import org.smartregister.chw.R;
import org.smartregister.chw.core.model.NavigationOption;
import org.smartregister.chw.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class NavigationModelFlv extends DefaultNavigationModelFlv {

    private List<NavigationOption> navigationOptions = new ArrayList<>();

    @Override
    public List<NavigationOption> getNavigationItems() {

        if (navigationOptions.size() == 0) {
            navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, R.string.menu_all_families, Constants.DrawerMenu.ALL_FAMILIES, 0));
            navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_children, R.mipmap.sidemenu_children_active, R.string.all_children_title, Constants.DrawerMenu.CHILD_CLIENTS, 0));
        }

        return navigationOptions;
    }
}