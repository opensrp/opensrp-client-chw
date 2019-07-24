package org.smartregister.chw.custom_view;

import com.opensrp.chw.core.custom_views.NavigationMenu;

public abstract class DefaultNavigationMenuFlv implements NavigationMenu.Flavour {
    @Override
    public String[] getSupportedLanguages() {
        return new String[]{"English", "Français"};
    }
}
