package org.smartregister.chw.custom_view;

import org.smartregister.chw.core.custom_views.NavigationMenu;

public abstract class DefaultNavigationMenuFlv implements NavigationMenu.Flavour {
    @Override
    public String[] getSupportedLanguages() {
        return new String[]{"English", "Français"};
    }
}
