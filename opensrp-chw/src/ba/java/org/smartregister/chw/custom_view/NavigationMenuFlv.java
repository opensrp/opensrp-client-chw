package org.smartregister.chw.custom_view;

public class NavigationMenuFlv implements NavigationMenu.Flavour {
    @Override
    public String[] getSupportedLanguages() {
        return new String[]{"English", "Kiswahili"};
    }
}
