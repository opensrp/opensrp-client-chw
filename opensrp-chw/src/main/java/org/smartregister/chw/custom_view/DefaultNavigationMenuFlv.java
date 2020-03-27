package org.smartregister.chw.custom_view;

import org.smartregister.chw.core.custom_views.NavigationMenu;

import java.util.HashMap;

public abstract class DefaultNavigationMenuFlv implements NavigationMenu.Flavour {
    @Override
    public String[] getSupportedLanguages() {
        return new String[]{"English", "Français"};
    }

    @Override
    public HashMap<String, String> getTableMapValues() {
        return new HashMap<>();
    }

    @Override
    public boolean stockReport() {
        return false;
    }

    @Override
    public boolean serviceReport() {
        return false;
    }

}
