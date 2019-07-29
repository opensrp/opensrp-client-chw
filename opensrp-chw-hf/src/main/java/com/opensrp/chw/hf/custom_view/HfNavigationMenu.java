package com.opensrp.chw.hf.custom_view;

import com.opensrp.chw.core.custom_views.NavigationMenu;

public class HfNavigationMenu implements NavigationMenu.Flavour {
    @Override
    public String[] getSupportedLanguages() {
        return new String[]{"English", "Kiswahili"};
    }
}
