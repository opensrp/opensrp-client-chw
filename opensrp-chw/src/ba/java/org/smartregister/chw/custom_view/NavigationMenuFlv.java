package org.smartregister.chw.custom_view;

import org.smartregister.chw.core.custom_views.NavigationMenu;

public class NavigationMenuFlv implements NavigationMenu.Flavour {
    @Override
    public String[] getSupportedLanguages() {
        return new String[]{"English", "Kiswahili"};
    }
}
