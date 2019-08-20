package org.smartregister.chw.hf.custom_view;

import org.smartregister.chw.core.custom_views.NavigationMenu;

public class HfNavigationMenu implements NavigationMenu.Flavour {
    @Override
    public String[] getSupportedLanguages() {
        return new String[]{"English", "Kiswahili"};
    }
}
