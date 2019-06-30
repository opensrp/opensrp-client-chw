package org.smartregister.chw.custom_view;

public class DefaultNavigationMenuFlv implements NavigationMenu.Flavour {
    @Override
    public String[] getSupportedLanguages() {
        return new String[]{"English", "Française"};
    }
}
