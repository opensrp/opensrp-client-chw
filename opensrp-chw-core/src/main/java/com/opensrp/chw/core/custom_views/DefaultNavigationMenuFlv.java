package com.opensrp.chw.core.custom_views;

public abstract class DefaultNavigationMenuFlv implements NavigationMenu.Flavour {
    @Override
    public String[] getSupportedLanguages() {
        return new String[]{"English", "Français"};
    }
}
